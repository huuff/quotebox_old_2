package xyz.haff.quoteapi.controller

import com.ninjasquad.springmockk.MockkBean
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.*
import kotlinx.coroutines.reactive.awaitSingle
import kotlinx.coroutines.reactor.mono
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockJwt
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockUser
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.returnResult
import reactor.core.publisher.Mono
import xyz.haff.quoteapi.config.WebFluxSecurityConfig
import xyz.haff.quoteapi.config.validation.ValidationConfiguration
import xyz.haff.quoteapi.data.entity.QuoteEntity
import xyz.haff.quoteapi.data.repository.QuoteRepository
import xyz.haff.quoteapi.data.repository.UserRepository
import xyz.haff.quoteapi.dto.QuoteDto
import xyz.haff.quoteapi.mapper.QuoteMapper
import xyz.haff.quoteapi.service.LikedQuoteService
import xyz.haff.quoteapi.service.UserService
import xyz.haff.quoteapi.testing.TestData

@WebFluxTest(
    controllers = [QuoteApiController::class],
)
@Import(WebFluxSecurityConfig::class, ValidationConfiguration::class)
class QuoteApiControllerTest(
    private val webClient: WebTestClient,
    @MockkBean private val quoteRepository: QuoteRepository,
    @MockkBean private val quoteMapper: QuoteMapper,
    @MockkBean private val reactiveJwtDecoder: ReactiveJwtDecoder, // Prevents oAuth breakage
    @MockkBean private val userRepository: UserRepository,
    @MockkBean private val userService: UserService,
    @MockkBean private val likedQuoteService: LikedQuoteService,
) : FunSpec({
    val (entity, dto) = TestData.randomQuote

    context("v1GetQuote") {
        test("200 OK") {
            // ARRANGE
            coEvery { likedQuoteService.findWithLike(eq(entity.id!!), isNull()) } returns dto

            // ACT
            val response = webClient
                .get()
                .uri("/quote/${entity.id}")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk
                .returnResult<QuoteDto>()
                .responseBody
                .awaitSingle()

            // ASSERT
            response shouldBe dto
        }

        test("404 Not Found") {
            // ARRANGE
            coEvery { likedQuoteService.findWithLike(eq(entity.id!!), any()) } returns null

            // ACT & ASSERT
            webClient
                .get()
                .uri("/quote/${entity.id}")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound
        }

        test("quote is liked") {
            // ARRANGE
            val fakeUserId = "634bc7a6e76695732e267491"
            coEvery { likedQuoteService.findWithLike(eq(entity.id!!), eq(fakeUserId)) } returns dto.copy(liked = true)

            // ACT
            val result = webClient
                .mutateWith(mockJwt().jwt { it.subject(fakeUserId) })
                .get()
                .uri("/quote/${entity.id}")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk
                .returnResult<QuoteDto>()
                .responseBody
                .awaitSingle()

            // ASSERT
            result.liked shouldBe true
        }
    }

    context("v1AddQuote") {
        test("401") {
            webClient.post()
                .uri("/quote")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(dto), QuoteDto::class.java)
                .exchange()
                .expectStatus().isUnauthorized
        }

        test("403") {
            webClient
                .mutateWith(mockUser())
                .post()
                .uri("/quote")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(dto), QuoteDto::class.java)
                .exchange()
                .expectStatus().isForbidden
        }

        test("201") {
            // ARRANGE
            coEvery { quoteMapper.dtoToEntity(any()) } returns entity
            coEvery { quoteRepository.save(eq(entity)) } returns entity

            // ACT & ASSERT
            webClient
                .mutateWith(mockUser().roles("ADMIN"))
                .post()
                .uri("/quote")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(dto), QuoteDto::class.java)
                .exchange()
                .expectStatus().isCreated
                .expectHeader()
                .location("/quote/${entity.id}")
        }

        context("v1UpdateQuote") {
            test("201") {
                // ARRANGE
                val fakeId = "63495ac2eb0bb2a94bb3e512"
                val mockEntity = mockk<QuoteEntity>(relaxed = true)
                coEvery { quoteRepository.findById(eq(fakeId)) } returns null
                coEvery { quoteMapper.dtoToEntity(eq(dto)) } returns mockEntity
                coEvery { quoteRepository.save(any<QuoteEntity>()) } returns mockEntity

                // ACT & ASSERT
                webClient
                    .mutateWith(mockUser().roles("ADMIN"))
                    .put()
                    .uri("/quote/$fakeId")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Mono.just(dto), QuoteDto::class.java)
                    .exchange()
                    .expectStatus().isCreated
                    .expectHeader()
                    .location("/quote/$fakeId")

                coVerify {
                    mockEntity.id = fakeId
                    quoteRepository.save(eq(mockEntity))
                }
            }

            test("204") {
                // ARRANGE
                val fakeId = "63495ac2eb0bb2a94bb3e512"
                val mockEntity = mockk<QuoteEntity>(relaxed = true)
                coEvery { quoteRepository.findById(eq(fakeId)) } returns mockEntity
                coEvery { quoteRepository.save(any()) } returns mockEntity

                // ACT & ASSERT
                webClient
                    .mutateWith(mockUser().roles("ADMIN"))
                    .put()
                    .uri("/quote/$fakeId")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Mono.just(dto), QuoteDto::class.java)
                    .exchange()
                    .expectStatus().isNoContent

                coVerify {
                    mockEntity.text = dto.text
                    mockEntity.author = dto.author
                    mockEntity.tags = dto.tags ?: listOf()
                    mockEntity.work = dto.work
                    quoteRepository.save(mockEntity)
                }
            }
        }

        context("v1DeleteQuote") {
            test("204") {
                // ARRANGE
                val fakeId = "634966b6fd26520899d5b995"
                coEvery { quoteRepository.existsById(eq(fakeId)) } returns true
                coEvery { quoteRepository.deleteById(eq(fakeId)) } just runs

                // ACT & ASSERT
                webClient
                    .mutateWith(mockUser().roles("ADMIN"))
                    .delete()
                    .uri("/quote/$fakeId")
                    .exchange()
                    .expectStatus().isNoContent

                coVerify { quoteRepository.deleteById(eq(fakeId)) }
            }

            test("404") {
                // ARRANGE
                val fakeId = "634967cb5938cee7481268f5"
                coEvery { quoteRepository.existsById(eq(fakeId)) } returns false

                // ACT & ASSERT
                webClient
                    .mutateWith(mockUser().roles("ADMIN"))
                    .delete()
                    .uri("/quote/$fakeId")
                    .exchange()
                    .expectStatus().isNotFound
            }
        }

        test("v1RandomQuote") {
            // ARRANGE
            coEvery { quoteRepository.getRandom() } returns entity

            // ACT & ASSERT
            webClient.get()
                .uri("/quote/random")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isSeeOther
                .expectHeader()
                .location("/quote/${entity.id}")

            coVerify { quoteRepository.getRandom() }
        }

        context("v1ToggleQuoteLike") {
            // TODO: Test error cases
            test("200") {
                // ARRANGE
                val fakeUserId = "63497d171b7c64ed35ce57b7"
                val fakeQuoteId = "63497e3699b55ab8837623aa"
                coEvery { likedQuoteService.toggleLike(eq(fakeUserId), eq(fakeQuoteId)) } returns true
                coEvery { userService.findOrRegisterUser(any()) } returns mockk()

                // ACT & ASSERT
                webClient
                    .mutateWith(mockJwt().jwt {
                        it.subject(fakeUserId)
                    })
                    .post()
                    .uri("/quote/$fakeQuoteId/like/toggle")
                    .exchange()
                    .expectStatus().isOk

                coVerify { likedQuoteService.toggleLike(eq(fakeUserId), eq(fakeQuoteId)) }
            }
        }
    }
})
