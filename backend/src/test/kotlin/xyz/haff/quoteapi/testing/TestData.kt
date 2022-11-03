package xyz.haff.quoteapi.testing

import xyz.haff.quoteapi.data.entity.QuoteEntity
import xyz.haff.quoteapi.data.entity.UserEntity
import xyz.haff.quoteapi.dto.QuoteDto

object TestData {
    val quoteEntities = listOf(
        QuoteEntity(id = "63404783b4b3d2f9bda61af0", text = "test quote 1", author = "author1", work = "work1", tags = listOf("tag1", "tag2", "tag3")),
        QuoteEntity(id = "634047a6f518c0b74de38585", text = "test quote 2", author = "author2", work = "work2", tags = listOf("tag2", "tag3")),
        QuoteEntity(id = "634047ac879abd4d07364386", text = "test quote 3", author = "author3", work = "work3", tags = listOf("tag3")),
    )

    val quoteDtos = listOf(
        QuoteDto(text = "test quote 1", author = "author1", work = "work1", tags = listOf("tag1", "tag2", "tag3")),
        QuoteDto(text = "test quote 2", author = "author2", work = "work2", tags = listOf("tag2", "tag3")),
        QuoteDto(text = "test quote 3", author = "author3", work = "work3", tags = listOf("tag3")),
    )

    val userEntity = UserEntity(
        id = "63496eefee427e052e33c826",
        likedQuotes = listOf(quoteEntities[0], quoteEntities[1]),
    )

    val quoteNotLikedByUser get() = quoteEntities[2]

    data class QuoteEntityAndDto(val entity: QuoteEntity, val dto: QuoteDto)
    val randomQuote get(): QuoteEntityAndDto {
        val index = quoteEntities.indices.random()

        return QuoteEntityAndDto(
            entity = quoteEntities[index],
            dto = quoteDtos[index],
        )
    }

    fun findQuote(id: String) = quoteEntities.find { it.id == id }
}