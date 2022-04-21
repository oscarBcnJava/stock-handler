package org.warehouse.stockhandler.model.entities

import javax.persistence.*

@Entity
data class ProductEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int,
    val name: String
)