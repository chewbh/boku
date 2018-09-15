package io.boonlogic.boku.microservices.video.web

import io.boonlogic.boku.microservices.video.web.handlers.ConfigHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.router

@Configuration
class Routes {

    @Bean
    fun webRoutes(
        configHandler: ConfigHandler
    ) = router {
        accept(MediaType.APPLICATION_JSON)
            .and(path("/api")).nest {
                GET("/name", configHandler::name)
                GET("/metadata", configHandler::metadata)
                GET("/foo", configHandler::foo)
            }
    }
}
