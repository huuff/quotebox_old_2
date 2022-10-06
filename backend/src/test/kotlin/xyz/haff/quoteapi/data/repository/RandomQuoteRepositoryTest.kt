package xyz.haff.quoteapi.data.repository

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest
import org.springframework.context.annotation.Import
import org.testcontainers.junit.jupiter.Testcontainers
import xyz.haff.quoteapi.data.entity.QuoteEntity
import xyz.haff.quoteapi.testing.MongoContainerTest

@Testcontainers
@DataMongoTest
@Import(RandomQuoteRepository::class)
@MongoContainerTest
class RandomQuoteRepositoryTest(
    private val randomQuoteRepository: RandomQuoteRepository,
    private val quoteRepository: QuoteRepository,
) : FunSpec({

    test("getOne") {
        val sampleQuote = QuoteEntity("test", "test", "test")

        quoteRepository.save(sampleQuote).block()

        randomQuoteRepository.getOne().block()!! shouldBe sampleQuote
    }
})
