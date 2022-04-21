package org.warehouse.stockhandler.it

import io.restassured.RestAssured.get
import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import org.hamcrest.Matchers.`is`
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus

class ProductControllerIT : BaseIT() {

    @Test
    fun given_GetStock_ThenExpectAValidResponse() {
        articleService.importFromFile()
        productService.importFromFile()

        get("http://localhost:$port/stock")
            .then()
            .assertThat()
            .statusCode(HttpStatus.OK.value())
            .body("item.size()", `is`(2))
            .body("item[0].name", `is`("Dining Chair"))
            .body("item[0].availableQuantity", `is`(2))
            .body("item[1].name", `is`("Dinning Table"))
            .body("item[1].availableQuantity", `is`(1))
    }

    @Test
    fun given_SellAProduct_ThenExpectStockIsUpdated() {
        articleService.importFromFile()
        productService.importFromFile()


        given()
            .contentType(ContentType.JSON)
            .header("Content-Type", "application/json")
            .body(
                """
				{"name":"Dining Chair","quantity":"1"}
			""".trimIndent()
            )
            .put("http://localhost:$port/sell")
            .then()
            .assertThat()
            .statusCode(HttpStatus.OK.value())

        get("http://localhost:$port/stock")
            .then()
            .assertThat()
            .statusCode(HttpStatus.OK.value())
            .body("item.size()", `is`(2))
            .body("item[0].name", `is`("Dining Chair"))
            .body("item[0].availableQuantity", `is`(1))
            .body("item[1].name", `is`("Dinning Table"))
            .body("item[1].availableQuantity", `is`(1))
    }

    @Test
    fun given_SellingMoreProductThanAvailable_ThenExpectException() {
        articleService.importFromFile()
        productService.importFromFile()

        given()
            .contentType(ContentType.JSON)
            .header("Content-Type", "application/json")
            .body(
                """
				{"name":"Dining Chair","quantity":"4"}
			""".trimIndent()
            )
            .put("http://localhost:$port/sell")
            .then()
            .assertThat()
            .statusCode(HttpStatus.CONFLICT.value())
    }

    @Test
    fun given_AddProductsFromFileWithArticlesReferencedInDatabase_ThenExpectSuccess() {
        articleService.importFromFile()
        assertTrue(productRepository.findAll().isEmpty())
        assertTrue(productContainsArticleRepository.findAll().isEmpty())

        given()
            .`when`()
            .post("http://localhost:$port/products/readfile")

            .then()
            .log().all()
            .assertThat()
            .statusCode(HttpStatus.ACCEPTED.value())

        val products = productRepository.findAll()
        val productsContainsArticles = productContainsArticleRepository.findAll()

        assertEquals(2, products.size)
        assertEquals(1, products[0].id)
        assertEquals("Dining Chair", products[0].name)
        assertEquals(2, products[1].id)
        assertEquals("Dinning Table", products[1].name)

        assertEquals(6, productsContainsArticles.size)

        assertEquals(1, productsContainsArticles[0].id)
        assertEquals(1, productsContainsArticles[0].articleEntity.id)
        assertEquals("leg", productsContainsArticles[0].articleEntity.name)
        assertEquals(12, productsContainsArticles[0].articleEntity.stock)
        assertEquals(1, productsContainsArticles[0].productEntity.id)
        assertEquals("Dining Chair", productsContainsArticles[0].productEntity.name)
        assertEquals(4, productsContainsArticles[0].amountOf)

        assertEquals(2, productsContainsArticles[1].id)
        assertEquals(2, productsContainsArticles[1].articleEntity.id)
        assertEquals("screw", productsContainsArticles[1].articleEntity.name)
        assertEquals(17, productsContainsArticles[1].articleEntity.stock)
        assertEquals(1, productsContainsArticles[1].productEntity.id)
        assertEquals("Dining Chair", productsContainsArticles[1].productEntity.name)
        assertEquals(8, productsContainsArticles[1].amountOf)

        assertEquals(3, productsContainsArticles[2].id)
        assertEquals(3, productsContainsArticles[2].articleEntity.id)
        assertEquals("seat", productsContainsArticles[2].articleEntity.name)
        assertEquals(2, productsContainsArticles[2].articleEntity.stock)
        assertEquals(1, productsContainsArticles[2].productEntity.id)
        assertEquals("Dining Chair", productsContainsArticles[2].productEntity.name)
        assertEquals(1, productsContainsArticles[2].amountOf)

        assertEquals(4, productsContainsArticles[3].id)
        assertEquals(1, productsContainsArticles[3].articleEntity.id)
        assertEquals("leg", productsContainsArticles[3].articleEntity.name)
        assertEquals(12, productsContainsArticles[3].articleEntity.stock)
        assertEquals(2, productsContainsArticles[3].productEntity.id)
        assertEquals("Dinning Table", productsContainsArticles[3].productEntity.name)
        assertEquals(4, productsContainsArticles[3].amountOf)

        assertEquals(5, productsContainsArticles[4].id)
        assertEquals(2, productsContainsArticles[4].articleEntity.id)
        assertEquals("screw", productsContainsArticles[4].articleEntity.name)
        assertEquals(17, productsContainsArticles[4].articleEntity.stock)
        assertEquals(2, productsContainsArticles[4].productEntity.id)
        assertEquals("Dinning Table", productsContainsArticles[4].productEntity.name)
        assertEquals(8, productsContainsArticles[4].amountOf)

        assertEquals(6, productsContainsArticles[5].id)
        assertEquals(4, productsContainsArticles[5].articleEntity.id)
        assertEquals("table top", productsContainsArticles[5].articleEntity.name)
        assertEquals(1, productsContainsArticles[5].articleEntity.stock)
        assertEquals(2, productsContainsArticles[5].productEntity.id)
        assertEquals("Dinning Table", productsContainsArticles[5].productEntity.name)
        assertEquals(1, productsContainsArticles[5].amountOf)
    }

    @Test
    fun given_AddProductsFromFileAndNoArticlesInDatabase_ThenExpectException() {
        assertTrue(productRepository.findAll().isEmpty())
        assertTrue(productContainsArticleRepository.findAll().isEmpty())

        given()
            .`when`()
            .post("http://localhost:$port/products/readfile")
            .then()
            .assertThat()
            .statusCode(HttpStatus.CONFLICT.value())

        val products = productRepository.findAll()
        val productsContainsArticles = productContainsArticleRepository.findAll()
        assertEquals(0, products.size)
        assertEquals(0, productsContainsArticles.size)
    }

}

