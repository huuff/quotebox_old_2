package xyz.haff.quoteapi.service

import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.stereotype.Service
import xyz.haff.quoteapi.data.repository.QuoteRepository
import xyz.haff.quoteapi.data.repository.UserRepository
import xyz.haff.quoteapi.dto.QuoteDto
import xyz.haff.quoteapi.exception.QuoteNotFoundException
import xyz.haff.quoteapi.exception.UserNotFoundException
import xyz.haff.quoteapi.mapper.QuoteMapper

@Service
class LikedQuoteService(
    private val userRepository: UserRepository,
    private val quoteRepository: QuoteRepository,
    private val quoteMapper: QuoteMapper,
    private val userService: UserService,
) {

    // TODO: Maybe should be transactional?
    // TODO: So many queries... this can't be performant!
    suspend fun toggleLike(userId: String, quoteId: String): Boolean {
        val userEntity = userRepository.findById(userId)
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
        }

        return modifiedCount == 1L
    }

    // TODO: Transactional?
    // TODO: Test
    suspend fun findWithLike(quoteId: String, userId: String?): QuoteDto? {
        val quoteEntity = quoteRepository.findById(quoteId).awaitSingleOrNull() ?: return null
        val quoteDto = quoteMapper.entityToDto(quoteEntity)

        return if (userId != null) {
            userService.findOrRegisterUser(userId)
            quoteDto.copy(
                liked = userRepository.hasLikedQuote(quoteId, userId)
            )
        } else {
            quoteDto
        }
    }
}