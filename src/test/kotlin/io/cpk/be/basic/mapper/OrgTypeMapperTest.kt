package io.cpk.be.basic.mapper

import io.cpk.be.basic.dto.OrgConfig
import io.cpk.be.basic.dto.OrgTypeDto
import io.cpk.be.basic.entity.OrgType
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

/**
 * Unit tests for OrgTypeMapper
 */
class OrgTypeMapperTest {

    private val orgTypeMapper = OrgTypeMapper()

    @Test
    fun `should map entity to dto`() {
        // Given
        val accessRights = listOf("user_read", "user_create", "user_edit")
        val orgConfigMap = mapOf("key1" to "value1", "key2" to 123, "key3" to true)
        val orgConfig = OrgConfig(orgConfigMap)
        
        val orgType = OrgType(
            name = "test-org-type",
            accessRight = accessRights,
            orgConfigs = orgConfig
        )

        // When
        val orgTypeDto = orgTypeMapper.toDto(orgType)

        // Then
        assertEquals("test-org-type", orgTypeDto.name)
        assertEquals(accessRights, orgTypeDto.accessRight)
        assertEquals(orgConfig.toMap(), orgTypeDto.orgConfigs.toMap())
    }

    @Test
    fun `should map dto to entity`() {
        // Given
        val accessRights = listOf("user_read", "user_create", "user_edit")
        val orgConfigMap = mapOf("key1" to "value1", "key2" to 123, "key3" to true)
        val orgConfig = OrgConfig(orgConfigMap)
        
        val orgTypeDto = OrgTypeDto(
            name = "test-org-type",
            accessRight = accessRights,
            orgConfigs = orgConfig
        )

        // When
        val orgType = orgTypeMapper.toEntity(orgTypeDto)

        // Then
        assertEquals("test-org-type", orgType.name)
        assertEquals(accessRights, orgType.accessRight)
        assertEquals(orgConfig.toMap(), orgType.orgConfigs.toMap())
        // createdAt and updatedAt should be set to default values
    }

    @Test
    fun `should map entity list to dto list`() {
        // Given
        val config1 = OrgConfig(mapOf("key1" to "value1"))
        val config2 = OrgConfig(mapOf("key2" to "value2"))
        
        val orgType1 = OrgType(
            name = "type-one",
            accessRight = listOf("user_read", "user_create"),
            orgConfigs = config1
        )
        val orgType2 = OrgType(
            name = "type-two",
            accessRight = listOf("user_read", "user_edit"),
            orgConfigs = config2
        )
        val orgTypes = listOf(orgType1, orgType2)

        // When
        val orgTypeDtos = orgTypeMapper.toDtoList(orgTypes)

        // Then
        assertEquals(2, orgTypeDtos.size)
        assertEquals("type-one", orgTypeDtos[0].name)
        assertEquals(listOf("user_read", "user_create"), orgTypeDtos[0].accessRight)
        assertEquals(config1.toMap(), orgTypeDtos[0].orgConfigs.toMap())
        
        assertEquals("type-two", orgTypeDtos[1].name)
        assertEquals(listOf("user_read", "user_edit"), orgTypeDtos[1].accessRight)
        assertEquals(config2.toMap(), orgTypeDtos[1].orgConfigs.toMap())
    }

    @Test
    fun `should map dto list to entity list`() {
        // Given
        val config1 = OrgConfig(mapOf("key1" to "value1"))
        val config2 = OrgConfig(mapOf("key2" to "value2"))
        
        val orgTypeDto1 = OrgTypeDto(
            name = "type-one",
            accessRight = listOf("user_read", "user_create"),
            orgConfigs = config1
        )
        val orgTypeDto2 = OrgTypeDto(
            name = "type-two",
            accessRight = listOf("user_read", "user_edit"),
            orgConfigs = config2
        )
        val orgTypeDtos = listOf(orgTypeDto1, orgTypeDto2)

        // When
        val orgTypes = orgTypeMapper.toEntityList(orgTypeDtos)

        // Then
        assertEquals(2, orgTypes.size)
        assertEquals("type-one", orgTypes[0].name)
        assertEquals(listOf("user_read", "user_create"), orgTypes[0].accessRight)
        assertEquals(config1.toMap(), orgTypes[0].orgConfigs.toMap())
        // createdAt and updatedAt should be set to default values
        assertEquals("type-two", orgTypes[1].name)
        assertEquals(listOf("user_read", "user_edit"), orgTypes[1].accessRight)
        assertEquals(config2.toMap(), orgTypes[1].orgConfigs.toMap())
        // createdAt and updatedAt should be set to default values
    }

    @Test
    fun `should handle empty collections correctly`() {
        // Given
        val config = OrgConfig()
        val orgType = OrgType(
            name = "test-org-type",
            accessRight = emptyList(),
            orgConfigs = config
        )

        // When
        val orgTypeDto = orgTypeMapper.toDto(orgType)

        // Then
        assertEquals("test-org-type", orgTypeDto.name)
        assertEquals(emptyList(), orgTypeDto.accessRight)
        assertEquals(config.toMap(), orgTypeDto.orgConfigs.toMap())
    }

    @Test
    fun `should handle empty collections correctly in DTO`() {
        // Given
        val config = OrgConfig()
        val orgTypeDto = OrgTypeDto(
            name = "test-org-type",
            accessRight = emptyList(),
            orgConfigs = config
        )

        // When
        val orgType = orgTypeMapper.toEntity(orgTypeDto)

        // Then
        assertEquals("test-org-type", orgType.name)
        assertEquals(emptyList(), orgType.accessRight)
        assertEquals(config.toMap(), orgType.orgConfigs.toMap())
    }
}