package com.matamichi.exception

import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidation(ex: MethodArgumentNotValidException): ProblemDetail {
        val detail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST)
        detail.title = "Validation Failed"
        detail.setProperty(
            "errors",
            ex.bindingResult.fieldErrors.map { "${it.field}: ${it.defaultMessage}" },
        )
        return detail
    }

    @ExceptionHandler(ResourceNotFoundException::class)
    fun handleNotFound(ex: ResourceNotFoundException): ProblemDetail {
        val detail = ProblemDetail.forStatus(HttpStatus.NOT_FOUND)
        detail.title = "Not Found"
        detail.detail = ex.message
        return detail
    }

    @ExceptionHandler(Exception::class)
    fun handleGeneral(ex: Exception): ProblemDetail {
        val detail = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR)
        detail.title = "Internal Server Error"
        return detail
    }
}
