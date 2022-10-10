package xyz.haff.quoteapi.data.repository

import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.data.mongodb.repository.Aggregation
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono
import xyz.haff.quoteapi.data.entity.QuoteEntity
import java.util.UUID

@Repository
interface QuoteRepository : ReactiveMongoRepository<QuoteEntity, String> {

    @Aggregation(
        pipeline = [
            "{ '\$sample':  { size:  1 } }",
        ]
    )
    fun getRandom(): Mono<QuoteEntity>

    @Aggregation(
        pipeline = [
            "{ '\$match':  { author: '?0' }}",
            "{ '\$sample':  { size:  1 } }",
        ]
    )
    fun getRandomByAuthor(author: String): Mono<QuoteEntity>

    @Aggregation(
        pipeline = [
            "{ '\$match':  { tags: {'\$all': [?0] }}}",
            "{ '\$sample':  { size:  1 } }",
        ]
    )
    fun getRandomByTags(tags: List<String>): Mono<QuoteEntity>

    @Aggregation(
        pipeline = [
            """{ '${'$'}match':  {
                author: '?0', 
                tags: {'${'$'}all': [?1] }
              }
            }
        """,
            "{ '\$sample':  { size:  1 } }",
        ]
    )
    fun getRandomByAuthorAndTags(author: String, tags: List<String>): Mono<QuoteEntity>
}