package io.boonlogic.boku.microservices.video.web.handlers

// import io.boonlogic.boku.event.messaging.toSpringCloudStreamMessage
import com.google.protobuf.util.JsonFormat
import io.boonlogic.boku.event.VideoEvents

import io.boonlogic.boku.microservices.video.domain.Metadata
import io.boonlogic.boku.microservices.video.domain.VideoSearchCreationResponse
import io.boonlogic.boku.microservices.video.domain.VideoSearchRequest
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
import org.springframework.http.MediaType
import org.springframework.messaging.support.MessageBuilder
import org.springframework.web.reactive.function.server.EntityResponse
import java.util.UUID

@EnableBinding(Source::class)
@RefreshScope
@Component
class ConfigHandler(
    @Value("\${configuration.project-name}") private val projectName: String,
    @Value("\${info.foo}") private val foo: String,
    private val metadata: List<Metadata>,
    private val eventStreamSource: Source
) {
    private val log = LoggerFactory.getLogger(ConfigHandler::class.java)

    fun name(req: ServerRequest): Mono<ServerResponse> =
        ServerResponse.ok().body(projectName.toMono(), String::class.java)

    fun foo(req: ServerRequest) =
        ServerResponse.ok().body(foo.toMono(), String::class.java)

    fun metadata(req: ServerRequest): Mono<ServerResponse> {
        return  ServerResponse.ok().body(metadata.toMono(),
            object : ParameterizedTypeReference<List<io.boonlogic.boku.microservices.video.domain.Metadata>>() {})
    }

    fun submitVideoSearchRequest(req: ServerRequest): Mono<ServerResponse> {

        val searchRequest = req.bodyToMono(VideoSearchRequest::class.java)
        val id = UUID.randomUUID().toString()

        val result = searchRequest.flatMap {

            val evt = VideoEvents.VideoSearchEvent.newBuilder()
                .setId(id)
                .setTitle(it.category)
                .setDesc(it.searchQuery)
                .build()

            val json = JsonFormat.printer().print(evt)

            // workStream.output().send(evt.toSpringCloudStreamMessage())
            eventStreamSource.output().send(MessageBuilder.withPayload(json).build())

            log.info("vidoe request sent => {}", evt)
            log.info("vidoe request sent (json) => {}", json)

            VideoSearchCreationResponse(id).toMono()
        }

        return ServerResponse.ok()
            .contentType(MediaType.APPLICATION_JSON)
            .body(result, VideoSearchCreationResponse::class.java)
    }
}
