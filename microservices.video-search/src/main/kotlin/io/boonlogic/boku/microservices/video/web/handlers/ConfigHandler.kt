package io.boonlogic.boku.microservices.video.web.handlers

import org.springframework.beans.factory.annotation.Value
import org.springframework.cloud.context.config.annotation.RefreshScope
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.toMono

@RefreshScope
@Component
class ConfigHandler(
    @Value("\${configuration.project-name}") private val projectName: String,
    @Value("\${info.foo}") private val foo: String
) {
    fun name(req: ServerRequest) =
        ServerResponse.ok().body(projectName.toMono(), String::class.java)

    fun foo(req: ServerRequest) =
        ServerResponse.ok().body(foo.toMono(), String::class.java)
}