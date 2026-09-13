package com.example.speechtotext.controller;

//This test checks the transcription controller without calling the real OpenAI API.
//Why this test:
//The controller receives an audio file from the webpage and should return the
//transcription given to it by TranscriptionService. I want to make sure that
//uploading the file and returning the result still works if the code is changed later.
//Expected result:
//- The controller accepts a multipart file called "file".
//- The request starts asynchronously because the service returns a Mono.
//- The HTTP response should be 200 OK.
//- The response text should be "This is a test transcription."
//The real TranscriptionService is replaced with a mock so the test does not
//use an OpenAI API key or make a real network request.

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.multipart.MultipartFile;

import com.example.speechtotext.service.TranscriptionService;

import reactor.core.publisher.Mono;


@WebMvcTest(TranscriptionController.class)
class TranscriptionControllerTest {

    // Used to send fake HTTP requests to the controller
    @Autowired
    private MockMvc mockMvc;

    // Use a fake TranscriptionService instead of calling OpenAI
    @MockitoBean
    private TranscriptionService transcriptionService;


    @Test
    void transcribeReturnsTextFromService() throws Exception {

        // Create a fake audio file similar to what the browser sends
        MockMultipartFile audioFile = new MockMultipartFile(
                "file",
                "recording.webm",
                "audio/webm",
                "fake audio".getBytes()
        );

        // Pretend that the transcription service returned this text
        when(
                transcriptionService.transcribe(
                        any(MultipartFile.class)
                )
        ).thenReturn(
                Mono.just("This is a test transcription.")
        );

        // Send the fake audio file to the transcription endpoint
        MvcResult result = mockMvc.perform(
                multipart("/api/speechtotext/transcribe")
                        .file(audioFile)
        )
        .andExpect(request().asyncStarted())
        .andReturn();

        // Get the result after the asynchronous request finishes
        mockMvc.perform(
                asyncDispatch(result)
        )
        .andExpect(status().isOk())
        .andExpect(
                content().string(
                        "This is a test transcription."
                )
        );
    }
}