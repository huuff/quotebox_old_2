package xyz.haff.quoteapi.data.repository

import io.kotest.matchers.collections.shouldBeIn
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest
import org.springframework.context.annotation.Import
import org.testcontainers.junit.jupiter.Testcontainers
import xyz.haff.quoteapi.data.entity.QuoteEntity
import xyz.haff.quoteapi.testing.FunSpecWithTestData
import xyz.haff.quoteapi.testing.MongoContainerTest
import xyz.haff.quoteapi.testing.TestData
import xyz.haff.quoteapi.testing.TestDataService


@Testcontainers
@DataMongoTest
@Import(TestDataService::class)
@MongoContainerTest
class QuoteRepositoryTest(
    private val quoteRepository: QuoteRepository,
    testDataService: TestDataService,
) : FunSpecWithTestData(testDataService, {

    test("save and find") {
        val quote = QuoteEntity(author = "Author", text = "Text")

        val savedQuote = quoteRepository.save(quote)
        val retrievedQuote = quoteRepository.findById(savedQuote.id!!)

        retrievedQuote shouldBe savedQuote
    }

    context("random") {
        test("getRandom") {
            quoteRepository.getRandom() shouldBeIn TestData.quoteEntities
        }

        test("getRandomByAuthor") {
            // ARRANGE
            val author = TestData.randomQuote.entity.author!!

            // ACT
            val quote = quoteRepository.getRandomByAuthor(author)!!

            // ASSERT
            quote shouldBeIn TestData.quoteEntities
            quote.author shouldBe author
        }

        test("getRandomByTags") {
            // ARRANGE
            val tags = TestData.randomQuote.entity.tags

            // ACT
            val quote = quoteRepository.getRandomByTags(tags)!!

            // ASSERT
            quote shouldBeIn TestData.quoteEntities
            quote.tags shouldContainExactly tags
        }

        test("getRandomByAuthorAndTags") {
            // ARRANGE
            val (_, _, author, _, tags) = TestData.randomQuote.entity

            // ACT
            val quote = quoteRepository.getRandomByAuthorAndTags(author!!, tags)!!

            // ASSERT
            quote shouldBeIn TestData.quoteEntities
            quote.tags shouldContainExactly tags
            quote.author shouldBe author
        }
    }

    context("with like status") {
        test("finds liked") {
            val userEntity = TestData.userEntity
            val quoteEntity = TestData.userEntity.likedQuotes[0]

            val quoteDto = quoteRepository.findWithLikeStatus(quoteEntity.id!!, userEntity.id!!)

            quoteDto!!.liked shouldBe true
        }

        test("find not liked") {
            val userEntity = TestData.userEntity
            val quoteEntity = TestData.quoteNotLikedByUser

            val quoteDto = quoteRepository.findWithLikeStatus(quoteEntity.id!!, userEntity.id!!)

            quoteDto!!.liked shouldBe false
        }
    }

})