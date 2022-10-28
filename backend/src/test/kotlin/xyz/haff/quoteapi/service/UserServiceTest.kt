package xyz.haff.quoteapi.service

import com.ninjasquad.springmockk.MockkBean
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.*
import kotlinx.coroutines.reactor.mono
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.context.annotation.Import
import org.springframework.test.context.junit.jupiter.SpringExtension
import reactor.core.publisher.Mono
import xyz.haff.quoteapi.data.entity.QuoteEntity
import xyz.haff.quoteapi.data.entity.UserEntity
import xyz.haff.quoteapi.data.repository.UserRepository

@ExtendWith(SpringExtension::class)
@Import(UserService::class)
class UserServiceTest(
    @MockkBean private val userRepository: UserRepository,
    private val userService: UserService,
) : FunSpec({
    val fakeUserId = "fakeid"

    context("findOrRegisterUser") {
        test("finds") {
            // ARRANGE
            val fakeEntity = UserEntity(id = fakeUserId)
            coEvery { userRepository.findById(eq(fakeUserId)) } returns fakeEntity

            // ACT
            val retrieved = userService.findOrRegisterUser(fakeUserId)

            // ASSERT
            coVerify { userRepository.findById(eq(fakeUserId)) }
            coVerify { userRepository.save(eq(fakeEntity)) wasNot called }
            retrieved shouldBe fakeEntity
        }

        test("doesn't find") {
            // ARRANGE
            val fakeEntity = UserEntity(id = fakeUserId)
            coEvery { userRepository.findById(eq(fakeUserId)) } returns null
            coEvery { userRepository.save(eq(fakeEntity)) } returns fakeEntity

            // ACT
            val saved = userService.findOrRegisterUser(fakeUserId)

            // ASSERT
            coVerify { userRepository.findById(eq(fakeUserId)) }
            coVerify { userRepository.save(eq(fakeEntity)) }
            saved shouldBe fakeEntity
        }
    }

    test("registerUser") {
        // ARRANGE
        coEvery { userRepository.save(any()) } returns mockk()

        // ACT
        userService.registerUser(fakeUserId)

        // ASSERT
        coVerify { userRepository.save(withArg {
            it.id shouldBe fakeUserId
        }) }
    }
})
