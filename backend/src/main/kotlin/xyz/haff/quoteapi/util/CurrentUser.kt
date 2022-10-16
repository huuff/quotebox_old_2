package xyz.haff.quoteapi.util

import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.security.core.context.ReactiveSecurityContextHolder

suspend fun getCurrentUserId(): String? = ReactiveSecurityContextHolder.getContext().awaitSingleOrNull()?.authentication?.name