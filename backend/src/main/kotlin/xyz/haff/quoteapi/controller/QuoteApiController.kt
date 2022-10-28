package xyz.haff.quoteapi.controller

import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.RestController
import xyz.haff.quoteapi.data.repository.QuoteRepository
import xyz.haff.quoteapi.data.repository.chooseRandom
import xyz.haff.quoteapi.dto.QuoteDto
import xyz.haff.quoteapi.exception.QuoteNotFoundException
import xyz.haff.quoteapi.exception.UserNotFoundException
import xyz.haff.quoteapi.mapper.QuoteMapper
import xyz.haff.quoteapi.service.LikedQuoteService
import xyz.haff.quoteapi.service.UserService
import xyz.haff.quoteapi.util.getCurrentUserId
import java.net.URI

@RestController
class QuoteApiController(
    private val quoteRepository: QuoteRepository,
    private val quoteMapper: QuoteMapper,
    private val userService: UserService,
    private val likedQuoteService: LikedQuoteService,
) : QuoteApi {

    override suspend fun v1GetQuote(id: String): ResponseEntity<QuoteDto> {
        val quote = likedQuoteService.findWithLike(id, getCurrentUserId()) ?: return ResponseEntity.notFound().build()

        return ResponseEntity.ok(quote)
    }

    @PreAuthorize("hasRole('ADMIN')")
    override suspend fun v1AddQuote(quoteDto: QuoteDto): ResponseEntity<Unit> {
        val entity = quoteRepository.save(quoteMapper.dtoToEntity(quoteDto)) ?: return ResponseEntity.internalServerError().build()

        return ResponseEntity.created(URI.create("/quote/${entity.id}")).build()
    }

    // TODO: Maybe should be transactional?
    @PreAuthorize("hasRole('ADMIN')")
    override suspend fun v1UpdateQuote(id: String, quoteDto: QuoteDto): ResponseEntity<Unit> {
        val existingEntity = quoteRepository.findById(id)

        if (existingEntity != null) {
            existingEntity.apply {
                text = quoteDto.text
                author = quoteDto.author
                tags = quoteDto.tags ?: listOf()
                work = quoteDto.work
            }
            quoteRepository.save(existingEntity)

            return ResponseEntity.noContent().build()
        } else {
            val newEntity = quoteMapper.dtoToEntity(quoteDto).apply { this.id = id }
            quoteRepository.save(newEntity)

            return ResponseEntity.created(URI.create("/quote/${id}")).build()
        }
    }

    // TODO: Maybe should be transactional?
    @PreAuthorize("hasRole('ADMIN')")
    override suspend fun v1DeleteQuote(id: String): ResponseEntity<Unit> {
        if (!quoteRepository.existsById(id)) {
            return ResponseEntity.notFound().build()
        }

        quoteRepository.deleteById(id)
        return ResponseEntity.noContent().build()
    }

    override suspend fun v1RandomQuote(author: String?, tags: List<String>?): ResponseEntity<Unit> {
        val quoteEntity = try {
            // MAYBE: A repository method that returns a random ID instead of a random entity? Otherwise, it might be
            // wasteful since the response is a redirect to that entity...
            // Alternatively: cache it with JPA's cache
            quoteRepository.chooseRandom(author, tags)
        } catch (e: IllegalArgumentException) {
            return ResponseEntity.badRequest().build()
        } ?: return ResponseEntity.notFound().build()

        return ResponseEntity
            .status(HttpStatus.SEE_OTHER)
            .location(URI.create("/quote/${quoteEntity.id}"))
            .build()
    }


    @PreAuthorize("isAuthenticated()")
    override suspend fun v1ToggleQuoteLike(id: String): ResponseEntity<Unit> {
        val userId = getCurrentUserId()
            ?: return ResponseEntity.status(401).build()
        userService.findOrRegisterUser(userId)

        return try {
            val wasApplied = likedQuoteService.toggleLike(userId, id)
            if (wasApplied) {
                ResponseEntity.ok().build()
            } else {
                ResponseEntity.internalServerError().build()
            }
        } catch (e: UserNotFoundException) {
            ResponseEntity.notFound().build()
        } catch (e: QuoteNotFoundException) {
            ResponseEntity.notFound().build()
        }
    }
}