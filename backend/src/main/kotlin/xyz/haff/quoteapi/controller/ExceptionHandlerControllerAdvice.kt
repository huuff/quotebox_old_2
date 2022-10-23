package xyz.haff.quoteapi.controller

import com.fasterxml.jackson.module.kotlin.MissingKotlinParameterException
import org.hibernate.validator.internal.util.annotation.ConstraintAnnotationDescriptor
import org.springframework.core.codec.DecodingException
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.validation.beanvalidation.SpringValidatorAdapter
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.support.WebExchangeBindException
import xyz.haff.quoteapi.dto.ValidationErrorDto
import xyz.haff.quoteapi.util.createValidationError
import javax.validation.ConstraintViolation
import javax.validation.ConstraintViolationException
import javax.validation.constraints.Size

@ControllerAdvice
class ExceptionHandlerControllerAdvice {
    // TODO: Should return a list of validation errors

    @ResponseBody
    @ExceptionHandler(DecodingException::class)
    suspend fun decodingError(e: DecodingException): ResponseEntity<ValidationErrorDto> {
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
            .body(result)
    }

    // TODO: Do it better
    @ResponseBody
    @ExceptionHandler(WebExchangeBindException::class)
    suspend fun bindException(e: WebExchangeBindException): ResponseEntity<ValidationErrorDto> {
        val fieldError = e.bindingResult.fieldError
        val constraintViolation = fieldError?.unwrap(ConstraintViolation::class.java)!!
        val result = when (constraintViolation.constraintDescriptor.annotation) {
            is Size -> when {
                constraintViolation.constraintDescriptor.attributes["min"] != 0 -> createValidationError(
                    path = constraintViolation.propertyPath.toString(),
                    type = ValidationErrorDto.Type.MUST_BE_LONGER_THAN,
                    parameter = constraintViolation.constraintDescriptor.attributes["min"]
                )
                else -> createValidationError(
                    path = constraintViolation.propertyPath.toString(),
                    type = ValidationErrorDto.Type.UNKNOWN
                )
            }
            else -> createValidationError(
                path = constraintViolation.propertyPath.toString(),
                type = ValidationErrorDto.Type.UNKNOWN
            )
        }

        return ResponseEntity
            .badRequest()
            .contentType(MediaType.APPLICATION_JSON)
            .body(result)
    }
}