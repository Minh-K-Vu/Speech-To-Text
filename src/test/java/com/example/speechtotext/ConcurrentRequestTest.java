package com.example.speechtotext;
//This test checks if the server can handle 210 requests at the same time.
//why this test:
//The assignment requires the server to handle more than 200 requests.
//Expected result:
//- All 210 requests should return status 200.
//- Each request should return "test transcription".
//- The requests should finish within 10 seconds.
//The transcription service is mocked so OpenAI is not called.
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.multipart.MultipartFile;
import com.example.speechtotext.service.TranscriptionService;
import reactor.core.publisher.Mono;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class ConcurrentRequestTest {

    // Spring gives the test server a random available port
    @LocalServerPort
    private int port;

    // Use a fake transcription service instead of the real OpenAI service
    @MockitoBean
    private TranscriptionService transcriptionService;

    @Test
    void serverHandles210Requests() throws Exception {

        // Pretend the transcription service takes 500ms to respond
        when(transcriptionService.transcribe(any(MultipartFile.class)))
                .thenReturn(
                        Mono.delay(Duration.ofMillis(500))
                                .thenReturn("test transcription")
                );
        int requestCount = 210;
        // Used to send HTTP requests to the server
        HttpClient client = HttpClient.newHttpClient();
        // Create enough threads for all 210 requests
        ExecutorService threadPool =
                Executors.newFixedThreadPool(requestCount);
        /*
         * This works like a starting gate.
         * All threads wait until startSignal is released.
         */
        CountDownLatch startSignal = new CountDownLatch(1);
        List<Future<HttpResponse<String>>> requests =
                new ArrayList<>();
        // Prepare all 210 requests
        for (int i = 0; i < requestCount; i++) {
            requests.add(
                    threadPool.submit(() -> {
                        // Wait until all requests are ready
                        startSignal.await();
                        // Send the request and wait for the response
                        return client.send(
                                createRequest(),
                                HttpResponse.BodyHandlers.ofString()
                        );
                    })
            );
        }
        long startTime = System.currentTimeMillis();
        // Let all 210 requests start
        startSignal.countDown();
        // Check every response
        for (Future<HttpResponse<String>> request : requests) {
            HttpResponse<String> response = request.get();
            // The request should succeed
            assertEquals(200, response.statusCode());
            // The fake transcription should be returned
            assertEquals("test transcription", response.body());
        }
        long totalTime =
                System.currentTimeMillis() - startTime;
        threadPool.shutdown();
        // Make sure the requests did not take too long
        assertTrue(
                totalTime < 10000,
                "Requests took too long"
        );
    }
    // Creates a fake audio file upload request
    private HttpRequest createRequest() {
        String boundary = "TestBoundary";
        // Create the multipart body similar to what the browser sends
        String body =
                "--" + boundary + "\r\n"
                + "Content-Disposition: form-data; "
                + "name=\"file\"; filename=\"recording.webm\"\r\n"
                + "Content-Type: audio/webm\r\n\r\n"
                + "fake audio\r\n"
                + "--" + boundary + "--\r\n";
        return HttpRequest.newBuilder()
                .uri(URI.create(
                        "http://localhost:"
                        + port
                        + "/api/speechtotext/transcribe"
                ))
                .header(
                        "Content-Type",
                        "multipart/form-data; boundary=" + boundary
                )
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();
    }
}