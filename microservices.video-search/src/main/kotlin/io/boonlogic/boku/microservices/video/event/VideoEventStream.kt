package io.boonlogic.boku.microservices.video.event

import io.boonlogic.boku.event.VideoEvents
import org.slf4j.LoggerFactory
import org.springframework.cloud.stream.annotation.EnableBinding
import org.springframework.cloud.stream.annotation.StreamListener
import org.springframework.cloud.stream.messaging.Sink

@EnableBinding(Sink::class)
class VideoEventStream() {

    val log = LoggerFactory.getLogger(VideoEventStream::class.java)

    @StreamListener(Sink.INPUT)
    fun listen(evt: VideoEvents.VideoSearchEvent) {
        log.info("search event received: {}", evt.id)
    }
}
