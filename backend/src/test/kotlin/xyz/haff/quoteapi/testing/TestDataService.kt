package xyz.haff.quoteapi.testing

import kotlinx.coroutines.reactive.awaitLast
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.stereotype.Service
import xyz.haff.quoteapi.data.repository.QuoteRepository

// TODO: I should maybe test it without a repository because then if any repository-related config is wrong all tests
// that depend on it will fail during initialization, which is harder to understand
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

