package io.boonlogic.boku.microservices.video

import org.springframework.cloud.stream.annotation.EnableBinding
import org.springframework.cloud.stream.annotation.StreamListener
import org.springframework.cloud.stream.messaging.Sink
import org.springframework.stereotype.Component

@EnableBinding(Sink::class)
@Component
class MessageReceiver {

    @StreamListener(Sink.INPUT)
    fun receive(message: String) {
        println("Received <${message}>")
    }

}