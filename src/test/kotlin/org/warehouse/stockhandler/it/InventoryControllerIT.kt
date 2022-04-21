package org.warehouse.stockhandler.it

import io.restassured.RestAssured.get
import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import org.hamcrest.Matchers.`is`
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus


class InventoryControllerIT : BaseIT() {

    @Test
    fun given_GetInventories_ThenExpectAValidResponse() {
        articleService.importFromFile()
        productService.importFromFile()

        get("http://localhost:$port/inventories")
            .then()
            .assertThat()
            .statusCode(HttpStatus.OK.value())
            .body("inventory.size()", `is`(4))
            .body("inventory[0].art_id", `is`(1))
            .body("inventory[0].name", `is`("leg"))
            .body("inventory[0].stock", `is`(12))
            .body("inventory[1].art_id", `is`(2))
            .body("inventory[1].name", `is`("screw"))
            .body("inventory[1].stock", `is`(17))
            .body("inventory[2].art_id", `is`(3))
            .body("inventory[2].name", `is`("seat"))
            .body("inventory[2].stock", `is`(2))
            .body("inventory[3].art_id", `is`(4))
            .body("inventory[3].name", `is`("table top"))
            .body("inventory[3].stock", `is`(1))
    }

    @Test
    fun given_AddInventories_ThenExpectDatabaseContent() {
        assertTrue(articleRepository.findAll().isEmpty())

        given()
            .contentType(ContentType.JSON)
            .header("Content-Type", "application/json")
            .body(
                """
				{"inventory":[{"art_id":1,"name":"chair","stock":3}]}
			""".trimIndent()
            )
            .`when`()
            .post("http://localhost:$port/inventories")
            .then()
            .assertThat()
            .statusCode(HttpStatus.ACCEPTED.value())

        val articles = articleRepository.findAll()

        assertEquals(1, articles.size)
        assertEquals(1, articles[0].id)
        assertEquals("chair", articles[0].name)
        assertEquals(3, articles[0].stock)
    }

    @Test
    fun given_AddInventoriesFromFile_ThenExpectDatabaseContent() {
        assertTrue(articleRepository.findAll().isEmpty())

        given()
            .`when`()
            .post("http://localhost:$port/inventories/readfile?override=false")
            .then()
            .assertThat()
            .statusCode(HttpStatus.ACCEPTED.value())

        val articles = articleRepository.findAll()

        assertEquals(4, articles.size)
        assertEquals(1, articles[0].id)
        assertEquals("leg", articles[0].name)
        assertEquals(12, articles[0].stock)
        assertEquals(2, articles[1].id)
        assertEquals("screw", articles[1].name)
        assertEquals(17, articles[1].stock)
        assertEquals(3, articles[2].id)
        assertEquals("seat", articles[2].name)
        assertEquals(2, articles[2].stock)
        assertEquals(4, articles[3].id)
        assertEquals("table top", articles[3].name)
        assertEquals(1, articles[3].stock)
    }
}

