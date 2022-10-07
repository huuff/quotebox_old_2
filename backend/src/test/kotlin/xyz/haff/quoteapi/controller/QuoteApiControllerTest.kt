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

// TODO: Finish it (but first make it work)
@WebFluxTest(QuoteApiController::class)
class QuoteApiControllerTest(
    private val webClient: WebTestClient,
    @MockkBean private val quoteRepository: QuoteRepository,
    @MockkBean private val randomQuoteRepository: RandomQuoteRepository,
    @MockkBean private val quoteMapper: QuoteMapper,
    private val objectMapper: ObjectMapper
) : FunSpec({
    val mockQuoteEntity = mockk<QuoteEntity> {
        every { id } returns "test"
    }
    val responseDto = QuoteDto(
        id = "id",
        author = "author",
        text = "text",
        work = "work",
        tags = listOf("tag1", "tag2", "tag3")
    )

    context("v1GetQuote") {
        test("200 OK") {
            // ARRANGE
            // TODO: Use the exact types instead of any
            every { quoteRepository.findById(any<String>()) } returns Mono.just(mockQuoteEntity)
            every { quoteMapper.quoteEntityToQuoteDto(any())} returns responseDto

            val response = webClient.get()
                .uri("/quote/${mockQuoteEntity.id}")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk
                .expectBody(QuoteDto::class.java)
                .returnResult()
                .responseBody!!

            response shouldBe responseDto
        }

        test("404 Not Found")
    }

    test("v1Random")

})
