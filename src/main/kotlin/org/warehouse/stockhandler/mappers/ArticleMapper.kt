package org.warehouse.stockhandler.mappers

import org.warehouse.stockhandler.model.dto.Inventory
import org.warehouse.stockhandler.model.entities.ArticleEntity

object ArticleMapper {

    fun asEntity(inventory: Inventory) =
        ArticleEntity(inventory.id, inventory.name, inventory.stock)

    fun asDto(articleEntity: ArticleEntity) =
        Inventory(articleEntity.id, articleEntity.name, articleEntity.stock)
}