package xyz.haff.quoteapi.data.repository

import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.aggregation.Aggregation
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono
import xyz.haff.quoteapi.data.entity.QuoteEntity

@Repository
class RandomQuoteRepository(
    private val mongoTemplate: ReactiveMongoTemplate,
) {
    fun getOne(): Mono<QuoteEntity> = mongoTemplate.aggregate(
        Aggregation.newAggregation(
            Aggregation.sample(1)
        ),
        "quotes", QuoteEntity::class.java
    ).next()
}