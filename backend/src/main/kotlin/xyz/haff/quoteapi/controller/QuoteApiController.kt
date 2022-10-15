package xyz.haff.quoteapi.controller

import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.RestController
import reactor.kotlin.extra.bool.not
import xyz.haff.quoteapi.data.repository.QuoteRepository
import xyz.haff.quoteapi.data.repository.UserRepository
import xyz.haff.quoteapi.data.repository.chooseRandom
import xyz.haff.quoteapi.dto.QuoteDto
import xyz.haff.quoteapi.exception.QuoteNotFoundException
import xyz.haff.quoteapi.exception.UserNotFoundException
import xyz.haff.quoteapi.mapper.QuoteMapper
import xyz.haff.quoteapi.security.User
import xyz.haff.quoteapi.service.ToggleQuoteLikeService
import java.net.URI

@RestController
class QuoteApiController(
    private val quoteRepository: QuoteRepository,
    private val quoteMapper: QuoteMapper,
    private val toggleQuoteLikeService: ToggleQuoteLikeService,
) : QuoteApi {

    override suspend fun v1GetQuote(id: String): ResponseEntity<QuoteDto> {
        val quote = quoteRepository.findById(id).awaitSingleOrNull() ?: return ResponseEntity.notFound().build()

        return ResponseEntity.ok(quoteMapper.entityToDto(quote))
    }

    // TODO: Validations?
    @PreAuthorize("hasRole('ADMIN')")
    override suspend fun v1AddQuote(quoteDto: QuoteDto): ResponseEntity<Unit> {
        val entity = quoteRepository.insert(quoteMapper.dtoToEntity(quoteDto))
            .awaitSingleOrNull() ?: return ResponseEntity.internalServerError().build()

        return ResponseEntity.created(URI.create("/quote/${entity.id}")).build()
    }

    // TODO: Maybe should be transactional?
    @PreAuthorize("hasRole('ADMIN')")
    override suspend fun v1UpdateQuote(id: String, quoteDto: QuoteDto): ResponseEntity<Unit> {
        val existingEntity = quoteRepository.findById(id).awaitSingleOrNull()

        if (existingEntity != null) {
            existingEntity.apply {
                text = quoteDto.text
                author = quoteDto.author
                tags = quoteDto.tags ?: listOf()
                work = quoteDto.work
            }
            quoteRepository.save(existingEntity).awaitSingleOrNull()

            return ResponseEntity.noContent().build()
        } else {
            val newEntity = quoteMapper.dtoToEntity(quoteDto).apply { this.id = id }
            quoteRepository.insert(newEntity).awaitSingleOrNull()

            return ResponseEntity.created(URI.create("/quote/${id}")).build()
        }
    }

    // TODO: Maybe should be transactional?
    @PreAuthorize("hasRole('ADMIN')")
    override suspend fun v1DeleteQuote(id: String): ResponseEntity<Unit> {
        if (!quoteRepository.existsById(id).awaitSingle()) {
            return ResponseEntity.notFound().build()
        }

        quoteRepository.deleteById(id).awaitSingleOrNull()
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
        }.awaitSingleOrNull() ?: return ResponseEntity.notFound().build()

        return ResponseEntity
            .status(HttpStatus.SEE_OTHER)
            .location(URI.create("/quote/${quoteEntity.id}"))
            .build()
    }


    @PreAuthorize("isAuthenticated()")
    override suspend fun v1ToggleQuoteLike(id: String): ResponseEntity<Unit> {
        val userId = (ReactiveSecurityContextHolder.getContext().awaitSingleOrNull()?.authentication?.principal as Jwt?)
            ?.subject
            ?: return ResponseEntity.status(401).build()

        return try {
            val wasApplied = toggleQuoteLikeService.toggleQuoteLike(userId, id)
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