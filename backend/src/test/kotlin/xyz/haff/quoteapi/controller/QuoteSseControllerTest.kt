package xyz.haff.quoteapi.controller

import com.ninjasquad.springmockk.MockkBean
import io.kotest.core.spec.style.FunSpec
import io.mockk.coEvery
import io.mockk.every
import kotlinx.coroutines.reactor.mono
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.returnResult
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import xyz.haff.koy.javatime.millis
import xyz.haff.quoteapi.config.WebFluxSecurityConfig
import xyz.haff.quoteapi.data.repository.QuoteRepository
import xyz.haff.quoteapi.dto.QuoteDto
import xyz.haff.quoteapi.mapper.QuoteMapper
import xyz.haff.quoteapi.testing.TestData

@WebFluxTest(QuoteSseController::class)
@Import(WebFluxSecurityConfig::class)
class QuoteSseControllerTest(
    private val webClient: WebTestClient,
    @MockkBean private val quoteRepository: QuoteRepository,
    @MockkBean private val quoteMapper: QuoteMapper,
    @MockkBean private val reactiveJwtDecoder: ReactiveJwtDecoder, // Prevents oAuth breakage
) : FunSpec({


    test("randomSSE") {
        // ARRANGE
        coEvery {
            quoteRepository.getRandomId()
        } returns TestData.quoteEntities[0].id!! andThen TestData.quoteEntities[1].id!! andThen TestData.quoteEntities[2].id!!
        coEvery {
            quoteRepository.findById(any())
        } returns TestData.quoteEntities[0] andThen TestData.quoteEntities[1] andThen TestData.quoteEntities[2]
        every {
            quoteMapper.entityToDto(any())
        } returns TestData.quoteDtos[0] andThen TestData.quoteDtos[1] andThen TestData.quoteDtos[2]

        // ACT
        val responses = webClient.get()
            .uri {
                it.path("/sse/quote/random")
                it.queryParam("interval", 1)
                it.queryParam("count", 3) // XXX: Won't work without it!
                it.build()
            }
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus().isOk
            .returnResult<QuoteDto>()
            .responseBody

        // ASSERT
        StepVerifier.withVirtualTime { responses }
            .expectSubscription()
            .expectNext(TestData.quoteDtos[0])
            .thenAwait(1.millis)
            .expectNext(TestData.quoteDtos[1])
            .thenAwait(1.millis)
            .expectNext(TestData.quoteDtos[2])
            .verifyComplete()
    }
})
