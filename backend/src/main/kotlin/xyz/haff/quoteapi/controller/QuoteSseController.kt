package xyz.haff.quoteapi.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import xyz.haff.koy.javatime.millis
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
    // TODO: Return a quote for the first iteration?
    @GetMapping(path = ["/random"], produces = ["application/x-ndjson"])
    fun randomSSE(
        @RequestParam(required = false, defaultValue = "5000") interval: Int,
        // XXX: Actually, I only added this because the QuoteSseControllerTest wouldn't work without it
        @RequestParam(required = false, defaultValue = Long.MAX_VALUE.toString()) count: Long,
        @RequestParam(required = false) author: String?,
        @RequestParam(required = false) tags: List<String>?,
    ): Flux<QuoteDto> =
        Flux.interval(interval.millis)
            .take(count)
            .flatMap { quoteRepository.chooseRandom(author, tags) }
            .map(quoteMapper::quoteEntityToQuoteDto)
}