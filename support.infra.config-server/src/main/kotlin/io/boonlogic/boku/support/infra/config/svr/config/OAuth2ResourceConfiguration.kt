package io.boonlogic.boku.support.infra.config.svr.config

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore

/**
 * Identifies this resource server.
 * Useful if the AuthorisationServer authorises multiple Resource servers.
 */
private const val RESOURCE_ID = "CONFIG_SERVICE"

@ConditionalOnProperty("boku.config.oauth2.client.enabled", havingValue = "true")
@EnableResourceServer
@Configuration
class OAuth2ResourceConfiguration: ResourceServerConfigurerAdapter() {

    override fun configure(resources: ResourceServerSecurityConfigurer?) {
        resources?.let {
            it.resourceId(RESOURCE_ID)
            it.tokenStore(tokenStore())
        }
    }

    override fun configure(http: HttpSecurity?) {

        http?.authorizeRequests()
            ?.antMatchers("/*.xml", "/**/*.xml")?.permitAll()
            ?.antMatchers("/*.json", "/**/*.json")?.permitAll()
            ?.antMatchers(HttpMethod.OPTIONS, "/**")?.permitAll()

        super.configure(http)

        http?.let {
            it.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.NEVER)
                .and()
                .requestMatchers()
                .antMatchers("/**")
                .and()
                .authorizeRequests()
                .antMatchers(HttpMethod.GET, "/**")
                .access("#oauth2.hasScope('read')")
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
