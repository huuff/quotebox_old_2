package xyz.haff.quoteapi.service

import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.stereotype.Service
import xyz.haff.quoteapi.data.repository.QuoteRepository
import xyz.haff.quoteapi.data.repository.UserRepository
import xyz.haff.quoteapi.exception.QuoteNotFoundException
import xyz.haff.quoteapi.exception.UserNotFoundException

@Service
class ToggleQuoteLikeService(
    private val userRepository: UserRepository,
    private val quoteRepository: QuoteRepository,
) {

    // TODO: Maybe should be transactional?
    // TODO: So many queries... this can't be performant!
    suspend fun toggleQuoteLike(userId: String, quoteId: String): Boolean {
        val userEntity = userRepository.findById(userId).awaitSingleOrNull()
            ?: throw UserNotFoundException(userId)

        if (!quoteRepository.existsById(quoteId).awaitSingle()) {
            throw QuoteNotFoundException(quoteId)
        }

        // TODO: What about a repository method to get liked quote ids? Or whether a quote is already liked?
        // this way we avoid the cost of fetching all quotes and mapping them
        val modifiedCount = if (userEntity.likedQuotes.none { it.id == quoteId }) {
            userRepository.addLikedQuote(userId, quoteId)
        } else {
            userRepository.removeLikedQuote(userId, quoteId)
        }.awaitSingle()

        return modifiedCount == 1L
    }
}