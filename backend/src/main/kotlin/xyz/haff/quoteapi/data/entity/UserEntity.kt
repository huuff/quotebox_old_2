package xyz.haff.quoteapi.data.entity

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.DocumentReference
import org.springframework.data.mongodb.core.mapping.Field

@Document(collection = "users")
data class UserEntity(
    @Id var id: String? = null,
    @DocumentReference @Field("liked_quotes") var likedQuotes: List<QuoteEntity> = listOf()
)