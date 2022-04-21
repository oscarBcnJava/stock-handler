package org.warehouse.stockhandler.model.entities

import javax.persistence.*

@Entity
data class ProductContainsArticleEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int,

    @ManyToOne(cascade = [CascadeType.MERGE]) @JoinColumn(name = "article_id")
    val articleEntity: ArticleEntity,

    @ManyToOne(cascade = [CascadeType.MERGE]) @JoinColumn(name = "product_id")
    val productEntity: ProductEntity,

    val amountOf: Int
)