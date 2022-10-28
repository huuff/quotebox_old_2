package xyz.haff.quoteapi.controller

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import xyz.haff.quoteapi.data.repository.QuoteRepository
import xyz.haff.quoteapi.data.repository.chooseRandom
import xyz.haff.quoteapi.dto.QuoteDto
import xyz.haff.quoteapi.mapper.QuoteMapper

@RequestMapping("/sse/quote")
@RestController
class QuoteSseController(
    private val quoteRepository: QuoteRepository,
    private val quoteMapper: QuoteMapper,
) {

    // TODO: Can I add it to the API? (Maybe AsyncAPI is the only choice)
    // TODO: Also add the liked field!
    @GetMapping(path = ["/random"], produces = ["application/x-ndjson"])
    fun randomSSE(
        @RequestParam(required = false, defaultValue = "5000") interval: Int,
        // XXX: Actually, I only added this because the QuoteSseControllerTest wouldn't work without it
        @RequestParam(required = false, defaultValue = Long.MAX_VALUE.toString()) count: Long, // TODO: As int?
        @RequestParam(required = false) author: String?,
        @RequestParam(required = false) tags: List<String>?,
    ): Flow<QuoteDto> = flow {
        repeat(count.toInt()) {
            val entity = quoteRepository.chooseRandom(author, tags)!! // TODO: What if there's none?
            val dto = quoteMapper.entityToDto(entity)
            emit(dto)
            delay(interval.toLong())
        }
    }
}