package io.cpk.be.basic.mapper

import io.cpk.be.basic.dto.AccessRightDto
import io.cpk.be.basic.entity.AccessRight
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

/**
 * Unit tests for AccessRightMapper
 */
class AccessRightMapperTest {

    private val accessRightMapper = AccessRightMapper()

    @Test
    fun `should map entity to dto`() {
        // Given
        val accessRight = AccessRight(
            name = "user_read"
        )

        // When
        val accessRightDto = accessRightMapper.toDto(accessRight)

        // Then
        assertEquals("user_read", accessRightDto.name)
    }

    @Test
    fun `should map dto to entity`() {
        // Given
        val accessRightDto = AccessRightDto(
            name = "user_read"
        )

        // When
        val accessRight = accessRightMapper.toEntity(accessRightDto)

        // Then
        assertEquals("user_read", accessRight.name)
        // createdAt and updatedAt should be set to default values
    }

    @Test
    fun `should map entity list to dto list`() {
        // Given
        val accessRight1 = AccessRight(
            name = "user_read"
        )
        val accessRight2 = AccessRight(
            name = "user_write"
        )
        val accessRights = listOf(accessRight1, accessRight2)

        // When
        val accessRightDtos = accessRightMapper.toDtoList(accessRights)

        // Then
        assertEquals(2, accessRightDtos.size)
        assertEquals("user_read", accessRightDtos[0].name)
        assertEquals("user_write", accessRightDtos[1].name)
    }

    @Test
    fun `should map dto list to entity list`() {
        // Given
        val accessRightDto1 = AccessRightDto(
            name = "user_read"
        )
        val accessRightDto2 = AccessRightDto(
            name = "user_write"
        )
        val accessRightDtos = listOf(accessRightDto1, accessRightDto2)

        // When
        val accessRights = accessRightMapper.toEntityList(accessRightDtos)

        // Then
        assertEquals(2, accessRights.size)
        assertEquals("user_read", accessRights[0].name)
        assertEquals("user_write", accessRights[1].name)
        // createdAt and updatedAt should be set to default values
    }
}