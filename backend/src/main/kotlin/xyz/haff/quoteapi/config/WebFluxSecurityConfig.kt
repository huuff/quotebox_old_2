package xyz.haff.quoteapi.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.config.web.server.invoke
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService
import org.springframework.security.core.userdetails.ReactiveUserDetailsService
import org.springframework.security.web.server.SecurityWebFilterChain
import xyz.haff.quoteapi.security.User

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity(proxyTargetClass = true)
class WebFluxSecurityConfig {

    @Bean
    // TODO: Use oAuth
    fun userDetailsService(): ReactiveUserDetailsService {
        val fakeUser = User(
            id = "fakeid",
            authorities = mutableListOf(SimpleGrantedAuthority("ROLE_ADMIN"))
        )
        return MapReactiveUserDetailsService(fakeUser)
    }

    @Bean
    fun springSecurityFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
        return http.invoke {
            authorizeExchange {
                authorize(anyExchange, permitAll)
            }
            csrf { disable() } // TODO: Should I?
            logout { disable() }
            httpBasic { disable() }
        }
    }
}