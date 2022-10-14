package xyz.haff.quoteapi.data.entity

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.DocumentReference

@Document(collection = "users")
data class UserEntity(
    @Id var id: String? = null,
    @DocumentReference var likedQuotes: List<QuoteEntity> = listOf()
)