package xyz.haff.quoteapi.data.entity

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.DocumentReference
import org.springframework.data.mongodb.core.mapping.Field

@Document(collection = UserEntity.COLLECTION_NAME)
data class UserEntity(
    @Id var id: String? = null,
    @DocumentReference @Field("liked_quotes") var likedQuotes: List<QuoteEntity> = listOf()
) {

    companion object {
        const val COLLECTION_NAME = "users"
    }
}