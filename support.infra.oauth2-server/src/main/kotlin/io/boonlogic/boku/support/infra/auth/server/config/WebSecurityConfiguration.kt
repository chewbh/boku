package io.boonlogic.boku.support.infra.auth.server.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter

@EnableWebSecurity
@Configuration
class WebSecurityConfiguration(

): WebSecurityConfigurerAdapter() {

    //    override fun configure(http: HttpSecurity?) {
//        http?.let {
//            it.csrf().disable()
//        }
//    }
//
    override fun configure(auth: AuthenticationManagerBuilder?) {
        auth?.let {
            it.inMemoryAuthentication()
                .withUser("user")
                .password("P@ssw0rd")
                .roles("USER")
        }
    }

    @Bean
    @Throws(Exception::class)
    override fun authenticationManagerBean(): AuthenticationManager {
        return super.authenticationManagerBean()
    }
}
