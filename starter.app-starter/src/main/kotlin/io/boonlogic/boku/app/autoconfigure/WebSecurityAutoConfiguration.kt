package io.boonlogic.boku.app.autoconfigure

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Lazy
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService
import org.springframework.security.core.userdetails.ReactiveUserDetailsService
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.factory.PasswordEncoderFactories
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.provisioning.InMemoryUserDetailsManager
import java.util.UUID

@ConditionalOnMissingBean(ReactiveUserDetailsService::class)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
@EnableWebFluxSecurity
@Configuration
class WebFluxSecurityAutoConfiguration {

    @Bean
    fun encoder(): PasswordEncoder =
        PasswordEncoderFactories.createDelegatingPasswordEncoder()

    @Bean
    fun userDetailsService(encoder: PasswordEncoder): MapReactiveUserDetailsService {

        val randomPassword = UUID.randomUUID().toString()
        val user = User
            .withUsername("user")
            .password(encoder.encode(randomPassword))
            .roles("USER").build()
        return MapReactiveUserDetailsService(user)
    }

    @Bean
    fun securityFilterChain(http: ServerHttpSecurity) =
        http.httpBasic()
            .and()
            .authorizeExchange()
            .anyExchange().permitAll()
}

@ConditionalOnClass(WebSecurityConfigurerAdapter::class)
@ConditionalOnMissingBean(
    UserDetailsService::class,
    AuthenticationManager::class, AuthenticationProvider::class)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@EnableWebSecurity
@Configuration
class ServletBasedWebSecurityConfiguration: WebSecurityConfigurerAdapter() {

    @Bean
    fun encoder(): PasswordEncoder =
        PasswordEncoderFactories.createDelegatingPasswordEncoder()

    @Bean
    @ConditionalOnMissingBean(
        type = ["org.springframework.security.oauth2.client.registration.ClientRegistrationRepository"])
    @Lazy
    fun userDetailsManager(encoder: PasswordEncoder): InMemoryUserDetailsManager {

        val randomPassword = UUID.randomUUID().toString()
        val user = User
            .withUsername("user")
            .password(encoder.encode(randomPassword))
            .roles("USER").build()
        return InMemoryUserDetailsManager(user)
    }

    override fun configure(http: HttpSecurity?) {
        http?.httpBasic()
            ?.and()
            ?.authorizeRequests()?.anyRequest()?.permitAll()
    }
}
