package xyz.haff.quoteapi.data.repository

import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository
import xyz.haff.quoteapi.data.entity.QuoteEntity
import java.util.UUID

@Repository
interface QuoteRepository : ReactiveMongoRepository<QuoteEntity, UUID> {

}