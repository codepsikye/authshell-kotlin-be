package io.cpk.be.basic.mapper

import io.cpk.be.basic.dto.CenterDto
import io.cpk.be.basic.entity.Center
import io.cpk.be.basic.entity.Org
import io.cpk.be.basic.repository.OrgRepository
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.util.Optional
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNull

/**
 * Unit tests for CenterMapper
 */
class CenterMapperTest {

    private lateinit var orgRepository: OrgRepository
    private lateinit var centerMapper: CenterMapper
    private lateinit var testOrg: Org
    
    @BeforeEach
    fun setup() {
        orgRepository = mockk()
        centerMapper = CenterMapper(orgRepository)
        
        testOrg = Org(id = 1, name = "Test Org")
        every { orgRepository.findById(1) } returns Optional.of(testOrg)
    }

    @Test
    fun `should map entity to dto`() {
        // Given
        val center = Center(
            id = 1,
            name = "Test Center",
            address = "123 Test St",
            phone = "555-1234",
            org = testOrg
        )

        // When
        val centerDto = centerMapper.toDto(center)

        // Then
        assertEquals(1, centerDto.id)
        assertEquals("Test Center", centerDto.name)
        assertEquals("123 Test St", centerDto.address)
        assertEquals("555-1234", centerDto.phone)
        assertEquals(1, centerDto.orgId)
    }

    @Test
    fun `should map dto to entity`() {
        // Given
        val now = LocalDateTime.now()
        val centerDto = CenterDto(
            id = 1,
            name = "Test Center",
            address = "123 Test St",
            phone = "555-1234",
            orgId = 1
        )

        // When
        val center = centerMapper.toEntity(centerDto)

        // Then
        assertEquals(1, center.id)
        assertEquals("Test Center", center.name)
        assertEquals("123 Test St", center.address)
        assertEquals("555-1234", center.phone)
        assertEquals(testOrg, center.org)
    }

    @Test
    fun `should map entity list to dto list`() {
        // Given
        val center1 = Center(
            id = 1,
            name = "Center One",
            address = "123 First St",
            phone = "555-1111",
            org = testOrg
        )
        val center2 = Center(
            id = 2,
            name = "Center Two",
            address = "456 Second St",
            phone = "555-2222",
            org = testOrg
        )
        val centers = listOf(center1, center2)

        // When
        val centerDtos = centerMapper.toDtoList(centers)

        // Then
        assertEquals(2, centerDtos.size)
        assertEquals(1, centerDtos[0].id)
        assertEquals("Center One", centerDtos[0].name)
        assertEquals("123 First St", centerDtos[0].address)
        assertEquals("555-1111", centerDtos[0].phone)
        assertEquals(1, centerDtos[0].orgId)
        assertEquals(2, centerDtos[1].id)
        assertEquals("Center Two", centerDtos[1].name)
        assertEquals("456 Second St", centerDtos[1].address)
        assertEquals("555-2222", centerDtos[1].phone)
        assertEquals(1, centerDtos[1].orgId)
    }

    @Test
    fun `should map dto list to entity list`() {
        // Given
        val now = LocalDateTime.now()
        val centerDto1 = CenterDto(
            id = 1,
            name = "Center One",
            address = "123 First St",
            phone = "555-1111",
            orgId = 1
        )
        val centerDto2 = CenterDto(
            id = 2,
            name = "Center Two",
            address = "456 Second St",
            phone = "555-2222",
            orgId = 1
        )
        val centerDtos = listOf(centerDto1, centerDto2)

        // When
        val centers = centerMapper.toEntityList(centerDtos)

        // Then
        assertEquals(2, centers.size)
        assertEquals(1, centers[0].id)
        assertEquals("Center One", centers[0].name)
        assertEquals("123 First St", centers[0].address)
        assertEquals("555-1111", centers[0].phone)
        assertEquals(testOrg, centers[0].org)
        // createdAt and updatedAt should be ignored in mapping and set to default values
        assertNotEquals(now, centers[0].createdAt)
        assertNotEquals(now, centers[0].updatedAt)
        assertEquals(2, centers[1].id)
        assertEquals("Center Two", centers[1].name)
        assertEquals("456 Second St", centers[1].address)
        assertEquals("555-2222", centers[1].phone)
        assertEquals(testOrg, centers[1].org)
        // createdAt and updatedAt should be ignored in mapping and set to default values
        assertNotEquals(now, centers[1].createdAt)
        assertNotEquals(now, centers[1].updatedAt)
    }

    @Test
    fun `should handle null values correctly`() {
        // Given
        val center = Center(
            id = 1,
            name = "Test Center",
            address = null,
            phone = null,
            org = testOrg
        )

        // When
        val centerDto = centerMapper.toDto(center)

        // Then
        assertEquals(1, centerDto.id)
        assertEquals("Test Center", centerDto.name)
        assertNull(centerDto.address)
        assertNull(centerDto.phone)
        assertEquals(1, centerDto.orgId)
    }
}