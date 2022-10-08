package xyz.haff.quoteapi.data.repository

import org.springframework.data.mongodb.repository.Aggregation
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono
import xyz.haff.quoteapi.data.entity.QuoteEntity
import java.util.UUID

@Repository
interface QuoteRepository : ReactiveMongoRepository<QuoteEntity, String> {

    @Aggregation(pipeline = [
        "{ '\$sample':  { size:  1 } }"
    ])
    fun getRandom(): Mono<QuoteEntity>
}