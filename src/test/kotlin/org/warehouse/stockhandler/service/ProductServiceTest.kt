package org.warehouse.stockhandler.service

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.test.util.ReflectionTestUtils
import org.warehouse.stockhandler.exceptions.InconsistentDataException
import org.warehouse.stockhandler.exceptions.QuantityNotAvailableException
import org.warehouse.stockhandler.model.dto.Article
import org.warehouse.stockhandler.model.dto.Product
import org.warehouse.stockhandler.model.dto.Products
import org.warehouse.stockhandler.model.entities.ArticleEntity
import org.warehouse.stockhandler.model.entities.ProductContainsArticleEntity
import org.warehouse.stockhandler.model.entities.ProductEntity
import org.warehouse.stockhandler.repositories.ArticleRepository
import org.warehouse.stockhandler.repositories.ProductContainsArticleRepository
import org.warehouse.stockhandler.repositories.ProductRepository
import java.util.*

@ExtendWith(MockitoExtension::class)
class ProductServiceTest {

    @Mock
    lateinit var productRepository: ProductRepository

    @Mock
    lateinit var articleRepository: ArticleRepository

    @Mock
    lateinit var productContainsArticleRepository: ProductContainsArticleRepository

    @InjectMocks
    lateinit var productService: ProductService

    @BeforeEach
    fun setup() {
        ReflectionTestUtils.setField(productService, "filePath", "classpath:/products.json")
    }

    @Test
    fun given_ProductWithUnexistingArticles_ThenExpectException() {
        val products =
            Products(
                listOf(Product("product1", listOf(Article(1, 2), Article(3, 1))))
            )

        val articleEntity1 = ArticleEntity(1, "article1", 3)
        val productEntity1 = ProductEntity(1, "product1")
        val productContainsArticleEntity = ProductContainsArticleEntity(0, articleEntity1, productEntity1, 3)

        `when`(productContainsArticleRepository.save(any())).thenReturn(productContainsArticleEntity)
        `when`(articleRepository.findById(1)).thenReturn(Optional.of(articleEntity1))

        assertThrows(InconsistentDataException::class.java) { productService.add(products) }
    }

    @Test
    fun given_ProductWithExistingArticles_ThenExpectSuccess() {
        val products =
            Products(
                listOf(Product("product1", listOf(Article(1, 2), Article(3, 1))))
            )

        val articleEntity1 = ArticleEntity(1, "article1", 3)
        val articleEntity3 = ArticleEntity(3, "article3", 5)
        val productEntity1 = ProductEntity(1, "product1")
        val productContainsArticleEntity = ProductContainsArticleEntity(1, articleEntity1, productEntity1, 3)

        `when`(productContainsArticleRepository.save(any())).thenReturn(productContainsArticleEntity)
        `when`(articleRepository.findById(1)).thenReturn(Optional.of(articleEntity1))
        `when`(articleRepository.findById(3)).thenReturn(Optional.of(articleEntity3))

        productService.add(products)

        verify(productRepository, times(1)).save(any())
        verify(productContainsArticleRepository, times(2)).save(any())
    }

    @Test
    fun given_NotEnoughArticles_ThenProductStockIsZero() {
        val productEntity1 = ProductEntity(1, "Chair")

        val articleEntity1 = ArticleEntity(1, "part1", 4)
        val articleEntity3 = ArticleEntity(3, "part3", 2)
        val productContainsArticleEntityArticle1 = ProductContainsArticleEntity(1, articleEntity1, productEntity1, 4)
        val productContainsArticleEntityArticle3 = ProductContainsArticleEntity(2, articleEntity3, productEntity1, 3)

        `when`(productRepository.findAll()).thenReturn(mutableListOf(productEntity1))
        `when`(productContainsArticleRepository.findAllByProductEntityId(1))
            .thenReturn(listOf(productContainsArticleEntityArticle1, productContainsArticleEntityArticle3))

        `when`(articleRepository.findById(1)).thenReturn(Optional.of(articleEntity1))
        `when`(articleRepository.findById(3)).thenReturn(Optional.of(articleEntity3))

        val stock = productService.getStock()

        assertEquals(1, stock.item.size)
        assertEquals(0, stock.item[0].availableQuantity)
    }

    @Test
    fun given_EnoughArticles_ThenExpectAvailableStock() {
        val productEntity1 = ProductEntity(1, "Chair")

        val articleEntity1 = ArticleEntity(1, "part1", 18)
        val articleEntity3 = ArticleEntity(3, "part3", 35)
        val productContainsArticleEntityArticle1 = ProductContainsArticleEntity(1, articleEntity1, productEntity1, 2)
        val productContainsArticleEntityArticle3 = ProductContainsArticleEntity(2, articleEntity3, productEntity1, 4)

        `when`(productRepository.findAll()).thenReturn(mutableListOf(productEntity1))
        `when`(productContainsArticleRepository.findAllByProductEntityId(1))
            .thenReturn(listOf(productContainsArticleEntityArticle1, productContainsArticleEntityArticle3))

        `when`(articleRepository.findById(1)).thenReturn(Optional.of(articleEntity1))
        `when`(articleRepository.findById(3)).thenReturn(Optional.of(articleEntity3))

        val stock = productService.getStock()

        assertEquals(1, stock.item.size)
        assertEquals(8, stock.item[0].availableQuantity)
    }

    @Test
    fun given_SellWithNotEnoughProducts_ThenExpectException() {
        val productEntity1 = ProductEntity(1, "DinningTable")

        val articleEntity1 = ArticleEntity(1, "part1", 4)
        val articleEntity3 = ArticleEntity(3, "part3", 6)
        val productContainsArticleEntityArticle1 = ProductContainsArticleEntity(1, articleEntity1, productEntity1, 2)
        val productContainsArticleEntityArticle3 = ProductContainsArticleEntity(2, articleEntity3, productEntity1, 3)

        `when`(productRepository.findAll()).thenReturn(mutableListOf(productEntity1))
        `when`(productContainsArticleRepository.findAllByProductEntityId(1))
            .thenReturn(listOf(productContainsArticleEntityArticle1, productContainsArticleEntityArticle3))

        `when`(articleRepository.findById(1)).thenReturn(Optional.of(articleEntity1))
        `when`(articleRepository.findById(3)).thenReturn(Optional.of(articleEntity3))

        assertThrows(QuantityNotAvailableException::class.java) { productService.sell("DinningTable", 3) }
    }

    @Test
    fun given_SellWithEnoughProducts_ThenExpectSuccess() {
        val productEntity1 = ProductEntity(1, "SmallTable")

        val articleEntity1 = ArticleEntity(1, "part1", 4)
        val articleEntity3 = ArticleEntity(3, "part3", 6)
        val productContainsArticleEntityArticle1 = ProductContainsArticleEntity(1, articleEntity1, productEntity1, 2)
        val productContainsArticleEntityArticle3 = ProductContainsArticleEntity(2, articleEntity3, productEntity1, 3)

        `when`(productRepository.findAll()).thenReturn(mutableListOf(productEntity1))
        `when`(productContainsArticleRepository.findAllByProductEntityId(1))
            .thenReturn(listOf(productContainsArticleEntityArticle1, productContainsArticleEntityArticle3))

        `when`(articleRepository.findById(1)).thenReturn(Optional.of(articleEntity1))
        `when`(articleRepository.findById(3)).thenReturn(Optional.of(articleEntity3))

        `when`(productContainsArticleRepository.findAllByProductEntityName("SmallTable"))
            .thenReturn(listOf(productContainsArticleEntityArticle1, productContainsArticleEntityArticle3))

        productService.sell("SmallTable", 2)

        verify(articleRepository, times(1)).decreaseStockById(4, 1)
        verify(articleRepository, times(1)).decreaseStockById(6, 3)
    }
}