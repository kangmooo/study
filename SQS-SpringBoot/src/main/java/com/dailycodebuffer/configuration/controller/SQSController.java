package com.dailycodebuffer.configuration.controller;

import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SQSController {

    @SqsListener("test-queue.fifo")
    public void loadMessagesFromQueue(String message) throws InterruptedException {
        System.out.println("Queue Messages: " + message);
        Thread.sleep(60*10*1000);
    }
}
