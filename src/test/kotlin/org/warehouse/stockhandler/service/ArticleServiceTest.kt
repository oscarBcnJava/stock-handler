package org.warehouse.stockhandler.service

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.test.util.ReflectionTestUtils
import org.warehouse.stockhandler.model.dto.Inventories
import org.warehouse.stockhandler.model.dto.Inventory
import org.warehouse.stockhandler.model.entities.ArticleEntity
import org.warehouse.stockhandler.repositories.ArticleRepository

@ExtendWith(MockitoExtension::class)
class ArticleServiceTest {

    @Mock
    lateinit var articleRepository: ArticleRepository

    @InjectMocks
    lateinit var articleService: ArticleService

    @BeforeEach
    fun setup() {
        ReflectionTestUtils.setField(articleService, "filePath", "classpath:/inventory.json")
    }

    @Test
    fun given_InventoriesAndNoOverride_ThenOnlyNotExisitingAreAdded() {
        val inventories =
            Inventories(
                listOf(
                    Inventory(1, "piece1", 4),
                    Inventory(2, "piece2", 3),
                    Inventory(1, "piece1", 10)
                )
            )

        `when`(articleRepository.existsById(1))
            .thenReturn(false)
            .thenReturn(true)

        articleService.add(inventories)

        verify(articleRepository, times(2)).save(any())
        verify(articleRepository, never()).updateArticlyById("piece1", 10, 1)
    }

    @Test
    fun given_InventoriesAndOverrideIsSet_ThenExisitingAreUpdated() {
        val inventories =
            Inventories(
                listOf(
                    Inventory(1, "piece1", 4),
                    Inventory(2, "piece2", 3),
                    Inventory(1, "piece1", 10)
                )
            )

        `when`(articleRepository.existsById(1))
            .thenReturn(false)
            .thenReturn(true)

        articleService.add(inventories, true)

        verify(articleRepository, times(2)).save(any())
        verify(articleRepository, times(1)).updateArticlyById("piece1", 10, 1)
    }

    @Test
    fun given_InventoriesRetrieval_ThenExpectSuccess() {
        val articleEntity1 = ArticleEntity(1, "article1", 10)
        val articleEntity2 = ArticleEntity(2, "article2", 15)
        val articleEntity3 = ArticleEntity(3, "article3", 5)

        `when`(articleRepository.findAll())
            .thenReturn(
                listOf(articleEntity1, articleEntity2, articleEntity3)
            )

        val inventories = articleService.inventories()

        assertEquals(3, inventories.inventory.size)
        assertEquals("article1", inventories.inventory[0].name)
        assertEquals(10, inventories.inventory[0].stock)
        assertEquals("article2", inventories.inventory[1].name)
        assertEquals(15, inventories.inventory[1].stock)
        assertEquals("article3", inventories.inventory[2].name)
        assertEquals(5, inventories.inventory[2].stock)
    }

    @Test
    fun given_ImportFromFileAndNoOverridingWithExistingFile_ThenExpectSuccess() {
        `when`(articleRepository.existsById(1)).thenReturn(true)

        articleService.importFromFile()

        verify(articleRepository, times(4)).existsById(any())
        verify(articleRepository, times(3)).save(any())
        verify(articleRepository, never()).updateArticlyById("leg", 12, 1)
    }

    @Test
    fun given_ImportFromFileAndOverridingWithExistingFile_ThenExpectSuccess() {
        `when`(articleRepository.existsById(1)).thenReturn(true)

        articleService.importFromFile(true)

        verify(articleRepository, times(4)).existsById(any())
        verify(articleRepository, times(3)).save(any())
        verify(articleRepository, times(1)).updateArticlyById("leg", 12, 1)
    }
}