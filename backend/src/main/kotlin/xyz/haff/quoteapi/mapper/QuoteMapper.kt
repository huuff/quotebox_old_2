package xyz.haff.quoteapi.mapper

import org.mapstruct.Mapper
import org.springframework.stereotype.Component
import xyz.haff.quoteapi.data.entity.QuoteEntity
import xyz.haff.quoteapi.dto.QuoteDto

@Component
@Mapper(componentModel = "spring")
interface QuoteMapper {
    fun quoteEntityToQuoteDto(entity: QuoteEntity): QuoteDto
}