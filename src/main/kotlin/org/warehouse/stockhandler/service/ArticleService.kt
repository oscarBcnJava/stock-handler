package org.warehouse.stockhandler.service

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.warehouse.stockhandler.mappers.ArticleMapper
import org.warehouse.stockhandler.model.dto.Inventories
import org.warehouse.stockhandler.others.Utils
import org.warehouse.stockhandler.repositories.ArticleRepository


@Service
class ArticleService {

    @Autowired
    lateinit var articleRepository: ArticleRepository

    @Value("\${stock-handler.import.inventories.path}")
    lateinit var filePath: String

    val mapper = jacksonObjectMapper()

    @Transactional
    fun add(inventories: Inventories, override: Boolean = false) {
        inventories.getInventory().forEach {
            val existsById = articleRepository.existsById(it.id)
            if (existsById && override) {
                articleRepository.updateArticlyById(it.name, it.stock, it.id)
            } else if (!existsById) {
                articleRepository.save(ArticleMapper.asEntity(it))
            }
        }
    }

    fun inventories() =
        Inventories(articleRepository.findAll().map(ArticleMapper::asDto))

    fun importFromFile(override: Boolean = false) {
        val jsonAsString: String = Utils.fileFromPath(filePath).readText(Charsets.UTF_8)
        val inventories: Inventories = mapper.readValue(jsonAsString, Inventories::class.java)
        add(inventories, override)
    }
}