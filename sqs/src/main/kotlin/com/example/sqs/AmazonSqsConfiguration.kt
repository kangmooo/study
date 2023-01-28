package com.example.sqs

import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.services.sqs.AmazonSQSAsyncClientBuilder
import org.springframework.beans.factory.annotation.Value
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
class AmazonSQSConfiguration (
    @Value("\${cloud.aws.credentials.access-key}") val accessKey: String,
    @Value("\${cloud.aws.credentials.secret-key}") val secretKey: String,
    @Value("\${cloud.aws.region.static}") val region: String,
){
    @Bean
    fun queueMessagingTemplate(): QueueMessagingTemplate {
        return QueueMessagingTemplate(
            AmazonSQSAsyncClientBuilder.standard()
                .withRegion(region)
                .withCredentials(AWSStaticCredentialsProvider(BasicAWSCredentials(accessKey, secretKey)))
                .build()
        )
    }
}