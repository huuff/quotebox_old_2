package xyz.haff.quoteapi.controller

import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import xyz.haff.quoteapi.data.repository.QuoteRepository
import xyz.haff.quoteapi.data.repository.RandomQuoteRepository
import xyz.haff.quoteapi.dto.QuoteDto
import xyz.haff.quoteapi.mapper.QuoteMapper

@Controller
class QuoteApiController(
    private val randomQuoteRepository: RandomQuoteRepository,
    private val quoteRepository: QuoteRepository,
    private val quoteMapper: QuoteMapper,
) : QuoteApi {

    override suspend fun v1GetQuote(id: String): ResponseEntity<QuoteDto> {
        val quote = quoteRepository.findById(id).awaitSingleOrNull() ?: return ResponseEntity.notFound().build()

        return ResponseEntity.ok(quoteMapper.quoteEntityToQuoteDto(quote))
    }

    override suspend fun v1Random(author: String?, tags: List<String>?): ResponseEntity<QuoteDto> {
        return ResponseEntity.ok(
            quoteMapper.quoteEntityToQuoteDto(randomQuoteRepository.getOne().awaitSingle())
        )
    }
}