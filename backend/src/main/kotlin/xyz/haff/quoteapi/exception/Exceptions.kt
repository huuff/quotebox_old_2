package xyz.haff.quoteapi.exception

import org.springframework.http.ResponseEntity

interface NotFoundException {
    fun <T> toResponseEntity(): ResponseEntity<T> = ResponseEntity.notFound().build()
}

class UserNotFoundException(val userId: String)
    : RuntimeException("User with id $userId does not exist"), NotFoundException

class QuoteNotFoundException(val quoteId: String)
    : RuntimeException("Quote with id $quoteId does not exist"), NotFoundException