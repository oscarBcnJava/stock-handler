package org.warehouse.stockhandler.it

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.context.jdbc.Sql.ExecutionPhase
import org.warehouse.stockhandler.repositories.ArticleRepository
import org.warehouse.stockhandler.repositories.ProductContainsArticleRepository
import org.warehouse.stockhandler.repositories.ProductRepository
import org.warehouse.stockhandler.service.ArticleService
import org.warehouse.stockhandler.service.ProductService

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Sql(scripts = ["/clear_database.sql"], executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
class BaseIT {

    @LocalServerPort
    val port: Int? = null

    @Autowired
    lateinit var articleRepository: ArticleRepository

    @Autowired
    lateinit var productRepository: ProductRepository

    @Autowired
    lateinit var productContainsArticleRepository: ProductContainsArticleRepository

    @Autowired
    lateinit var articleService: ArticleService

    @Autowired
    lateinit var productService: ProductService

}