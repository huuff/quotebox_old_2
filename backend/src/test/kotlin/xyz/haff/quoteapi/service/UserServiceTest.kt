package xyz.haff.quoteapi.service

import com.ninjasquad.springmockk.MockkBean
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.*
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
            every { userRepository.findById(eq(fakeUserId)) } returns Mono.just(fakeEntity)

            // ACT
            val retrieved = userService.findOrRegisterUser(fakeUserId)

            // ASSERT
            verify { userRepository.findById(eq(fakeUserId)) }
            verify { userRepository.insert(eq(fakeEntity)) wasNot called }
            retrieved shouldBe fakeEntity
        }

        test("doesn't find") {
            // ARRANGE
            val fakeEntity = UserEntity(id = fakeUserId)
            every { userRepository.findById(eq(fakeUserId)) } returns Mono.empty()
            every { userRepository.insert(eq(fakeEntity)) } returns Mono.just(fakeEntity)

            // ACT
            val saved = userService.findOrRegisterUser(fakeUserId)

            // ASSERT
            verify { userRepository.findById(eq(fakeUserId)) }
            verify { userRepository.insert(eq(fakeEntity)) }
            saved shouldBe fakeEntity
        }
    }

    test("registerUser") {
        // ARRANGE
        every { userRepository.insert(any<UserEntity>()) } returns Mono.just(mockk())

        // ACT
        userService.registerUser(fakeUserId)

        // ASSERT
        verify { userRepository.insert(withArg<UserEntity> {
            it.id shouldBe fakeUserId
        }) }
    }
})
