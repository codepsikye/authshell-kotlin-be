package io.cpk.be.basic.mapper

import io.cpk.be.basic.dto.OrgDto
import io.cpk.be.basic.entity.Org
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

/**
 * Unit tests for OrgMapper
 */
class OrgMapperTest {

    private val orgMapper = OrgMapper()


    @Test
    fun `should map entity to dto`() {
        // Given
        val org = Org(
            id = 1,
            name = "Test Organization",
            address = "123 Test St",
            phone = "555-1234",
            city = "Test City",
            country = "Test Country",
            notes = "Test Notes",
            orgTypeName = "test-org-type"
        )

        // When
        val orgDto = orgMapper.toDto(org)

        // Then
        assertEquals(1, orgDto.id)
        assertEquals("Test Organization", orgDto.name)
        assertEquals("123 Test St", orgDto.address)
        assertEquals("555-1234", orgDto.phone)
        assertEquals("Test City", orgDto.city)
        assertEquals("Test Country", orgDto.country)
        assertEquals("Test Notes", orgDto.notes)
        assertEquals("test-org-type", orgDto.orgTypeName)
    }

    @Test
    fun `should map dto to entity`() {
        // Given
        val orgDto = OrgDto(
            id = 1,
            name = "Test Organization",
            address = "123 Test St",
            phone = "555-1234",
            city = "Test City",
            country = "Test Country",
            notes = "Test Notes",
            orgTypeName = "test-org-type"
        )

        // When
        val org = orgMapper.toEntity(orgDto)

        // Then
        assertEquals(1, org.id)
        assertEquals("Test Organization", org.name)
        assertEquals("123 Test St", org.address)
        assertEquals("555-1234", org.phone)
        assertEquals("Test City", org.city)
        assertEquals("Test Country", org.country)
        assertEquals("Test Notes", org.notes)
        assertEquals("test-org-type", org.orgTypeName)
        assertEquals(null, org.orgType)
    }

    @Test
    fun `should map entity list to dto list`() {
        // Given
        val org1 = Org(
            id = 1,
            name = "Organization One",
            address = "123 First St",
            phone = "555-1111",
            city = "City One",
            country = "Country One",
            notes = "Notes One",
            orgTypeName = "type-one"
        )
        val org2 = Org(
            id = 2,
            name = "Organization Two",
            address = "456 Second St",
            phone = "555-2222",
            city = "City Two",
            country = "Country Two",
            notes = "Notes Two",
            orgTypeName = "type-two"
        )
        val orgs = listOf(org1, org2)

        // When
        val orgDtos = orgMapper.toDtoList(orgs)

        // Then
        assertEquals(2, orgDtos.size)
        assertEquals(1, orgDtos[0].id)
        assertEquals("Organization One", orgDtos[0].name)
        assertEquals("123 First St", orgDtos[0].address)
        assertEquals("555-1111", orgDtos[0].phone)
        assertEquals("City One", orgDtos[0].city)
        assertEquals("Country One", orgDtos[0].country)
        assertEquals("Notes One", orgDtos[0].notes)
        assertEquals("type-one", orgDtos[0].orgTypeName)
        
        assertEquals(2, orgDtos[1].id)
        assertEquals("Organization Two", orgDtos[1].name)
        assertEquals("456 Second St", orgDtos[1].address)
        assertEquals("555-2222", orgDtos[1].phone)
        assertEquals("City Two", orgDtos[1].city)
        assertEquals("Country Two", orgDtos[1].country)
        assertEquals("Notes Two", orgDtos[1].notes)
        assertEquals("type-two", orgDtos[1].orgTypeName)
    }

    @Test
    fun `should map dto list to entity list`() {
        // Given
        val orgDto1 = OrgDto(
            id = 1,
            name = "Organization One",
            address = "123 First St",
            phone = "555-1111",
            city = "City One",
            country = "Country One",
            notes = "Notes One",
            orgTypeName = "type-one"
        )
        val orgDto2 = OrgDto(
            id = 2,
            name = "Organization Two",
            address = "456 Second St",
            phone = "555-2222",
            city = "City Two",
            country = "Country Two",
            notes = "Notes Two",
            orgTypeName = "type-two"
        )
        val orgDtos = listOf(orgDto1, orgDto2)

        // When
        val orgs = orgMapper.toEntityList(orgDtos)

        // Then
        assertEquals(2, orgs.size)
        assertEquals(1, orgs[0].id)
        assertEquals("Organization One", orgs[0].name)
        assertEquals("123 First St", orgs[0].address)
        assertEquals("555-1111", orgs[0].phone)
        assertEquals("City One", orgs[0].city)
        assertEquals("Country One", orgs[0].country)
        assertEquals("Notes One", orgs[0].notes)
        assertEquals("type-one", orgs[0].orgTypeName)
        assertEquals(null, orgs[0].orgType)
        
        assertEquals(2, orgs[1].id)
        assertEquals("Organization Two", orgs[1].name)
        assertEquals("456 Second St", orgs[1].address)
        assertEquals("555-2222", orgs[1].phone)
        assertEquals("City Two", orgs[1].city)
        assertEquals("Country Two", orgs[1].country)
        assertEquals("Notes Two", orgs[1].notes)
        assertEquals("type-two", orgs[1].orgTypeName)
        // orgType should be null as it's ignored in mapping
        assertEquals(null, orgs[1].orgType)
    }

    @Test
    fun `should handle null values correctly`() {
        // Given
        val org = Org(
            id = 1,
            name = "Test Organization",
            address = null,
            phone = null,
            city = null,
            country = null,
            notes = null,
            orgTypeName = "test-org-type"
        )

        // When
        val orgDto = orgMapper.toDto(org)

        // Then
        assertEquals(1, orgDto.id)
        assertEquals("Test Organization", orgDto.name)
        assertNull(orgDto.address)
        assertNull(orgDto.phone)
        assertNull(orgDto.city)
        assertNull(orgDto.country)
        assertNull(orgDto.notes)
        assertEquals("test-org-type", orgDto.orgTypeName)
    }
}