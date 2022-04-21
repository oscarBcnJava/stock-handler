package org.warehouse.stockhandler.model.dto

import com.fasterxml.jackson.annotation.JsonProperty


data class Products(
    @JvmField val products: List<Product>
) {
    fun getProducts() = products.toList()
}


data class Product(
    val name: String,
    @JsonProperty("contain_articles") @JvmField  val containArticles: List<Article>
) {
    fun getContainArticles() = containArticles.toList()
}

data class Article(
    @JsonProperty("art_id") val id: Int,
    @JsonProperty("amount_of") val amount: Int
)