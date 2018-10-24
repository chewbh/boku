package io.boonlogic.boku.support.infra.config.svr.config

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy

@ConditionalOnProperty("boku.config.oauth2.client.enabled",
    havingValue = "false", matchIfMissing = true)
@Configuration
@EnableWebSecurity
class SecurityConfiguration: WebSecurityConfigurerAdapter() {

    override fun configure(http: HttpSecurity?) {
        http
            ?.csrf()?.disable()
            ?.httpBasic()
            ?.and()
            ?.sessionManagement()?.sessionCreationPolicy(SessionCreationPolicy.NEVER)
            ?.and()
            ?.authorizeRequests()
            ?.antMatchers(HttpMethod.POST, "/encrypt")?.permitAll()
            ?.antMatchers(HttpMethod.POST, "/decrypt")?.denyAll()
            ?.antMatchers("/*.xml", "/**/*.xml")?.permitAll()
            ?.antMatchers("/*.json", "/**/*.json")?.permitAll()
            ?.antMatchers("/actuator", "/actuator/**")?.permitAll()
            ?.anyRequest()?.authenticated()
    }
}
