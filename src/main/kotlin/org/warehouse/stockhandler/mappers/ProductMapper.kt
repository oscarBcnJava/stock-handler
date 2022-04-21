package org.warehouse.stockhandler.mappers

import org.warehouse.stockhandler.model.dto.Product
import org.warehouse.stockhandler.model.entities.ProductEntity

object ProductMapper {

    fun asEntity(product: Product) =
        ProductEntity(0, product.name)

}