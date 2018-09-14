package io.boonlogic.boku.app.props

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.cloud.bootstrap.config.PropertySourceLocator
import org.springframework.cloud.client.discovery.DiscoveryClient
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import org.springframework.core.env.MapPropertySource
import org.springframework.core.env.PropertySource

@ConditionalOnProperty("spring.cloud.config.discovery.service-id", matchIfMissing = false)
@ConditionalOnBean(DiscoveryClient::class)
@Configuration
class DiscoveryFirstConfigServerPropertySourceLocator(
    @Value("\${spring.cloud.config.discovery.service-id}") private val configServiceName: String,
    private val discoveryClient: DiscoveryClient
): PropertySourceLocator {

    override fun locate(environment: Environment?): PropertySource<*> {

        println(this::class.java.name)
        val instances = this.discoveryClient.getInstances(configServiceName)
        val serviceInstance = instances[0]

        return MapPropertySource("extendedProperty",
            mapOf("configserver.discovered.uri" to serviceInstance.uri))
    }
}
