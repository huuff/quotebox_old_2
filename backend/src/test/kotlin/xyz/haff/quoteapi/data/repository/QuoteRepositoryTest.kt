package xyz.haff.quoteapi.data.repository

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest
import org.testcontainers.junit.jupiter.Testcontainers
import xyz.haff.quoteapi.data.entity.QuoteEntity
import xyz.haff.quoteapi.testing.MongoContainerTest


@Testcontainers
@DataMongoTest
@MongoContainerTest
class QuoteRepositoryTest : FunSpec() {
    @Autowired // TODO: try to add it from constructor
    private lateinit var quoteRepository: QuoteRepository

    init {
        test("can save and retrieve an entity") {
            val quote = QuoteEntity(author = "Author", text = "Text")

            val savedQuote = quoteRepository.save(quote).awaitSingle()
            val retrievedQuote = quoteRepository.findById(savedQuote.id!!).awaitSingle()

            retrievedQuote shouldBe savedQuote
        }
    }
}