package xyz.haff.quoteapi.config

import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.reactor.asFlux
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.convert.converter.Converter
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.config.web.server.invoke
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverter
import org.springframework.security.web.server.SecurityWebFilterChain
import reactor.core.publisher.Flux

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity(proxyTargetClass = true)
class WebFluxSecurityConfig {
/*
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
    */

    @Bean
    fun springSecurityFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
        return http.invoke {
            authorizeExchange {
                authorize(anyExchange, permitAll)
            }
            csrf { disable() }
            logout { disable() }
            httpBasic { disable() }
            oauth2ResourceServer {
                jwt { }
            }
        }
    }

    @Bean
    fun jwtAuthenticationConverter(): ReactiveJwtAuthenticationConverter {

        /*
        val grantedAuthoritiesConverter = JwtGrantedAuthoritiesConverter()
        grantedAuthoritiesConverter.setAuthoritiesClaimName("realm_access.roles")
        grantedAuthoritiesConverter.setAuthorityPrefix("ROLE_")
         */

        // TODO: Try the commented out way
        val grantedAuthoritiesConverter = Converter<Jwt, Flux<GrantedAuthority>> { source ->
                println(source);
                Flux.fromIterable(
                    ((source.claims["realm_access"] as Map<String, Any>)["roles"] as List<String>)

                ).map(::SimpleGrantedAuthority)
            }

        val jwtAuthenticationConverter = ReactiveJwtAuthenticationConverter()
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter)
        return jwtAuthenticationConverter
    }
}