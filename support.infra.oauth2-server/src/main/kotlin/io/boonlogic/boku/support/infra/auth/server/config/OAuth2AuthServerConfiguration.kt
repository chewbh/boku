package io.boonlogic.boku.support.infra.auth.server.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.crypto.factory.PasswordEncoderFactories
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore

/** responsible for generating tokens specific to a client */
@EnableAuthorizationServer
@Configuration
class OAuth2AuthServerConfiguration(
    private val authenticationManager: AuthenticationManager
): AuthorizationServerConfigurerAdapter() {

    override fun configure(endpoints: AuthorizationServerEndpointsConfigurer?) {
        endpoints
            ?.tokenStore(tokenStore())
            ?.tokenEnhancer(accessTokenConverter())
            ?.authenticationManager(authenticationManager)
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder()
    }

    override fun configure(clients: ClientDetailsServiceConfigurer?) {

        val encoder = passwordEncoder()
//            .secret("{noop}secret")

        val CLIEN_ID = "svc-user"
        val CLIENT_SECRET = encoder.encode("P@ssw0rd")
        val GRANT_TYPE_PASSWORD = "password"
        val AUTHORIZATION_CODE = "authorization_code"
        val REFRESH_TOKEN = "refresh_token"
        val IMPLICIT = "implicit"
        val CLIENT_CREDENTIALS = "client_credentials"
        val SCOPE_READ = "read"
        val SCOPE_WRITE = "write"
        val TRUST = "trust"

        clients?.let {
            it.inMemory()
                .withClient(CLIEN_ID)
                .authorizedGrantTypes(
                    GRANT_TYPE_PASSWORD, AUTHORIZATION_CODE, REFRESH_TOKEN, IMPLICIT,
                    CLIENT_CREDENTIALS)
                .authorities("ROLE_CLIENT", "ROLE_TRUSTED_CLIENT")
                .secret(CLIENT_SECRET)
                .scopes(SCOPE_READ, SCOPE_WRITE, TRUST)
        }
    }

    @Bean
    fun tokenStore() = JwtTokenStore(accessTokenConverter())

    @Bean
    fun accessTokenConverter() =
        JwtAccessTokenConverter().apply {
            setSigningKey("as466gf")
        }
}
