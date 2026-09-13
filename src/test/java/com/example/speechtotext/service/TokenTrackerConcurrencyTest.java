package com.example.speechtotext.service;
//This test checks that TokenTracker is safe when lots of threads
//update the token counts at the same time.
//Why this test:
//The server can handle many transcription requests at once.
//Each request may update the token totals, so two or more threads
//could try to update the same value at the same time.
//Expected result:
//- 100 threads are created.
//- Each thread updates the token count 1000 times.
//- The final input token count should be 100000.
//- The final output token count should be 100000.
//If some updates are lost, then there is probably a race condition.
//

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.jupiter.api.Test;

import com.example.speechtotext.dto.Stats;


class TokenTrackerConcurrencyTest {
    @Test
    void tokenCountsStayCorrectWhenUpdatedAtSameTime() throws Exception {
        TokenTracker tokenTracker = new TokenTracker();
        int threadCount = 100;
        int updatesEachThread = 1000;
        // Create 100 threads
        ExecutorService threadPool =
                Executors.newFixedThreadPool(threadCount);
        List<Future<?>> testTasks = new ArrayList<>();
        // Give each thread the same job
        for (int i = 0; i < threadCount; i++) {
            Future<?> testTask = threadPool.submit(() -> {
                for (int j = 0; j < updatesEachThread; j++) {
                    tokenTracker.addUsage(1, 1);
                }
            });
            testTasks.add(testTask);
        }
        // Wait until every thread finishes
        for (Future<?> testTask : testTasks) {
            testTask.get();
        }
        threadPool.shutdown();
        Stats stats = tokenTracker.getStats();
        long expectedCount =
                (long) threadCount * updatesEachThread;
        // Check that all updates were counted
        assertEquals(
                expectedCount,
                stats.inputTokens()
        );
        assertEquals(
                expectedCount,
                stats.outputTokens()
        );
    }
}