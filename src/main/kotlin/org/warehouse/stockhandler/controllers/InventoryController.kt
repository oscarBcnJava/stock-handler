package org.warehouse.stockhandler.controllers

import io.swagger.v3.oas.annotations.Operation
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.warehouse.stockhandler.model.dto.Inventories
import org.warehouse.stockhandler.service.ArticleService

@RestController
class InventoryController {

    @Autowired
    lateinit var articleService: ArticleService

    @GetMapping("/inventories")
    @Operation(summary = "Retrieve inventory from database")
    fun retrieveInventories(): Inventories {
        return articleService.inventories()
    }

    @PostMapping("/inventories")
    @Operation(summary = "Add Inventory to database")
    fun addInventories(
        @RequestBody inventories: Inventories,
        @RequestParam(value = "override") override: Boolean = false
    ): ResponseEntity<Any> {
        articleService.add(inventories, override)
        return ResponseEntity.accepted().build()
    }

    @PostMapping("/inventories/readfile")
    @Operation(summary = "Read inventories from file. Path used will be the one set in the properties file")
    fun addInventoriesFromFile(@RequestParam(value = "override") override: Boolean = false): ResponseEntity<Any> {
        articleService.importFromFile(override)
        return ResponseEntity.accepted().build()
    }

}