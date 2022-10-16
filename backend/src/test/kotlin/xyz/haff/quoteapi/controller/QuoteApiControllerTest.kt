package xyz.haff.quoteapi.controller

import com.ninjasquad.springmockk.MockkBean
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.*
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.boot.autoconfigure.security.reactive.ReactiveSecurityAutoConfiguration
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
import xyz.haff.quoteapi.data.entity.QuoteEntity
import xyz.haff.quoteapi.data.repository.QuoteRepository
import xyz.haff.quoteapi.dto.QuoteDto
import xyz.haff.quoteapi.mapper.QuoteMapper
import xyz.haff.quoteapi.service.ToggleQuoteLikeService
import xyz.haff.quoteapi.testing.TestData

// TODO: Replace all of the Mono.just with mono { }? Does that work?
@WebFluxTest(
    controllers = [QuoteApiController::class],
    excludeAutoConfiguration = [
        ReactiveSecurityAutoConfiguration::class,
    ],
)
@Import(WebFluxSecurityConfig::class)
class QuoteApiControllerTest(
    private val webClient: WebTestClient,
    @MockkBean private val quoteRepository: QuoteRepository,
    @MockkBean private val quoteMapper: QuoteMapper,
    @MockkBean private val reactiveJwtDecoder: ReactiveJwtDecoder, // Prevents oAuth breakage
    @MockkBean private val toggleQuoteLikeService: ToggleQuoteLikeService,
) : FunSpec({
    val (entity, dto) = TestData.randomQuote

    context("v1GetQuote") {
        test("200 OK") {
            // ARRANGE
            every { quoteRepository.findById(any<String>()) } returns Mono.just(entity)
            every { quoteMapper.entityToDto(any()) } returns dto

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
            verify { quoteMapper.entityToDto(entity) }
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
            every { quoteMapper.dtoToEntity(any()) } returns entity
            every { quoteRepository.insert(eq(entity)) } returns Mono.just(entity)

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
    }

    context("v1UpdateQuote") {
        test("201") {
            // ARRANGE
            val fakeId = "63495ac2eb0bb2a94bb3e512"
            val mockEntity = mockk<QuoteEntity>(relaxed = true)
            every { quoteRepository.findById(eq(fakeId)) } returns Mono.empty()
            every { quoteMapper.dtoToEntity(eq(dto)) } returns mockEntity
            every { quoteRepository.insert(any<QuoteEntity>()) } returns Mono.just(mockEntity)

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

            verify {
                mockEntity.id = fakeId
                quoteRepository.insert(eq(mockEntity))
            }
        }

        test("204") {
            // ARRANGE
            val fakeId = "63495ac2eb0bb2a94bb3e512"
            val mockEntity = mockk<QuoteEntity>(relaxed = true)
            every { quoteRepository.findById(eq(fakeId)) } returns Mono.just(mockEntity)
            every { quoteRepository.save(any<QuoteEntity>()) } returns Mono.just(mockEntity)

            // ACT & ASSERT
            webClient
                .mutateWith(mockUser().roles("ADMIN"))
                .put()
                .uri("/quote/$fakeId")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(dto), QuoteDto::class.java)
                .exchange()
                .expectStatus().isNoContent

            verify {
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
            every { quoteRepository.existsById(eq(fakeId)) } returns Mono.just(true)
            every { quoteRepository.deleteById(eq(fakeId)) } returns Mono.empty()

            // ACT & ASSERT
            webClient
                .mutateWith(mockUser().roles("ADMIN"))
                .delete()
                .uri("/quote/$fakeId")
                .exchange()
                .expectStatus().isNoContent

            verify { quoteRepository.deleteById(eq(fakeId)) }
        }

        test("404") {
            // ARRANGE
            val fakeId = "634967cb5938cee7481268f5"
            every { quoteRepository.existsById(eq(fakeId)) } returns Mono.just(false)

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
        every { quoteRepository.getRandom() } returns Mono.just(entity)

        // ACT & ASSERT
        webClient.get()
            .uri("/quote/random")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isSeeOther
            .expectHeader()
            .location("/quote/${entity.id}")

        verify { quoteRepository.getRandom() }
    }

    context("v1ToggleQuoteLike") {
        // TODO: Test error cases
        test("200") {
            // ARRANGE
            val fakeUserId = "63497d171b7c64ed35ce57b7"
            val fakeQuoteId = "63497e3699b55ab8837623aa"
            coEvery { toggleQuoteLikeService.toggleQuoteLike(eq(fakeUserId), eq(fakeQuoteId)) } returns true

            // ACT & ASSERT
            webClient
                .mutateWith(mockJwt().jwt {
                    it.subject(fakeUserId)
                })
                .post()
                .uri("/quote/$fakeQuoteId/like/toggle")
                .exchange()
                .expectStatus().isOk

            coVerify { toggleQuoteLikeService.toggleQuoteLike(eq(fakeUserId), eq(fakeQuoteId)) }
        }
    }

})
