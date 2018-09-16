package io.boonlogic.boku.microservices.video.web.handlers

// import io.boonlogic.boku.event.messaging.toSpringCloudStreamMessage
import com.google.protobuf.util.JsonFormat
import io.boonlogic.boku.microservices.video.event.WorkProtos
import io.boonlogic.boku.microservices.video.event.WorkProtos.WorkEvent

import io.boonlogic.boku.microservices.video.domain.Metadata
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.cloud.context.config.annotation.RefreshScope
import org.springframework.core.ParameterizedTypeReference
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono
import reactor.core.publisher.toMono

import org.springframework.cloud.stream.annotation.EnableBinding
import org.springframework.cloud.stream.messaging.Source
import org.springframework.messaging.support.MessageBuilder

@EnableBinding(Source::class)
@RefreshScope
@Component
class ConfigHandler(
    @Value("\${configuration.project-name}") private val projectName: String,
    @Value("\${info.foo}") private val foo: String,
    private val workStream: Source,
    private val metadata: List<Metadata>
) {
    private val log = LoggerFactory.getLogger(ConfigHandler::class.java)

    fun name(req: ServerRequest): Mono<ServerResponse> {
        log.debug("ok ok")
        log.info("info ok")
        return  ServerResponse.ok().body(projectName.toMono(), String::class.java)
    }

    fun foo(req: ServerRequest) =
        ServerResponse.ok().body(foo.toMono(), String::class.java)

    fun metadata(req: ServerRequest): Mono<ServerResponse> {
        log.debug("ok ok")
        log.info("info ok")
        return  ServerResponse.ok().body(metadata.toMono(),
            object : ParameterizedTypeReference<List<io.boonlogic.boku.microservices.video.domain.Metadata>>() {})
    }

    fun submitWorkRequest() {
        val id = Math.random().toString()

        val evt = WorkProtos.WorkEvent.newBuilder()
            .setId(id)
            .setDesc("hello")
            .build()

        val json = JsonFormat.printer().print(evt)
        // workStream.output().send(evt.toSpringCloudStreamMessage())
        workStream.output().send(MessageBuilder.withPayload(json).build())
        log.info("work request sent => {}", evt)
        log.info("work request sent (json) => {}", json)
    }
}
