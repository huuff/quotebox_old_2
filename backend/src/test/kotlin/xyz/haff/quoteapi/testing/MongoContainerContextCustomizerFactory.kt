package xyz.haff.quoteapi.testing

import org.springframework.context.ConfigurableApplicationContext
import org.springframework.core.annotation.AnnotatedElementUtils
import org.springframework.core.env.MapPropertySource
import org.springframework.test.context.ContextConfigurationAttributes
import org.springframework.test.context.ContextCustomizer
import org.springframework.test.context.ContextCustomizerFactory
import org.springframework.test.context.MergedContextConfiguration
import org.testcontainers.containers.MongoDBContainer

class MongoContainerContextCustomizerFactory : ContextCustomizerFactory {

    override fun createContextCustomizer(
        testClass: Class<*>,
        configAttributes: MutableList<ContextConfigurationAttributes>
    ): ContextCustomizer? = if (AnnotatedElementUtils.hasAnnotation(testClass, MongoContainerTest::class.java)) {
        MongoContainerTestContextCustomizer()
    } else { null }

    private class MongoContainerTestContextCustomizer : ContextCustomizer {
        override fun customizeContext(
            context: ConfigurableApplicationContext,
            mergedConfig: MergedContextConfiguration
        ) {
            // TODO: Specific digest
            // TODO: Is this container actually shared across all tests? Found my answer: No
            val mongoContainer = MongoDBContainer("mongo:focal").apply { this.start() }
            context.environment.propertySources.addFirst(MapPropertySource("MongoDB Testcontainer Properties",
                mapOf("spring.data.mongodb.uri" to mongoContainer.replicaSetUrl)
            ))
        }

    }
}