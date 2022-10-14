package xyz.haff.quoteapi.testing

import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Service
import xyz.haff.quoteapi.data.entity.QuoteEntity
import xyz.haff.quoteapi.data.entity.UserEntity

@Service
class TestDataService(
    private val mongoTemplate: ReactiveMongoTemplate,
) {
    suspend fun load() {
        mongoTemplate.insert(TestData.quoteEntities, QuoteEntity::class.java).awaitFirstOrNull()
        mongoTemplate.insert(TestData.userEntity).awaitSingleOrNull()
    }
    suspend fun clear() {
        mongoTemplate.remove(Query(), QuoteEntity::class.java).awaitSingleOrNull()
        mongoTemplate.remove(Query(), UserEntity::class.java).awaitSingleOrNull()
    }
}

