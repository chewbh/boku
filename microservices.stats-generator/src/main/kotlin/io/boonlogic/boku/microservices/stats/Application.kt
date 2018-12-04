package io.boonlogic.boku.microservices.stats

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.ComponentScan

@SpringBootApplication
@ComponentScan(basePackages = ["io.boonlogic.boku"])
class Application

fun main(args: Array<String>) {
    SpringApplication.run(Application::class.java, *args)
}


