package org.warehouse.stockhandler.service

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.warehouse.stockhandler.exceptions.InconsistentDataException
import org.warehouse.stockhandler.exceptions.ProductNotFoundException
import org.warehouse.stockhandler.exceptions.QuantityNotAvailableException
import org.warehouse.stockhandler.mappers.ProductMapper
import org.warehouse.stockhandler.model.dto.Item
import org.warehouse.stockhandler.model.dto.Products
import org.warehouse.stockhandler.model.dto.Stock
import org.warehouse.stockhandler.model.entities.ArticleEntity
import org.warehouse.stockhandler.model.entities.ProductContainsArticleEntity
import org.warehouse.stockhandler.model.entities.ProductEntity
import org.warehouse.stockhandler.others.Utils
import org.warehouse.stockhandler.repositories.ArticleRepository
import org.warehouse.stockhandler.repositories.ProductContainsArticleRepository
import org.warehouse.stockhandler.repositories.ProductRepository
import java.util.*

@Service
class ProductService {

    @Autowired
    lateinit var productRepository: ProductRepository

    @Autowired
    lateinit var articleRepository: ArticleRepository

    @Autowired
    lateinit var productContainsArticleRepository: ProductContainsArticleRepository

    @Value("\${stock-handler.import.products.path}")
    lateinit var filePath: String

    val mapper = jacksonObjectMapper()

    @Transactional(rollbackFor = [InconsistentDataException::class])
    fun add(products: Products) {
        products.getProducts().forEach { product ->
            val productEntity =
                productRepository.findProductEntityByName(product.name)
                    .orElse(ProductMapper.asEntity(product))
            productRepository.save(productEntity)

            product.getContainArticles().forEach { containedArticle ->
                articleRepository.findById(containedArticle.id).map { article ->
                    updateProductArticleRelation(productEntity, article, containedArticle.amount)
                }.orElseThrow { InconsistentDataException("Article ${containedArticle.id} not found in Database") }
            }
        }
    }

    private fun updateProductArticleRelation(product: ProductEntity, article: ArticleEntity, amount: Int) {
        if (productContainsArticleRepository
                .existsByArticleEntityIdAndProductEntityName(article.id, product.name)
        ) {
            productContainsArticleRepository
                .updateStockByProductNameAndArticleId(product.name, article.id, amount)
        } else {
            val productContainsArticleEntity = ProductContainsArticleEntity(0, article, product, amount)
            productContainsArticleRepository.save(productContainsArticleEntity)
        }
    }

    fun getStock(): Stock =
        Stock(
            productRepository.findAll().map { productEntity ->
                val quantities: PriorityQueue<Int> = PriorityQueue()
                productContainsArticleRepository.findAllByProductEntityId(productEntity.id)
                    .forEach { productContainsArticle ->
                        val neededAmount: Int = productContainsArticle.amountOf
                        val stock: Int =
                            articleRepository.findById(productContainsArticle.articleEntity.id).map { it.stock }
                                .orElseThrow { InconsistentDataException("Article ${productContainsArticle.articleEntity.name} not found in Database") }
                        quantities.add(stock / neededAmount)
                    }
                Item(productEntity.name, quantities.peek())
            })

    @Transactional(rollbackFor = [Throwable::class])
    fun sell(name: String, amount: Int) {
        val stock = getStock()
        val productInStock =
            stock.getItem().find { it.name == name }
                ?: throw ProductNotFoundException("Product $name not found in Stock")

        if (productInStock.availableQuantity < amount) {
            throw QuantityNotAvailableException("Product $name, has only ${productInStock.availableQuantity} items in stock")
        }

        productContainsArticleRepository.findAllByProductEntityName(name)
            .forEach { productContainsArticle ->
                val articleId = productContainsArticle.articleEntity.id
                articleRepository.decreaseStockById(productContainsArticle.amountOf * amount, articleId)
            }
    }

    @Transactional(rollbackFor = [InconsistentDataException::class])
    fun importFromFile(override: Boolean = false) {
        val jsonAsString: String = Utils.fileFromPath(filePath).readText(Charsets.UTF_8)
        val products: Products = mapper.readValue(jsonAsString, Products::class.java)
        add(products)
    }

}