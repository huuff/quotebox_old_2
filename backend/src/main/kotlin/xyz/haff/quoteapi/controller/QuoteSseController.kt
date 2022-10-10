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

    // TODO: Test
    // TODO: Can I add it to the API? (Maybe AsyncAPI is the only choice)
    @GetMapping(path = ["/random"], produces = ["application/x-ndjson"])
    fun randomSSE(
        @RequestParam interval: Int? = null,
        @RequestParam author: String? = null,
        @RequestParam tags: List<String>? = null
    ): Flux<QuoteDto> =
        Flux.interval((interval ?: 5000).millis)
            .flatMap { quoteRepository.chooseRandom(author, tags) }
            .map(quoteMapper::quoteEntityToQuoteDto)
}