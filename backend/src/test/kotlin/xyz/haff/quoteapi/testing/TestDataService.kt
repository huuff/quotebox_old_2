package xyz.haff.quoteapi.testing

import io.kotest.core.spec.style.FunSpec
import kotlinx.coroutines.reactive.awaitLast
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.stereotype.Service
import xyz.haff.quoteapi.data.repository.QuoteRepository

@Service
class TestDataService(
    private val repository: QuoteRepository,
) {

    suspend fun load() {
        repository.saveAll(TestData.entities).awaitLast()
    }

    suspend fun clear() {
        repository.deleteAll().awaitSingleOrNull()
    }
}

