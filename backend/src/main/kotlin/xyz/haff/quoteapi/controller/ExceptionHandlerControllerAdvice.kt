package xyz.haff.quoteapi.controller

import com.fasterxml.jackson.module.kotlin.MissingKotlinParameterException
import org.springframework.core.codec.DecodingException
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.support.WebExchangeBindException
import xyz.haff.quoteapi.dto.ValidationErrorDto
import xyz.haff.quoteapi.util.createValidationError
import javax.validation.ConstraintViolation
import javax.validation.constraints.Size

@ControllerAdvice
class ExceptionHandlerControllerAdvice {
    @ResponseBody
    @ExceptionHandler(DecodingException::class)
    suspend fun decodingError(e: DecodingException): ResponseEntity<List<ValidationErrorDto>> {
        val result = when (val cause = e.cause) {
            is MissingKotlinParameterException -> createValidationError(
                cause.parameter.name!!,
                ValidationErrorDto.Type.MISSING
            )

            else -> createValidationError("unknown", ValidationErrorDto.Type.UNKNOWN)
        }

        return ResponseEntity
            .badRequest()
            .contentType(MediaType.APPLICATION_JSON)
            .body(listOf(result))
    }


    @ResponseBody
    @ExceptionHandler(WebExchangeBindException::class)
    suspend fun bindException(e: WebExchangeBindException): ResponseEntity<List<ValidationErrorDto>> {
        val errors = e.bindingResult.fieldErrors.map { fieldError ->
            val constraintViolation = fieldError?.unwrap(ConstraintViolation::class.java)!!

            val path = constraintViolation.propertyPath.toString()
            val descriptor = constraintViolation.constraintDescriptor
            val annotation = descriptor.annotation
            when {
                annotation is Size && descriptor.attributes["min"] != 0 -> createValidationError(
                        path = path,
                        type = ValidationErrorDto.Type.MUST_BE_LONGER_THAN,
                        parameter = descriptor.attributes["min"]
                    )
                else -> createValidationError(
                    path = path,
                    type = ValidationErrorDto.Type.UNKNOWN
                )
            }
        }


        return ResponseEntity
            .badRequest()
            .contentType(MediaType.APPLICATION_JSON)
            .body(errors)
    }
}