package org.warehouse.stockhandler.repositories

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import org.warehouse.stockhandler.model.entities.ProductEntity
import java.util.Optional

@Repository
interface ProductRepository : JpaRepository<ProductEntity, Int> {

    fun findProductEntityByName(name: String): Optional<ProductEntity>
}