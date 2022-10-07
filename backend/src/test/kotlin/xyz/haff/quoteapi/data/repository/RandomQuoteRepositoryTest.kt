package xyz.haff.quoteapi.data.repository

import io.kotest.matchers.collections.shouldBeIn
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest
import org.springframework.context.annotation.Import
import org.testcontainers.junit.jupiter.Testcontainers
import xyz.haff.quoteapi.testing.FunSpecWithTestData
import xyz.haff.quoteapi.testing.MongoContainerTest
import xyz.haff.quoteapi.testing.TestData
import xyz.haff.quoteapi.testing.TestDataService

@Testcontainers
@DataMongoTest
@Import(RandomQuoteRepository::class, TestDataService::class)
@MongoContainerTest
class RandomQuoteRepositoryTest(
    private val randomQuoteRepository: RandomQuoteRepository,
    private val testDataService: TestDataService,
) : FunSpecWithTestData(testDataService, {

    test("getOne") {
        randomQuoteRepository.getOne().awaitSingle() shouldBeIn TestData.entities
    }

})
