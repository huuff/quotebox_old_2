package xyz.haff.quoteapi.util

import xyz.haff.quoteapi.dto.ValidationErrorDto

// TODO: Test?
fun createValidationError(
    path: String,
    type: ValidationErrorDto.Type,
    parameter: Any? = null
): ValidationErrorDto {
    if (parameter == null && type == ValidationErrorDto.Type.MUST_BE_LONGER_THAN) {
        throw IllegalArgumentException("A parameter must be provided for a validation error of type $type")
    }

    return ValidationErrorDto(
        path = path,
        type = type,
        parameter = parameter?.toString(),
        description = when (type) {
            ValidationErrorDto.Type.MISSING -> "Parameter $path must not be null"
            ValidationErrorDto.Type.MUST_BE_LONGER_THAN -> "Parameter $path must be longer than $parameter"
            ValidationErrorDto.Type.UNKNOWN -> "Unknown validation error"
        }
    )
}