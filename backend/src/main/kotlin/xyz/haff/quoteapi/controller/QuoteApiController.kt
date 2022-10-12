package xyz.haff.quoteapi.controller

import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import xyz.haff.quoteapi.data.repository.QuoteRepository
import xyz.haff.quoteapi.data.repository.chooseRandom
import xyz.haff.quoteapi.dto.QuoteDto
import xyz.haff.quoteapi.mapper.QuoteMapper
import java.net.URI

@RestController
class QuoteApiController(
    private val quoteRepository: QuoteRepository,
    private val quoteMapper: QuoteMapper,
) : QuoteApi {

    override suspend fun v1GetQuote(id: String): ResponseEntity<QuoteDto> {
        val quote = quoteRepository.findById(id).awaitSingleOrNull() ?: return ResponseEntity.notFound().build()

        return ResponseEntity.ok(quoteMapper.entityToDto(quote))
    }

    override suspend fun v1RandomQuote(author: String?, tags: List<String>?): ResponseEntity<QuoteDto> {
        val quoteEntity = try {
            quoteRepository.chooseRandom(author, tags)
        } catch (e: IllegalArgumentException) {
            return ResponseEntity.badRequest().build()
        }.awaitSingleOrNull() ?: return ResponseEntity.notFound().build()

        return ResponseEntity.ok(quoteMapper.entityToDto(quoteEntity))
    }

    // TODO: Validations?
    // TODO: Secure
    // TODO: Test
    override suspend fun v1AddQuote(quoteDto: QuoteDto): ResponseEntity<Unit> {
        val entity = quoteRepository.insert(quoteMapper.dtoToEntity(quoteDto))
            .awaitSingleOrNull() ?: return ResponseEntity.internalServerError().build()

        return ResponseEntity.created(URI.create("/quote/${entity.id}")).build()
    }
}