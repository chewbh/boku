package io.boonlogic.boku.app.props

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.cloud.bootstrap.config.PropertySourceLocator
import org.springframework.cloud.client.discovery.DiscoveryClient
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import org.springframework.core.env.MapPropertySource
import org.springframework.core.env.PropertySource
import java.net.URI

private const val PROPS_CONFIG_SVR_URI = "boku.cloud.config.uri"

@ConditionalOnProperty(
    "spring.cloud.config.discovery.enabled", havingValue = "true",
    matchIfMissing = false)
@ConditionalOnClass(DiscoveryClient::class)
@Configuration
class DiscoveryFirstConfigServerPropertySourceLocator (
    @Value("\${spring.cloud.config.discovery.service-id}")
    private val configServiceId: String,
    private val discoveryClient: DiscoveryClient
): PropertySourceLocator {

    private val log = LoggerFactory.getLogger(DiscoveryFirstConfigServerPropertySourceLocator::class.java)

    override fun locate(environment: Environment?): PropertySource<*> {
        log.debug("Discovery first enabled for spring cloud config service: {}", configServiceId)

        val instances = this.discoveryClient.getInstances(configServiceId)
        val configUri = instances.first().uri
        log.info("populating environment property: {} with {}", PROPS_CONFIG_SVR_URI, configUri)
        return MapPropertySource("extendedProperty",
            mapOf(PROPS_CONFIG_SVR_URI to configUri))
    }
}

@ConditionalOnProperty(
    "spring.cloud.config.discovery.enabled",
    havingValue = "false",
    matchIfMissing = true)
@Configuration
class NonDiscoveryFirstConfigServerPropertySourceLocator(
    @Value("\${spring.cloud.config.uri}") private val configServiceUri: String
): PropertySourceLocator {
    private val log = LoggerFactory.getLogger(NonDiscoveryFirstConfigServerPropertySourceLocator::class.java)

    override fun locate(environment: Environment?): PropertySource<*> {

        log.debug("leveraging {spring.cloud.config.uri}")
        log.info("populating environment property: {} with {}", PROPS_CONFIG_SVR_URI, configServiceUri)
        return MapPropertySource("extendedProperty",
            mapOf(PROPS_CONFIG_SVR_URI to URI.create(configServiceUri)))
    }
}
