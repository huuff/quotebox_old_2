package xyz.haff.quoteapi.data.repository

import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
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
        val user = userRepository.findById(TestData.userEntity.id!!)

        user.shouldNotBeNull()
        user.likedQuotes shouldBe TestData.userEntity.likedQuotes
    }

    test("addLikedQuote") {
        // ARRANGE
        val quoteToLike = TestData.quoteNotLikedByUser
        val user = userRepository.findById(TestData.userEntity.id!!)!!

        // SANITY CHECK
        user.likedQuotes.map { it.id } shouldNotContain quoteToLike.id

        // ACT
        val changedRecords = userRepository.addLikedQuote(user.id!!, quoteToLike.id!!)

        // ASSERT
        changedRecords shouldBe 1
        userRepository.findById(user.id!!)!!.likedQuotes.map { it.id } shouldContain quoteToLike.id
    }

    test("removeLikedQuote") {
        // ARRANGE
        val user = userRepository.findById(TestData.userEntity.id!!)!!
        val quoteToRemove = user.likedQuotes[0]

        // SANITY CHECK
        user.likedQuotes.map { it.id } shouldContain quoteToRemove.id

        // ACT
        val changedRecords = userRepository.removeLikedQuote(user.id!!, quoteToRemove.id!!)

        // ASSERT
        changedRecords shouldBe 1
        userRepository.findById(user.id!!)!!.likedQuotes.map { it.id } shouldNotContain quoteToRemove.id
    }

    context("hasLikedQuote") {
        test("true") {
            userRepository.hasLikedQuote(
                TestData.userEntity.id!!,
                TestData.userEntity.likedQuotes[0].id!!
            ) shouldBe true
        }

        test("false") {
            userRepository.hasLikedQuote(
                TestData.userEntity.id!!,
                TestData.quoteNotLikedByUser.id!!
            ) shouldBe false
        }
    }

})