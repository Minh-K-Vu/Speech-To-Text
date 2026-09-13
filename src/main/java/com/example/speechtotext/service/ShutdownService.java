package com.example.speechtotext.service;

import java.util.concurrent.atomic.AtomicBoolean;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Service;

@Service
public class ShutdownService {
    //Access to Spring application
    private ConfigurableApplicationContext applicationContext;
    //Use atomic to handle concurrent requests
    private AtomicBoolean shutdownRequested =
            new AtomicBoolean(false);
    public ShutdownService(
            ConfigurableApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }
    public boolean requestShutdown() {
        // set shutdownRequeted from false to true
        boolean accepted =
                shutdownRequested.compareAndSet(false, true);
        //Now if other shutdown request come this will return false
        if (!accepted) {
            return false;
        }
        //Create a new thread to handle shutdown.
        Thread shutdownThread = new Thread(() -> {
            try {
            	//Give some time for the requests to be sent
                Thread.sleep(250);

            } catch (InterruptedException error) {

                Thread.currentThread().interrupt();
            }
            //Close the spring application
            applicationContext.close();
        });
        shutdownThread.setName("graceful-shutdown-thread");
        shutdownThread.start();
        return true;
    }
}