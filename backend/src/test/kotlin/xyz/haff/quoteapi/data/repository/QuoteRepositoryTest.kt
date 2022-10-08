package xyz.haff.quoteapi.data.repository

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldBeIn
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

    test("getRandom") {
        quoteRepository.getRandom().awaitSingle() shouldBeIn TestData.entities
    }
})