package xyz.haff.quoteapi

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Profile
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Component
import xyz.haff.quoteapi.data.entity.QuoteEntity

@Component
@Profile("demo")
class Bootstrap(
    private val mongoTemplate: ReactiveMongoTemplate,
    private val objectMapper: ObjectMapper,
) : CommandLineRunner {
    override fun run(vararg args: String?) {
        if (mongoTemplate.count(Query(), QuoteEntity::class.java).block()!! != 0L) return

        val entities =
            objectMapper.readValue(Bootstrap::class.java.classLoader.getResource("data.json"), object : TypeReference<List<QuoteEntity>>() {})

        mongoTemplate.insert(entities, QuoteEntity::class.java).blockLast()
    }
}