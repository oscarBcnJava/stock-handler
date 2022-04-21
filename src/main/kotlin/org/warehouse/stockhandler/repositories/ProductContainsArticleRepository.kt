package org.warehouse.stockhandler.repositories

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import org.warehouse.stockhandler.model.entities.ProductContainsArticleEntity

@Repository
interface ProductContainsArticleRepository : JpaRepository<ProductContainsArticleEntity, Int> {

    fun findAllByProductEntityId(id: Int): List<ProductContainsArticleEntity>

    fun findAllByProductEntityName(name: String): List<ProductContainsArticleEntity>

    fun existsByArticleEntityIdAndProductEntityName(id: Int, name: String): Boolean

    @Modifying
    @Query(
        """ update ProductContainsArticleEntity p set p.amountOf = ?3 where exists 
        ( select p1 from ProductContainsArticleEntity p1 
            join p1.productEntity pe join p1.articleEntity ae 
            where p.id = p1.id and pe.name LIKE ?1 and ae.id = ?2 ) """
    )
    fun updateStockByProductNameAndArticleId(name: String, id: Int, stock: Int)
}