package xyz.haff.quoteapi.data.repository

import org.springframework.data.mongodb.repository.ExistsQuery
import org.springframework.data.mongodb.repository.Query
import org.springframework.data.mongodb.repository.Update
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import xyz.haff.quoteapi.data.MongoOperators.addToSet
import xyz.haff.quoteapi.data.MongoOperators.pull
import xyz.haff.quoteapi.data.entity.UserEntity

interface UserRepository : CoroutineCrudRepository<UserEntity, String> {

    @Query("{ '_id':  '?0' }")
    @Update("{ '$addToSet': { 'liked_quotes': ObjectId('?1') } }")
    suspend fun addLikedQuote(userId: String, likedQuoteId: String): Long

    @Query("{ '_id':  '?0'}")
    @Update("{ '$pull': { 'liked_quotes': ObjectId('?1') } }")
    suspend fun removeLikedQuote(userId: String, likedQuoteId: String): Long

    @ExistsQuery("{ '_id': '?0', 'liked_quotes': ObjectId('?1') }")
    suspend fun hasLikedQuote(userId: String, quoteId: String): Boolean

}