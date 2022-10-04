package xyz.haff.quoteapi.testing

import org.testcontainers.containers.MongoDBContainer


object TestMongoDatabase {
    // TODO: specific version or digest
    @JvmStatic
    val container = MongoDBContainer("mongo:focal").apply {
        this.start()
    }
}