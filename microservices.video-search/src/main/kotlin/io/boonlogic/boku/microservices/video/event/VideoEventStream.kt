package io.boonlogic.boku.microservices.video.event

import io.boonlogic.boku.microservices.video.event.WorkProtos.WorkEvent
import org.slf4j.LoggerFactory
import org.springframework.cloud.stream.annotation.EnableBinding
import org.springframework.cloud.stream.annotation.StreamListener
import org.springframework.cloud.stream.messaging.Sink

@EnableBinding(Sink::class)
class WorkEventStream() {

    val log = LoggerFactory.getLogger(WorkEventStream::class.java)

    @StreamListener(Sink.INPUT)
    fun listen(evt: WorkEvent) {
        log.info("search event received: {}", evt.id)
    }
}
