package io.boonlogic.boku.app.config

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.cloud.config.client.ConfigClientProperties
import org.springframework.cloud.config.client.ConfigServicePropertySourceLocator
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.security.oauth2.client.DefaultOAuth2ClientContext
import org.springframework.security.oauth2.client.OAuth2ClientContext
import org.springframework.security.oauth2.client.OAuth2RestTemplate
import org.springframework.security.oauth2.client.token.DefaultAccessTokenRequest
import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsAccessTokenProvider
import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsResourceDetails
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client

@ConditionalOnProperty("boku.config.oauth2.client.enabled", havingValue = "true")
@Configuration
class OAuth2ConfigClientRestConfiguration(
    private val clientProperties: ConfigClientProperties,
    private val oauth2RestTemplate: OAuth2RestTemplate) {

    @Bean
    fun configServicePropertySourceLocator(): ConfigServicePropertySourceLocator =
        ConfigServicePropertySourceLocator(clientProperties).apply {
            setRestTemplate(oauth2RestTemplate)
        }
}

@ConditionalOnProperty("boku.config.oauth2.client.enabled", havingValue = "true")
@EnableOAuth2Client
@Configuration
class OAuth2ConfigClientConfiguration {

    @Primary
    @Bean
    @ConfigurationProperties(prefix = "boku.config.oauth2.client")
    fun clientCredentialsResourceDetails(): ClientCredentialsResourceDetails =
        ClientCredentialsResourceDetails().apply {
            scope = listOf("read","trust")
        }

    @Bean
    fun oauth2ClientContext(resourceDetails: ClientCredentialsResourceDetails): OAuth2ClientContext {
        return DefaultOAuth2ClientContext(
            ClientCredentialsAccessTokenProvider()
                .obtainAccessToken(resourceDetails, DefaultAccessTokenRequest()))
    }

//    curl -H "Authorization: Bearer $(curl clientid:clientsecret@authserver/oauth/token -d "grant_type=client_credentials" | jq --raw-output .'access_token')" localhost:8080/api/hello | jq
    @Bean
    fun oauth2RestTemplate(
        resourceDetails: ClientCredentialsResourceDetails,
        oauth2ClientContext: OAuth2ClientContext
        ): OAuth2RestTemplate = OAuth2RestTemplate(resourceDetails, oauth2ClientContext)
}
