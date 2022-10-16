package xyz.haff.quoteapi.mapper

import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.NullValuePropertyMappingStrategy
import org.mapstruct.ObjectFactory
import org.mapstruct.TargetType
import xyz.haff.quoteapi.data.entity.QuoteEntity
import xyz.haff.quoteapi.dto.QuoteDto

@Mapper
interface QuoteMapper {
    @Mapping(target = "liked", ignore = true)
    fun entityToDto(entity: QuoteEntity): QuoteDto

    @Mapping(target = "id", ignore = true)
    @Mapping(
        source = "tags",
        target = "tags",
        defaultExpression = "java(new java.util.ArrayList<String>())"
    )
    fun dtoToEntity(dto: QuoteDto): QuoteEntity
}