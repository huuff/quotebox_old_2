package xyz.haff.quoteapi.data.repository

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldBeIn
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.beans.factory.annotation.Autowired
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

        val savedQuote = quoteRepository.save(quote).awaitSingle()
        val retrievedQuote = quoteRepository.findById(savedQuote.id!!).awaitSingle()

        retrievedQuote shouldBe savedQuote
    }

    context("random") {
        test("getRandom") {
            quoteRepository.getRandom().awaitSingle() shouldBeIn TestData.entities
        }

        test("getRandomByAuthor") {
            // ARRANGE
            val author = TestData.random.entity.author

            // ACT
            val quote = quoteRepository.getRandomByAuthor(author).awaitSingle()

            // ASSERT
            quote shouldBeIn TestData.entities
            quote.author shouldBe author
        }

        test("getRandomByTags") {
            // ARRANGE
            val tags = TestData.random.entity.tags

            // ACT
            val quote = quoteRepository.getRandomByTags(tags).awaitSingle()

            // ASSERT
            quote shouldBeIn TestData.entities
            quote.tags shouldContainExactly tags
        }

        test("getRandomByAuthorAndTags") {
            // ARRANGE
            val (_, _, author, _, tags) = TestData.random.entity

            // ACT
            val quote = quoteRepository.getRandomByAuthorAndTags(author, tags).awaitSingle()

            // ASSERT
            quote shouldBeIn TestData.entities
            quote.tags shouldContainExactly tags
            quote.author shouldBe author
        }
    }

})