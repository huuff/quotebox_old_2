package xyz.haff.quoteapi.data.repository

import io.kotest.core.spec.style.FunSpec
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
@Import(TestDataService::class, QuoteWithLikeRepository::class)
@MongoContainerTest
class QuoteWithLikeRepositoryTest(
    private val quoteWithLikeRepository: QuoteWithLikeRepository,
    testDataService: TestDataService,
) : FunSpecWithTestData(testDataService, {

    test("finds liked") {
        val userEntity = TestData.userEntity
        val quoteEntity = TestData.userEntity.likedQuotes[0]

        val quoteDto = quoteWithLikeRepository.find(quoteEntity.id!!, userEntity.id!!)

        quoteDto!!.liked shouldBe true
    }

    test("find not liked") {
        val userEntity = TestData.userEntity
        val quoteEntity = TestData.quoteNotLikedByUser

        val quoteDto = quoteWithLikeRepository.find(quoteEntity.id!!, userEntity.id!!)

        quoteDto!!.liked shouldBe false
    }
})
