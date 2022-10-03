package xyz.haff.quoteapi.data.entity

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*

@Document
data class QuoteEntity(
    @Id var id: UUID? = null,
    var author: String,
    var text: String,
    var work: String? = null,
    var tags: List<String> = listOf(),
)
