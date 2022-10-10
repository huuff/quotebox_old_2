package xyz.haff.quoteapi.controller

import com.ninjasquad.springmockk.MockkBean
import io.kotest.core.spec.style.FunSpec
import io.mockk.every
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.returnResult
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import xyz.haff.quoteapi.data.repository.QuoteRepository
import xyz.haff.quoteapi.dto.QuoteDto
import xyz.haff.quoteapi.mapper.QuoteMapper
import xyz.haff.quoteapi.testing.TestData

@WebFluxTest(QuoteSseController::class)
class QuoteSseControllerTest(
    private val webClient: WebTestClient,
    @MockkBean private val quoteRepository: QuoteRepository,
    @MockkBean private val quoteMapper: QuoteMapper,
) : FunSpec({

    // TODO: Not working
    test("randomSSE") {
        // ARRANGE
        every {
            quoteRepository.findById(any<String>())
        } returns Mono.just(TestData.entities[0]) andThen Mono.just(TestData.entities[1]) andThen Mono.just(TestData.entities[2])
        every {
            quoteMapper.quoteEntityToQuoteDto(any())
        } returns TestData.dtos[0] andThen TestData.dtos[1] andThen TestData.dtos[2]

        // ACT
        val responses = webClient.get()
            .uri("/sse/quote/random")
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus().isOk
            .returnResult<QuoteDto>()
            .responseBody

        // ASSERT
        StepVerifier.create(responses)
            .expectNext(TestData.dtos[0])
            .expectNext(TestData.dtos[1])
            .expectNext(TestData.dtos[2])
            .expectComplete()
            .verify()
    }
})
