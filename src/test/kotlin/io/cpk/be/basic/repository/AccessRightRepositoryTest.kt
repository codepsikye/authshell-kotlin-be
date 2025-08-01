package io.cpk.be.basic.repository

import io.cpk.be.basic.entity.AccessRight
import io.cpk.be.config.TestConfig
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import org.springframework.context.annotation.Import
import org.springframework.data.domain.PageRequest
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Unit tests for AccessRightRepository
 */
@DataJpaTest
@Import(TestConfig::class)
class AccessRightRepositoryTest {

    @Autowired private lateinit var entityManager: TestEntityManager

    @Autowired private lateinit var accessRightRepository: AccessRightRepository

    @Test
    fun `should save and find access right`() {
        // Given
        val accessRight = AccessRight(
            name = "test-access-right"
        )

        // When
        val savedAccessRight = entityManager.persistAndFlush(accessRight)
        val found = accessRightRepository.findById(savedAccessRight.name)

        // Then
        assertTrue(found.isPresent)
        val foundAccessRight = found.get()
        assertEquals(savedAccessRight.name, foundAccessRight.name)
        assertEquals("test-access-right", foundAccessRight.name)
    }

    @Test
    fun `should find all access rights`() {
        // Given
        val accessRight1 = AccessRight(name = "access-right-1")
        val accessRight2 = AccessRight(name = "access-right-2")

        entityManager.persistAndFlush(accessRight1)
        entityManager.persistAndFlush(accessRight2)

        // When
        val accessRights = accessRightRepository.findAll()

        // Then
        assertTrue(accessRights.size >= 2)
        assertTrue(accessRights.any { it.name == "access-right-1" })
        assertTrue(accessRights.any { it.name == "access-right-2" })
    }

    @Test
    fun `should find access rights by name containing with pagination`() {
        // Given
        val accessRight1 = AccessRight(name = "read-access")
        val accessRight2 = AccessRight(name = "write-access")
        val accessRight3 = AccessRight(name = "delete-permission")

        entityManager.persistAndFlush(accessRight1)
        entityManager.persistAndFlush(accessRight2)
        entityManager.persistAndFlush(accessRight3)

        // When
        val pageable = PageRequest.of(0, 10)
        val result = accessRightRepository.findAllByNameContaining("access", pageable)

        // Then
        assertEquals(2, result.totalElements)
        assertTrue(result.content.any { it.name == "read-access" })
        assertTrue(result.content.any { it.name == "write-access" })
        assertTrue(result.content.none { it.name == "delete-permission" })
    }

    @Test
    fun `should delete access right`() {
        // Given
        val accessRight = AccessRight(name = "access-right-to-delete")

        val savedAccessRight = entityManager.persistAndFlush(accessRight)
        assertTrue(accessRightRepository.findById(savedAccessRight.name).isPresent)

        // When
        accessRightRepository.deleteById(savedAccessRight.name)
        entityManager.flush()

        // Then
        assertTrue(accessRightRepository.findById(savedAccessRight.name).isEmpty)
    }

    @Test
    fun `should update access right`() {
        // Given
        val accessRight = AccessRight(
            name = "access-right-to-update"
        )

        val savedAccessRight = entityManager.persistAndFlush(accessRight)

        // When
        val updatedAccessRight = savedAccessRight.copy()
        accessRightRepository.save(updatedAccessRight)
        entityManager.flush()

        // Then
        val updated = accessRightRepository.findById(savedAccessRight.name).get()
        assertEquals("access-right-to-update", updated.name)
    }
}