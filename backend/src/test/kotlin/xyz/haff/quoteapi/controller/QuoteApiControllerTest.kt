package xyz.haff.quoteapi.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.ninjasquad.springmockk.MockkBean
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.reactor.mono
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import reactor.core.publisher.Mono
import xyz.haff.quoteapi.data.entity.QuoteEntity
import xyz.haff.quoteapi.data.repository.QuoteRepository
import xyz.haff.quoteapi.data.repository.RandomQuoteRepository
import xyz.haff.quoteapi.dto.QuoteDto
import xyz.haff.quoteapi.mapper.QuoteMapper
import xyz.haff.quoteapi.testing.TestData

// TODO: Finish it (but first make it work)
@WebFluxTest(QuoteApiController::class)
class QuoteApiControllerTest(
    private val webClient: WebTestClient,
    @MockkBean private val quoteRepository: QuoteRepository,
    @MockkBean private val randomQuoteRepository: RandomQuoteRepository,
    @MockkBean private val quoteMapper: QuoteMapper,
    private val objectMapper: ObjectMapper
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
                .expectBody(QuoteDto::class.java)
                .returnResult()
                .responseBody!!

            // ASSERT
            response shouldBe dto
        }

        test("404 Not Found") {

        }
    }

    test("v1Random") {

    }

})
