package xyz.haff.quoteapi.data.repository

import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest
import org.springframework.context.annotation.Import
import org.testcontainers.junit.jupiter.Testcontainers
import xyz.haff.quoteapi.testing.FunSpecWithTestData
import xyz.haff.quoteapi.testing.MongoContainerTest
import xyz.haff.quoteapi.testing.TestData
import xyz.haff.quoteapi.testing.TestDataService

@Testcontainers
@DataMongoTest
@Import(TestDataService::class)
@MongoContainerTest
class UserRepositoryTest(
    testDataService: TestDataService,
    userRepository: UserRepository,
) : FunSpecWithTestData(testDataService, {

    test("retrieves an user with all of their liked quotes") {
        val user = userRepository.findById(TestData.userEntity.id!!).awaitSingleOrNull()

        user.shouldNotBeNull()
        user.likedQuotes shouldBe TestData.userEntity.likedQuotes
    }

})