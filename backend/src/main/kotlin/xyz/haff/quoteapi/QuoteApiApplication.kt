package xyz.haff.quoteapi

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class QuoteApiApplication

fun main(args: Array<String>) {
    runApplication<QuoteApiApplication>(*args)
}