package xyz.haff.quoteapi.data.repository

import org.springframework.data.mongodb.repository.Query
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.data.mongodb.repository.Update
import reactor.core.publisher.Mono
import xyz.haff.quoteapi.data.entity.UserEntity

interface UserRepository : ReactiveMongoRepository<UserEntity, String> {

    @Query("{ '_id':  '?0' }")
    @Update("{ '\$addToSet': { 'liked_quotes': ObjectId('?1') } }")
    fun addLikedQuote(userId: String, likedQuoteId: String): Mono<Long>


}