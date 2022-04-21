package org.warehouse.stockhandler.repositories

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import org.warehouse.stockhandler.model.entities.ArticleEntity


@Repository
interface ArticleRepository : JpaRepository<ArticleEntity, Int> {
    @Modifying
    @Query("update ArticleEntity a set a.stock = (a.stock - ?1) where a.id = ?2")
    fun decreaseStockById(decreaseItems: Int, id: Int)

    @Modifying
    @Query("update ArticleEntity a set a.name = ?1, a.stock = ?2 where a.id = ?3")
    fun updateArticlyById(name: String, stock: Int, id: Int)
}