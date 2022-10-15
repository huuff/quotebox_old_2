package xyz.haff.quoteapi.service

import com.ninjasquad.springmockk.MockkBean
import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.context.annotation.Import
import org.springframework.test.context.junit.jupiter.SpringExtension
import reactor.core.publisher.Mono
import xyz.haff.quoteapi.data.repository.QuoteRepository
import xyz.haff.quoteapi.data.repository.UserRepository
import xyz.haff.quoteapi.exception.QuoteNotFoundException
import xyz.haff.quoteapi.exception.UserNotFoundException

@ExtendWith(SpringExtension::class)
@Import(ToggleQuoteLikeService::class)
class ToggleQuoteLikeServiceTest(
    @MockkBean private val quoteRepository: QuoteRepository,
    @MockkBean private val userRepository: UserRepository,
    private val toggleQuoteLikeService: ToggleQuoteLikeService,
) : FunSpec({
    val fakeUserId = "634a7c63e11f6ed82e70cc64"
    val fakeQuoteId = "634a7d494f7a0bd42ccf6c59"

    context("toggleQuoteLike") {
        test("user not found") {
            // ARRANGE
            every { userRepository.findById(eq(fakeUserId)) } returns Mono.empty()

            // ACT & ASSERT
            shouldThrowExactly<UserNotFoundException> {
                toggleQuoteLikeService.toggleQuoteLike(fakeUserId, fakeQuoteId)
            }
        }

        test("quote not found") {
            // ARRANGE
            every { userRepository.findById(eq(fakeUserId)) } returns Mono.just(mockk())
            every { quoteRepository.existsById(eq(fakeQuoteId)) } returns Mono.just(false)

            // ACT & ASSERT
            shouldThrowExactly<QuoteNotFoundException> {
                toggleQuoteLikeService.toggleQuoteLike(fakeUserId, fakeQuoteId)
            }
        }

        test("likes quote") {
            // ARRANGE
            every { userRepository.findById(eq(fakeUserId)) } returns Mono.just(mockk {
                // Has no liked quote
                every { likedQuotes } returns listOf()
            })
            every { quoteRepository.existsById(eq(fakeQuoteId)) } returns Mono.just(true)
            every { userRepository.addLikedQuote(eq(fakeUserId), eq(fakeQuoteId))} returns Mono.just(1L)

            // ACT
            val wasApplied = toggleQuoteLikeService.toggleQuoteLike(fakeUserId, fakeQuoteId)

            // ASSERT
            wasApplied shouldBe true
            verify { userRepository.addLikedQuote(eq(fakeUserId), eq(fakeQuoteId)) }
        }

        test("unlikes quote") {
            // ARRANGE
            every { userRepository.findById(eq(fakeUserId)) } returns Mono.just(mockk {
                // Already liked this quote
                every { likedQuotes } returns listOf(mockk {
                    every { id } returns fakeQuoteId
                })
            })
            every { quoteRepository.existsById(eq(fakeQuoteId)) } returns Mono.just(true)
            every { userRepository.removeLikedQuote(eq(fakeUserId), eq(fakeQuoteId))} returns Mono.just(1L)

            // ACT
            val wasApplied = toggleQuoteLikeService.toggleQuoteLike(fakeUserId, fakeQuoteId)

            // ASSERT
            wasApplied shouldBe true
            verify { userRepository.removeLikedQuote(eq(fakeUserId), eq(fakeQuoteId)) }
        }
    }
})
