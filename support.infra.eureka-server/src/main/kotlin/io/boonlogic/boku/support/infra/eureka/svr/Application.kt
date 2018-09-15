package io.boonlogic.boku.support.infra.eureka.svr

import org.springframework.boot.WebApplicationType
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer

@SpringBootApplication
@EnableEurekaServer
class Application

fun main(args: Array<String>) {
    SpringApplicationBuilder(Application::class.java)
        .web(WebApplicationType.SERVLET)
        .run(*args)
}
