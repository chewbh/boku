package io.boonlogic.boku.microservices.video.web.handlers

// import io.boonlogic.boku.event.messaging.toSpringCloudStreamMessage
import com.google.protobuf.util.JsonFormat
import io.boonlogic.boku.event.VideoEvents

import io.boonlogic.boku.microservices.video.domain.Metadata
import io.boonlogic.boku.microservices.video.domain.VideoSearchCreationResponse
import io.boonlogic.boku.microservices.video.domain.VideoSearchRequest
import io.micrometer.core.instrument.Counter
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.Timer
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
import java.time.Duration
import java.util.Random
import java.util.UUID
import java.util.concurrent.TimeUnit

@EnableBinding(Source::class)
@RefreshScope
@Component
class ConfigHandler(
    @Value("\${configuration.project-name}") private val projectName: String,
    @Value("\${info.foo}") private val foo: String,
    private val metadata: List<Metadata>,
    private val eventStreamSource: Source,
    private val registry: MeterRegistry
) {
    private val log = LoggerFactory.getLogger(ConfigHandler::class.java)



    fun name(req: ServerRequest): Mono<ServerResponse> =
        ServerResponse.ok().body(projectName.toMono(), String::class.java)

    fun foo(req: ServerRequest): Mono<ServerResponse> {
        return ServerResponse.ok().body(foo.toMono().map {

            val type = if(Random().nextBoolean()) "adhoc" else "scheduled"

            Counter.builder("search.jobs.submitted")
                .description("count of batch search job submitted")
                .tags(
                    "type", type,
                    "searchMode", "foo"
                ).register(registry).increment()

            if(Random().nextBoolean()) {
                val fooTimer = Timer
                    .builder("search.jobs.duration")
                    .description("Latency of completed or erroneous jobs") // optional
                    .tags(
                        "type", type,
                        "searchMode", "foo"
                    )
                    .publishPercentiles(0.5, 0.95) // median and 95th percentile
                    .publishPercentileHistogram()
                    .sla(Duration.ofMillis(100))
                    .minimumExpectedValue(Duration.ofSeconds(1))
                    .maximumExpectedValue(Duration.ofSeconds(3000))
                    .register(registry)

                fooTimer.record(Random().nextInt(3000).toLong(), TimeUnit.SECONDS)
            } else {
                log.info("not complete yet")
            }

            it
        }, String::class.java)
    }


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
