package xyz.haff.quoteapi.util

import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.security.oauth2.jwt.Jwt

suspend fun getCurrentUserId(): String? =
    (ReactiveSecurityContextHolder.getContext().awaitSingleOrNull()?.authentication?.principal as Jwt?)?.subject