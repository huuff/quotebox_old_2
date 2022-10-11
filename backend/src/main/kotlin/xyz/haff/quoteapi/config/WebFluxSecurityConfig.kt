package xyz.haff.quoteapi.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.config.web.server.invoke
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService
import org.springframework.security.core.userdetails.ReactiveUserDetailsService
import org.springframework.security.core.userdetails.User
import org.springframework.security.web.server.SecurityWebFilterChain

@Configuration
@EnableWebFluxSecurity
class WebFluxSecurityConfig {

    @Bean
    // TODO: Remove and use oauth
    fun userDetailsService(): ReactiveUserDetailsService {
        val userDetails = User.withDefaultPasswordEncoder()
            .username("user")
            .password("user")
            .roles("USER")
            .build()
        return MapReactiveUserDetailsService(userDetails)
    }

    @Bean
    fun springSecurityFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
        return http.invoke {
            authorizeExchange {
                authorize(anyExchange, permitAll)
            }
            formLogin { }
            httpBasic { }
        }
    }
}