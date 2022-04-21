package org.warehouse.stockhandler

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class StockHandlerApplication

fun main(args: Array<String>) {
    runApplication<StockHandlerApplication>(*args)
}
