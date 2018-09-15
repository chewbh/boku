package io.boonlogic.boku.microservices.video.config

import com.fasterxml.jackson.databind.ObjectMapper
import io.boonlogic.boku.microservices.video.domain.Metadata
import org.springframework.beans.factory.annotation.Value
import org.springframework.cloud.context.config.annotation.RefreshScope
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ResourceLoader
import java.net.URI

@Configuration
class LoadDataConfiguration(
    private val resourceLoader: ResourceLoader,
    private val objectmapper: ObjectMapper,
    @Value("\${metadata.config}") private val metadataConfigUri: String
){

    @RefreshScope
    @Bean
    fun metadata(): List<Metadata> {
        println(metadataConfigUri)
        val data: List<Metadata> = objectmapper.readValue(URI.create(metadataConfigUri).toURL(),
            objectmapper.typeFactory.constructCollectionType(MutableList::class.java, Metadata::class.java))
        return data
    }

}
