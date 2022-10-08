package xyz.haff.quoteapi.mapper

import org.mapstruct.Mapper
import xyz.haff.quoteapi.data.entity.QuoteEntity
import xyz.haff.quoteapi.dto.QuoteDto

@Mapper
interface QuoteMapper {
    fun quoteEntityToQuoteDto(entity: QuoteEntity): QuoteDto
}