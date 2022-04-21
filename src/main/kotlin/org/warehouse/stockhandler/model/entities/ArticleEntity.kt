package org.warehouse.stockhandler.model.entities

import javax.persistence.*

@Entity
data class ArticleEntity(
    @Id
    val id: Int,
    val name: String,
    val stock: Int
)