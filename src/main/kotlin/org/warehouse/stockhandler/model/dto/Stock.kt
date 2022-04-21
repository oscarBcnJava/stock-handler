package org.warehouse.stockhandler.model.dto

data class Stock(
    @JvmField val item: List<Item>
) {
    fun getItem() = item.toList()
}

data class Item(
    val name: String,
    val availableQuantity: Int
)