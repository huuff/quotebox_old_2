package xyz.haff.quoteapi.data.entity

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*
@Document(collection = "quotes")
data class QuoteEntity(
    @Id var id: String? = null,
    var text: String,
    var author: String? = null,
    var work: String? = null,
    var tags: List<String> = listOf(),
)
