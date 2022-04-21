package org.warehouse.stockhandler.controllers

import io.swagger.v3.oas.annotations.Operation
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.warehouse.stockhandler.model.dto.Products
import org.warehouse.stockhandler.model.dto.SellProduct
import org.warehouse.stockhandler.model.dto.Stock
import org.warehouse.stockhandler.service.ProductService

@RestController
class ProductController {

    @Autowired
    lateinit var productService: ProductService

    @GetMapping("/stock")
    @Operation(summary = "Retrieve actual stock information from database")
    fun retrieveStock(): Stock {
        return productService.getStock()
    }

    @PostMapping("/products")
    @Operation(summary = "Add Products to database")
    fun addInventories(
        @RequestBody products: Products
    ): ResponseEntity<Any> {
        productService.add(products)
        return ResponseEntity.accepted().build()
    }

    @PutMapping("/sell")
    @Operation(summary = "Sell a quantity of product. It will be found by name")
    fun sellProduct(@RequestBody sellProduct: SellProduct): ResponseEntity<Any> {
        productService.sell(sellProduct.name, sellProduct.quantity)
        return ResponseEntity.ok().build()
    }

    @PostMapping("/products/readfile")
    @Operation(summary = "Read products from file. Path used will be the one set in the properties file")
    fun addInventoriesFromFile(): ResponseEntity<Any> {
        productService.importFromFile()
        return ResponseEntity.accepted().build()
    }

}