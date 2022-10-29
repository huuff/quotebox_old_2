package xyz.haff.quoteapi.data.repository

import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.reactive.awaitSingleOrNull
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.aggregation.Aggregation.*
import org.springframework.data.mongodb.core.aggregation.Fields
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.isEqualTo
import org.springframework.stereotype.Repository
import xyz.haff.quoteapi.data.MongoOperators.first
import xyz.haff.quoteapi.data.MongoOperators.getField
import xyz.haff.quoteapi.data.MongoOperators.lookup
import xyz.haff.quoteapi.data.MongoOperators.match
import xyz.haff.quoteapi.data.MongoOperators.project
import xyz.haff.quoteapi.data.NativeAggregationOperation
import xyz.haff.quoteapi.data.entity.QuoteEntity
import xyz.haff.quoteapi.data.entity.UserEntity
import xyz.haff.quoteapi.dto.QuoteDto

@Repository
class QuoteWithLikeRepository(
    private val mongo: ReactiveMongoTemplate,
) {

    // TODO: Is this vulnerable to injection?
    suspend fun find(quoteId: String, userId: String): QuoteDto? {
        return mongo.aggregate(
            newAggregation(
                match(Criteria("_id").`is`(quoteId)),
                NativeAggregationOperation(
                    """
                {
                 $lookup: {
                  from: '${UserEntity.COLLECTION_NAME}',
                  'let': {
                   quote_id: '${"$"}_id'
                  },
                  pipeline: [
                   {
                    $match: {
                     _id: ObjectId('$userId')
                    }
                   },
                   {
                    $project: {
                     quote_id: '${"$$"}quote_id',
                     liked: {
                      ${'$'}in: [
                       '${"$$"}quote_id',
                       '${"$"}liked_quotes'
                      ]
                     }
                    }
                   }
                  ],
                  as: 'intermediate_result'
                 }
                }
            """.trimIndent()
                ),
                NativeAggregationOperation(
                    """
                {  
                   "$project": {
                      _id: 1,
                      text: 1,
                      author: 1,
                      work: 1,
                      tags: 1,
                      liked: {
                       $getField: {
                        field: 'liked',
                        input: {
                         $first: '${"$"}intermediate_result'
                        }
                       }
                      }
                   }
                }
            """.trimIndent()
                )
            ),
            QuoteEntity.COLLECTION_NAME,
            QuoteDto::class.java
        ).awaitFirstOrNull()
    }
}