package xyz.haff.quoteapi.controller

import com.ninjasquad.springmockk.MockkBean
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import kotlinx.coroutines.reactor.mono
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import reactor.core.publisher.Mono
import xyz.haff.quoteapi.data.entity.QuoteEntity
import xyz.haff.quoteapi.data.repository.QuoteRepository
import xyz.haff.quoteapi.data.repository.RandomQuoteRepository

// TODO: Finish it (but first make it work)
@WebMvcTest(QuoteApiController::class)
class QuoteApiControllerTest(
    private val mockMvc: MockMvc,
    @MockkBean private val quoteRepository: QuoteRepository,
    @MockkBean private val randomQuoteRepository: RandomQuoteRepository,
) : FunSpec({
    val sampleQuote = QuoteEntity(
        id = "12345",
        text = "text",
        author = "author",
        work = "work",
        tags = listOf("tag1", "tag2")
    )

    context("v1GetQuote") {
        test("200 OK") {
            // ARRANGE
            every { quoteRepository.findById(any<String>()) } returns Mono.just(sampleQuote)

            // ACT & ASSERT
            mockMvc.get("/quote/${sampleQuote.id}") {
                accept = MediaType.APPLICATION_JSON
            }.andExpect {
                status { isOk() }
                content { contentType(MediaType.APPLICATION_JSON) }
                content { json("{}")}
            }
        }

        test("404 Not Found")
    }

    test("v1Random")

})
