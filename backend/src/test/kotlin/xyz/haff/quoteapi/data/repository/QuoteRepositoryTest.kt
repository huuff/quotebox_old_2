package xyz.haff.quoteapi.data.repository

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.MongoDBContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import xyz.haff.quoteapi.data.entity.QuoteEntity


// TODO: Hmmm why does it pick the SampleBean but not the QuoteRepository?
@Testcontainers
// TODO: Use DataMongoTest, maybe exclude embedded mongo
@SpringBootTest
class QuoteRepositoryTest : FunSpec() {
    @Autowired
    private lateinit var quoteRepository: QuoteRepository

    companion object {
        // TODO: specific version or digest
        @JvmStatic
        @Container
        val container = MongoDBContainer("mongo:focal").apply {
            this.start()
        }

        @JvmStatic
        @DynamicPropertySource
        fun mongoProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.data.mongodb.uri") { container.replicaSetUrl }
        }
    }

    init {
        test("can save and retrieve an entity") {
            val quote = QuoteEntity(author = "Author", text = "Text")

            // TODO: Try to use coroutines
            val savedQuote = quoteRepository.save(quote).block()!!
            val retrievedQuote = quoteRepository.findById(savedQuote.id!!).block()

            retrievedQuote shouldBe savedQuote
        }
    }
}