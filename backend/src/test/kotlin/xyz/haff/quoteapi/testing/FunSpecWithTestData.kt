package xyz.haff.quoteapi.testing

import io.kotest.core.annotation.Ignored
import io.kotest.core.spec.style.FunSpec

@Ignored
open class FunSpecWithTestData(testDataService: TestDataService, body: FunSpec.() -> Unit) : FunSpec({
    beforeEach {
        testDataService.load()
    }

    body()

    afterEach {
        testDataService.clear()
    }
})