package com.example.sqs

import io.awspring.cloud.sqs.annotation.SqsListener
import io.awspring.cloud.sqs.config.SqsBootstrapConfiguration
import io.awspring.cloud.sqs.config.SqsMessageListenerContainerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.web.util.UriComponentsBuilder
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.regions.Region.AP_NORTHEAST_2
import software.amazon.awssdk.services.sqs.SqsAsyncClient


@Import(SqsBootstrapConfiguration::class)
@Configuration
class SqsConfiguration {

    @Bean
    fun defaultSqsListenerContainerFactory(): SqsMessageListenerContainerFactory<Any> {
        return SqsMessageListenerContainerFactory
            .builder<Any>()
            .sqsAsyncClient(
                SqsAsyncClient.builder()
                    .region(AP_NORTHEAST_2)
                    .credentialsProvider(
                        StaticCredentialsProvider.create(
                            AwsBasicCredentials.create(
                                "accessKey",
                                "secretAccessKey",
                            )
                        )
                    )
                    .endpointOverride(
                        UriComponentsBuilder.newInstance()
                            .scheme("https")
                            .host("host")
                            .path("path")
                            .build().toUri()
                    )
                    .build()
            )
            .build()
    }

    @SqsListener("queue-name")
    fun loadMessagesFromQueue(message: String) {
        println("Queue Messages: $message")
    }
}