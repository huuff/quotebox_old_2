package xyz.haff.quoteapi.data.repository

import io.kotest.inspectors.shouldForNone
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest
import org.springframework.context.annotation.Import
import org.testcontainers.junit.jupiter.Testcontainers
import xyz.haff.quoteapi.testing.FunSpecWithTestData
import xyz.haff.quoteapi.testing.MongoContainerTest
import xyz.haff.quoteapi.testing.TestData
import xyz.haff.quoteapi.testing.TestDataService

@Testcontainers
@DataMongoTest
@Import(TestDataService::class)
@MongoContainerTest
class UserRepositoryTest(
    testDataService: TestDataService,
    userRepository: UserRepository,
) : FunSpecWithTestData(testDataService, {

    test("retrieves an user with all of their liked quotes") {
        val user = userRepository.findById(TestData.userEntity.id!!).awaitSingleOrNull()

        user.shouldNotBeNull()
        user.likedQuotes shouldBe TestData.userEntity.likedQuotes
    }

    test("addLikedQuote") {
        // ARRANGE
        val quoteToLike = TestData.quoteNotLikedByUser
        val user = userRepository.findById(TestData.userEntity.id!!).awaitSingle()

        // SANITY CHECK
        user.likedQuotes.map { it.id } shouldNotContain quoteToLike.id

        // ACT
        val changedRecords = userRepository.addLikedQuote(user.id!!, quoteToLike.id!!).awaitSingle()

        // ASSERT
        changedRecords shouldBe 1
        userRepository.findById(user.id!!).awaitSingle().likedQuotes.map { it.id } shouldContain quoteToLike.id
    }

    test("removeLikedQuote") {
        // ARRANGE
        val user = userRepository.findById(TestData.userEntity.id!!).awaitSingle()
        val quoteToRemove = user.likedQuotes[0]

        // SANITY CHECK
        user.likedQuotes.map { it.id } shouldContain quoteToRemove.id

        // ACT
        val changedRecords = userRepository.removeLikedQuote(user.id!!, quoteToRemove.id!!).awaitSingle()

        // ASSERT
        changedRecords shouldBe 1
        userRepository.findById(user.id!!).awaitSingle().likedQuotes.map { it.id } shouldNotContain quoteToRemove.id
    }

    context("hasLikedQuote") {
        test("true") {
            userRepository.hasLikedQuote(
                TestData.userEntity.id!!,
                TestData.userEntity.likedQuotes[0].id!!
            ).awaitSingle() shouldBe true
        }

        test("false") {
            userRepository.hasLikedQuote(
                TestData.userEntity.id!!,
                TestData.quoteNotLikedByUser.id!!
            ).awaitSingle() shouldBe false
        }
    }

})