package xyz.haff.quoteapi.controller

import com.ninjasquad.springmockk.MockkBean
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.verify
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.returnResult
import reactor.core.publisher.Mono
import xyz.haff.quoteapi.data.repository.QuoteRepository
import xyz.haff.quoteapi.dto.QuoteDto
import xyz.haff.quoteapi.mapper.QuoteMapper
import xyz.haff.quoteapi.testing.TestData

@WebFluxTest(QuoteApiController::class)
class QuoteApiControllerTest(
    private val webClient: WebTestClient,
    @MockkBean private val quoteRepository: QuoteRepository,
    @MockkBean private val quoteMapper: QuoteMapper,
) : FunSpec({
    val (entity, dto) = TestData.random

    context("v1GetQuote") {
        test("200 OK") {
            // ARRANGE
            every { quoteRepository.findById(any<String>()) } returns Mono.just(entity)
            every { quoteMapper.quoteEntityToQuoteDto(any())} returns dto

            // ACT
            val response = webClient.get()
                .uri("/quote/${entity.id}")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk
                .returnResult<QuoteDto>()
                .responseBody
                .awaitSingle()

            // ASSERT
            response shouldBe dto
            verify { quoteRepository.findById(entity.id!!) }
            verify { quoteMapper.quoteEntityToQuoteDto(entity) }
        }

        test("404 Not Found") {
            // ARRANGE
            every { quoteRepository.findById(any<String>()) } returns Mono.empty()

            // ACT & ASSERT
            webClient.get()
                .uri("/quote/${entity.id}")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound

            verify { quoteRepository.findById(entity.id!!) }
        }
    }

    test("v1Random") {
        // ARRANGE
        every { quoteRepository.getRandom() } returns Mono.just(entity)
        every { quoteMapper.quoteEntityToQuoteDto(any())} returns dto

        // ACT & ASSERT
        webClient.get()
            .uri("/quote/random")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk

        verify { quoteRepository.getRandom() }
        verify { quoteMapper.quoteEntityToQuoteDto(entity) }
    }

})
