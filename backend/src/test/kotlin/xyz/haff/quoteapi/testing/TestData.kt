package xyz.haff.quoteapi.testing

import xyz.haff.quoteapi.data.entity.QuoteEntity
import xyz.haff.quoteapi.dto.QuoteDto

object TestData {
    val entities = listOf(
        QuoteEntity(id = "63404783b4b3d2f9bda61af0", text = "text1", author = "author1", work = "work1", tags = listOf("tag1", "tag2", "tag3")),
        QuoteEntity(id = "634047a6f518c0b74de38585", text = "text2", author = "author2", work = "work2", tags = listOf("tag2", "tag3")),
        QuoteEntity(id = "634047ac879abd4d07364386", text = "text3", author = "author3", work = "work3", tags = listOf("tag3")),
    )

    val dtos = listOf(
        QuoteDto(text = "text1", author = "author1", work = "work1", tags = listOf("tag1", "tag2", "tag3")),
        QuoteDto(text = "text2", author = "author2", work = "work2", tags = listOf("tag2", "tag3")),
        QuoteDto(text = "text3", author = "author3", work = "work3", tags = listOf("tag3")),
    )

    data class EntityAndDto(val entity: QuoteEntity, val dto: QuoteDto)
    val random get(): EntityAndDto {
        val index = entities.indices.random()

        return EntityAndDto(
            entity = entities[index],
            dto = dtos[index],
        )
    }
}