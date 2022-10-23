package xyz.haff.quoteapi.controller

import com.fasterxml.jackson.module.kotlin.MissingKotlinParameterException
import org.springframework.core.codec.DecodingException
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody
import xyz.haff.quoteapi.dto.ValidationErrorDto
import xyz.haff.quoteapi.util.createValidationError

@ControllerAdvice
class ExceptionHandlerControllerAdvice {

    // TODO: Also add handler for bean validation
    @ResponseBody
    @ExceptionHandler(DecodingException::class)
    suspend fun decodingError(e: DecodingException): ResponseEntity<ValidationErrorDto> {
        val result = when (val cause = e.cause) {
            is MissingKotlinParameterException -> createValidationError(cause.parameter.name!!, ValidationErrorDto.Type.MISSING)
            else -> createValidationError("unknown", ValidationErrorDto.Type.UNKNOWN)
        }

        return ResponseEntity
            .badRequest()
            .contentType(MediaType.APPLICATION_JSON)
            .body(result)
    }
}