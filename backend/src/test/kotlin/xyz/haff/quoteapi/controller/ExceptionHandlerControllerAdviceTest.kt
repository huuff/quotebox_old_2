package xyz.haff.quoteapi.controller

import com.ninjasquad.springmockk.MockkBean
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.boot.autoconfigure.ImportAutoConfiguration
import org.springframework.boot.autoconfigure.security.reactive.ReactiveSecurityAutoConfiguration
import org.springframework.boot.autoconfigure.web.reactive.error.ErrorWebFluxAutoConfiguration
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockUser
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.returnResult
import xyz.haff.quoteapi.config.WebFluxSecurityConfig
import xyz.haff.quoteapi.config.validation.ValidationConfiguration
import xyz.haff.quoteapi.data.repository.QuoteRepository
import xyz.haff.quoteapi.data.repository.UserRepository
import xyz.haff.quoteapi.dto.ValidationErrorDto
import xyz.haff.quoteapi.mapper.QuoteMapper
import xyz.haff.quoteapi.service.ToggleQuoteLikeService
import xyz.haff.quoteapi.service.UserService
import xyz.haff.quoteapi.util.createValidationError

@WebFluxTest(
    controllers = [QuoteApiController::class],
    excludeAutoConfiguration = [
        ReactiveSecurityAutoConfiguration::class,  // TODO: Is it necessary?
    ],
)
@ImportAutoConfiguration(ErrorWebFluxAutoConfiguration::class)  // TODO: Is it necessary?
@Import(WebFluxSecurityConfig::class, ValidationConfiguration::class)
class ExceptionHandlerControllerAdviceTest(
    private val webClient: WebTestClient,
    // All mocked becaus the QuoteApiController needs them
    @MockkBean private val quoteRepository: QuoteRepository,
    @MockkBean private val quoteMapper: QuoteMapper,
    @MockkBean private val reactiveJwtDecoder: ReactiveJwtDecoder,
    @MockkBean private val userRepository: UserRepository,
    @MockkBean private val userService: UserService,
    @MockkBean private val toggleQuoteLikeService: ToggleQuoteLikeService,
) : FunSpec({

        test("text must not be null") {
            // ACT
            val error = webClient
                .mutateWith(mockUser().roles("ADMIN"))
                .post()
                .uri("/quote")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(
                    """
                        {
                          "author": "test"
                        }
                    """.trimIndent()
                )
                .exchange()
                .expectStatus().isBadRequest
                .returnResult<ValidationErrorDto>()
                .responseBody
                .awaitSingle()

            // ASSERT
            error shouldBe createValidationError(
                path = "text",
                type = ValidationErrorDto.Type.MISSING,
            )
        }

        test("text must have at least 10 characters") {
            // ACT
            val error = webClient
                .mutateWith(mockUser().roles("ADMIN"))
                .post()
                .uri("/quote")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(
                    """
                        {
                          "text": "short"
                        }
                    """.trimIndent()
                )
                .exchange()
                .expectStatus().isBadRequest
                .returnResult<ValidationErrorDto>()
                .responseBody
                .awaitSingle()

            // ASSERT
            error shouldBe createValidationError(
                path = "text",
                type = ValidationErrorDto.Type.MUST_BE_LONGER_THAN,
                parameter = 10,
            )
        }
})