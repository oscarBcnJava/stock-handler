package org.warehouse.stockhandler.configuration

import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import org.warehouse.stockhandler.exceptions.InconsistentDataException
import org.warehouse.stockhandler.exceptions.QuantityNotAvailableException

@ControllerAdvice
class RestExceptionHandler : ResponseEntityExceptionHandler() {
    @ExceptionHandler(value = [QuantityNotAvailableException::class])
    protected fun handleOutOfStock(ex: Exception, request: WebRequest): ResponseEntity<Any> =
        handleExceptionInternal(
            ex, "The quantity requested is not available",
            HttpHeaders(), HttpStatus.CONFLICT, request
        )

    @ExceptionHandler(value = [InconsistentDataException::class])
    protected fun handleProductsDataError(ex: Exception, request: WebRequest): ResponseEntity<Any> =
        handleExceptionInternal(
            ex, "There are no articles in database for some Products",
            HttpHeaders(), HttpStatus.CONFLICT, request
        )
}