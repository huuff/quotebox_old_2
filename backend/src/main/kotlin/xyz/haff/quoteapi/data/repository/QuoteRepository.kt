package xyz.haff.quoteapi.data.repository

import org.springframework.data.mongodb.repository.Aggregation
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono
import xyz.haff.quoteapi.data.entity.QuoteEntity
import xyz.haff.quoteapi.data.MongoOperators.all
import xyz.haff.quoteapi.data.MongoOperators.match
import xyz.haff.quoteapi.data.MongoOperators.sample

@Repository
interface QuoteRepository : CoroutineCrudRepository<QuoteEntity, String> {

    @Aggregation(
        pipeline = [
            "{ '$sample':  { size:  1 } }",
        ]
    )
    suspend fun getRandom(): QuoteEntity

    @Aggregation(
        pipeline = [
            "{ '$match':  { author: '?0' }}",
            "{ '$sample':  { size:  1 } }",
        ]
    )
    suspend fun getRandomByAuthor(author: String): QuoteEntity?

    @Aggregation(
        pipeline = [
            "{ '$match':  { tags: {'$all': [?0] }}}",
            "{ '$sample':  { size:  1 } }",
        ]
    )
    suspend fun getRandomByTags(tags: List<String>): QuoteEntity?

    @Aggregation(
        pipeline = [
            """{ '$match':  {
                author: '?0', 
                tags: {'$all': [?1] }
              }
            }
        """,
            "{ '$sample':  { size:  1 } }",
        ]
    )
    suspend fun getRandomByAuthorAndTags(author: String, tags: List<String>): QuoteEntity?

}