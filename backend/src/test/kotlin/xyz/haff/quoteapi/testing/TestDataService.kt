package xyz.haff.quoteapi.testing

import io.kotest.core.spec.style.FunSpec
import kotlinx.coroutines.reactive.awaitLast
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.stereotype.Service
import xyz.haff.quoteapi.data.entity.QuoteEntity
import xyz.haff.quoteapi.data.repository.QuoteRepository

@Service
class TestDataService(
    private val repository: QuoteRepository,
) {

    val quotes = listOf(
        QuoteEntity(text = "text1", author = "author1", work = "work1", tags = listOf("tag1", "tag2", "tag3")),
        QuoteEntity(text = "text2", author = "author2", work = "work2", tags = listOf("tag2", "tag3")),
        QuoteEntity(text = "text3", author = "author3", work = "work3", tags = listOf("tag3")),
    )

    suspend fun load() {
        repository.saveAll(quotes).awaitLast()
    }

    suspend fun clear() {
        repository.deleteAll().awaitSingleOrNull()
    }
}

