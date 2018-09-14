package io.boonlogic.boku.event.stream.autoconfigure

import io.boonlogic.boku.event.messaging.converter.ProtobufMessageConverter

import org.slf4j.LoggerFactory
import org.springframework.cloud.stream.annotation.StreamMessageConverter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.messaging.converter.MessageConverter

@Configuration
class EventStreamAutoConfiguration {

    private val log = LoggerFactory.getLogger(EventStreamAutoConfiguration::class.java)

    @Bean
    @StreamMessageConverter
    fun protobufMessageConverter(): MessageConverter = ProtobufMessageConverter()

}
