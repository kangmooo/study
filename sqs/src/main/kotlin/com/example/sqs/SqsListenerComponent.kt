package com.example.sqs

import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class SqsListenerComponent {


    @GetMapping("/test")
    fun test(){
        println("testsetsetsetsetsetstetsetsete")
    }
    @SqsListener("test-queue.fifo")
    fun loadMessagesFromQueue(message: String) {
        println("Queue Messages: $message")
        Thread.sleep((60 * 10 * 1000).toLong())
    }
}