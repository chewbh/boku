package io.boonlogic.boku.support.infra.config.svr

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.cloud.config.server.EnableConfigServer

@SpringBootApplication
@EnableConfigServer
class Application

fun main(args: Array<String>) {
    SpringApplication.run(Application::class.java)
}