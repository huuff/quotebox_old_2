package xyz.haff.quoteapi.service

import com.ninjasquad.springmockk.MockkBean
import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.reactor.mono
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.context.annotation.Import
import org.springframework.test.context.junit.jupiter.SpringExtension
import reactor.core.publisher.Mono
import xyz.haff.quoteapi.data.repository.QuoteRepository
import xyz.haff.quoteapi.data.repository.UserRepository
import xyz.haff.quoteapi.exception.QuoteNotFoundException
import xyz.haff.quoteapi.exception.UserNotFoundException
import xyz.haff.quoteapi.mapper.QuoteMapper

@ExtendWith(SpringExtension::class)
@Import(LikedQuoteService::class)
class LikedQuoteServiceTest(
    @MockkBean private val quoteRepository: QuoteRepository,
    @MockkBean private val userRepository: UserRepository,
    @MockkBean private val quoteMapper: QuoteMapper,
    @MockkBean private val userService: UserService,
    private val likedQuoteService: LikedQuoteService,
) : FunSpec({
    val fakeUserId = "634a7c63e11f6ed82e70cc64"
    val fakeQuoteId = "634a7d494f7a0bd42ccf6c59"

    context("toggleQuoteLike") {
        test("user not found") {
            // ARRANGE
            every { userRepository.findById(eq(fakeUserId)) } returns Mono.empty()

            // ACT & ASSERT
            shouldThrowExactly<UserNotFoundException> {
                likedQuoteService.toggleLike(fakeUserId, fakeQuoteId)
            }
        }

        test("quote not found") {
            // ARRANGE
            every { userRepository.findById(eq(fakeUserId)) } returns mono { mockk() }
            every { quoteRepository.existsById(eq(fakeQuoteId)) } returns mono { false }

            // ACT & ASSERT
            shouldThrowExactly<QuoteNotFoundException> {
                likedQuoteService.toggleLike(fakeUserId, fakeQuoteId)
            }
        }

        test("likes quote") {
            // ARRANGE
            every { userRepository.findById(eq(fakeUserId)) } returns mono { mockk {
                // Has no liked quote
                every { likedQuotes } returns listOf()
            }}
            every { quoteRepository.existsById(eq(fakeQuoteId)) } returns mono { true }
            every { userRepository.addLikedQuote(eq(fakeUserId), eq(fakeQuoteId))} returns mono  { 1L }

            // ACT
            val wasApplied = likedQuoteService.toggleLike(fakeUserId, fakeQuoteId)

            // ASSERT
            wasApplied shouldBe true
            verify { userRepository.addLikedQuote(eq(fakeUserId), eq(fakeQuoteId)) }
        }

        test("unlikes quote") {
            // ARRANGE
            every { userRepository.findById(eq(fakeUserId)) } returns mono { mockk {
                // Already liked this quote
                every { likedQuotes } returns listOf(mockk {
                    every { id } returns fakeQuoteId
                })
            }}
            every { quoteRepository.existsById(eq(fakeQuoteId)) } returns mono { true }
            every { userRepository.removeLikedQuote(eq(fakeUserId), eq(fakeQuoteId))} returns mono { 1L }

            // ACT
            val wasApplied = likedQuoteService.toggleLike(fakeUserId, fakeQuoteId)

            // ASSERT
            wasApplied shouldBe true
            verify { userRepository.removeLikedQuote(eq(fakeUserId), eq(fakeQuoteId)) }
        }
    }
})
