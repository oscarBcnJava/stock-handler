package org.warehouse.stockhandler.model.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class Inventories(
    @JvmField val inventory: List<Inventory>
) {
    fun getInventory() = inventory.toList()
}

data class Inventory(
   @JsonProperty("art_id") val id: Int,
    val name: String,
    val stock: Int
)