package xyz.haff.quoteapi.service

import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.stereotype.Service
import xyz.haff.quoteapi.data.entity.UserEntity
import xyz.haff.quoteapi.data.repository.UserRepository

@Service
class UserService(
    private val userRepository: UserRepository,
) {

    // TODO: Maybe this should be called by Spring Security at the filter level? Otherwise, this method call
    // is sprinkled all around the code
    /**
     * Since users are managed by the OpenID Connect provider, we just store their OpenID Connect subject when they
     * are first used
     */
    suspend fun findOrRegisterUser(userId: String): UserEntity
        = userRepository.findById(userId) ?: registerUser(userId)

    suspend fun registerUser(userId: String): UserEntity
        = userRepository.save(UserEntity(id = userId))
}