package xyz.haff.quoteapi.data.repository

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldBeIn
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest
import org.springframework.context.annotation.Import
import org.testcontainers.junit.jupiter.Testcontainers
import xyz.haff.quoteapi.data.entity.QuoteEntity
import xyz.haff.quoteapi.testing.MongoContainerTest
import xyz.haff.quoteapi.testing.TestDataService

@Testcontainers
@DataMongoTest
@Import(RandomQuoteRepository::class, TestDataService::class)
@MongoContainerTest
class RandomQuoteRepositoryTest(
    private val randomQuoteRepository: RandomQuoteRepository,
    private val testData: TestDataService,
) : FunSpec({

    beforeEach {
        testData.load()
    }

    test("getOne") {
        randomQuoteRepository.getOne().awaitSingle() shouldBeIn testData.quotes
    }

    afterEach {
        testData.clear()
    }
})
