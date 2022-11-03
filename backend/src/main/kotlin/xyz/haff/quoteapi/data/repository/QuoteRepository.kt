package xyz.haff.quoteapi.data.repository

import org.springframework.data.mongodb.repository.Aggregation
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository
import xyz.haff.quoteapi.data.MongoOperators.all
import xyz.haff.quoteapi.data.MongoOperators.first
import xyz.haff.quoteapi.data.MongoOperators.getField
import xyz.haff.quoteapi.data.MongoOperators.`in`
import xyz.haff.quoteapi.data.MongoOperators.lookup
import xyz.haff.quoteapi.data.MongoOperators.match
import xyz.haff.quoteapi.data.MongoOperators.project
import xyz.haff.quoteapi.data.MongoOperators.sample
import xyz.haff.quoteapi.data.entity.QuoteEntity
import xyz.haff.quoteapi.data.entity.UserEntity
import xyz.haff.quoteapi.dto.QuoteDto

@Repository
interface QuoteRepository : CoroutineCrudRepository<QuoteEntity, String> {

    @Aggregation(
        pipeline = [
            "{ '$sample':  { size:  1 } }",
            "{ '$project':  { _id:  1 } }",
        ]
    )
    suspend fun getRandomId(): String

    @Aggregation(
        pipeline = [
            "{ '$match':  { author: '?0' }}",
            "{ '$sample':  { size:  1 } }",
            "{ '$project':  { _id:  1 } }",
        ]
    )
    suspend fun getRandomIdByAuthor(author: String): String?

    @Aggregation(
        pipeline = [
            "{ '$match':  { tags: {'$all': [?0] }}}",
            "{ '$sample':  { size:  1 } }",
            "{ '$project':  { _id:  1 } }",
        ]
    )
    suspend fun getRandomIdByTags(tags: List<String>): String?

    @Aggregation(
        pipeline = [
            """{ '$match':  {
                author: '?0', 
                tags: {'$all': [?1] }
              }
            }
        """,
            "{ '$sample':  { size:  1 } }",
            "{ '$project':  { _id:  1 } }",
        ]
    )
    suspend fun getRandomIdByAuthorAndTags(author: String, tags: List<String>): String?

    // TODO: Use it where appropriate
    @Aggregation(
        pipeline = [
            "{ $match: { _id: ?0} }",
            """{
                 $lookup: {
                  from: ${UserEntity.COLLECTION_NAME},
                  'let': {
                   quote_id: '${'$'}_id'
                  },
                  pipeline: [
                   {
                    $match: {
                     _id: ObjectId('?1')
                    }
                   },
                   {
                    $project: {
                     quote_id: '${"$$"}quote_id',
                     liked: {
                      ${`in`}: [
                       '${"$$"}quote_id',
                       '${'$'}liked_quotes'
                      ]
                     }
                    }
                   }
                  ],
                  as: 'intermediate_result'
                 }
                }""",
            """
                {
                 $project: {
                  _id: 1,
                  text: 1,
                  author: 1,
                  work: 1,
                  tags: 1,
                  liked: {
                   $getField: {
                    field: 'liked',
                    input: {
                     $first: '${'$'}intermediate_result'
                    }
                   }
                  }
                 }
                }
            """
        ]
    )
    suspend fun findWithLikeStatus(quoteId: String, userId: String): QuoteDto?
}