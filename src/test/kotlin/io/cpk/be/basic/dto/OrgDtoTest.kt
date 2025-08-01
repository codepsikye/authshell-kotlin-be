package io.cpk.be.basic.dto

import jakarta.validation.Validation
import jakarta.validation.Validator
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNull

class OrgDtoTest {
    
    private lateinit var validator: Validator
    
    @BeforeEach
    fun setUp() {
        val factory = Validation.buildDefaultValidatorFactory()
        validator = factory.validator
    }

    @Test
    fun `should create OrgDto with required parameters`() {
        // Given
        val name = "Test Organization"
        val orgTypeName = "Corporation"
        
        // When
        val orgDto = OrgDto(
            name = name,
            orgTypeName = orgTypeName
        )
        
        // Then
        assertNull(orgDto.id)
        assertEquals(name, orgDto.name)
        assertNull(orgDto.address)
        assertNull(orgDto.phone)
        assertNull(orgDto.city)
        assertNull(orgDto.country)
        assertNull(orgDto.notes)
        assertEquals(orgTypeName, orgDto.orgTypeName)
        assertEquals(emptyMap<String, Any>(), orgDto.orgConfigs)
    }
    
    @Test
    fun `should create OrgDto with all parameters`() {
        // Given
        val id = 1
        val name = "Test Organization"
        val address = "123 Test St"
        val phone = "123-456-7890"
        val city = "Test City"
        val country = "Test Country"
        val notes = "Test notes"
        val orgTypeName = "Corporation"
        val orgConfigs = mapOf("maxUsers" to 10, "maxCenters" to 5, "defaultCenter" to 1)
        
        // When
        val orgDto = OrgDto(
            id = id,
            name = name,
            address = address,
            phone = phone,
            city = city,
            country = country,
            notes = notes,
            orgTypeName = orgTypeName,
            orgConfigs = orgConfigs
        )
        
        // Then
        assertEquals(id, orgDto.id)
        assertEquals(name, orgDto.name)
        assertEquals(address, orgDto.address)
        assertEquals(phone, orgDto.phone)
        assertEquals(city, orgDto.city)
        assertEquals(country, orgDto.country)
        assertEquals(notes, orgDto.notes)
        assertEquals(orgTypeName, orgDto.orgTypeName)
        assertEquals(orgConfigs, orgDto.orgConfigs)
    }
    
    @Test
    fun `should correctly implement equals and hashCode`() {
        // Given
        val orgConfigs1 = mapOf("maxUsers" to 10)
        val orgConfigs2 = mapOf("maxUsers" to 20)
        
        val orgDto1 = OrgDto(
            id = 1,
            name = "Test Organization",
            orgTypeName = "Corporation",
            orgConfigs = orgConfigs1
        )
        
        val orgDto2 = OrgDto(
            id = 1,
            name = "Test Organization",
            orgTypeName = "Corporation",
            orgConfigs = orgConfigs1
        )
        
        val orgDto3 = OrgDto(
            id = 2,
            name = "Test Organization",
            orgTypeName = "Corporation",
            orgConfigs = orgConfigs1
        )
        
        val orgDto4 = OrgDto(
            id = 1,
            name = "Test Organization",
            orgTypeName = "Corporation",
            orgConfigs = orgConfigs2
        )
        
        // Then
        assertEquals(orgDto1, orgDto2)
        assertEquals(orgDto1.hashCode(), orgDto2.hashCode())
        assertNotEquals(orgDto1, orgDto3)
        assertNotEquals(orgDto1.hashCode(), orgDto3.hashCode())
        assertNotEquals(orgDto1, orgDto4)
        assertNotEquals(orgDto1.hashCode(), orgDto4.hashCode())
    }
    
    @Test
    fun `should correctly implement copy`() {
        // Given
        val orgConfigs = mapOf("maxUsers" to 10, "maxCenters" to 5)
        val orgDto = OrgDto(
            id = 1,
            name = "Test Organization",
            orgTypeName = "Corporation",
            orgConfigs = orgConfigs
        )
        
        // When - copy with some changes but keep orgConfigs
        val copied1 = orgDto.copy(
            name = "Updated Organization",
            address = "456 New St"
        )
        
        // When - copy with new orgConfigs
        val newOrgConfigs = mapOf("maxUsers" to 20, "maxCenters" to 10)
        val copied2 = orgDto.copy(
            name = "Another Organization",
            orgConfigs = newOrgConfigs
        )
        
        // Then - check copied1
        assertEquals(1, copied1.id)
        assertEquals("Updated Organization", copied1.name)
        assertEquals("456 New St", copied1.address)
        assertEquals("Corporation", copied1.orgTypeName)
        assertEquals(orgConfigs, copied1.orgConfigs)
        
        // Then - check copied2
        assertEquals(1, copied2.id)
        assertEquals("Another Organization", copied2.name)
        assertEquals("Corporation", copied2.orgTypeName)
        assertEquals(newOrgConfigs, copied2.orgConfigs)
    }
    
    @Test
    fun `should validate name is not empty`() {
        // Given
        val orgDto = OrgDto(
            name = "",
            orgTypeName = "Corporation"
        )
        
        // When
        val violations = validator.validate(orgDto)
        
        // Then
        assertEquals(1, violations.size)
        assertEquals("Name cannot be empty", violations.first().message)
    }
    
    @Test
    fun `should validate orgTypeName is not empty`() {
        // Given
        val orgDto = OrgDto(
            name = "Test Organization",
            orgTypeName = ""
        )
        
        // When
        val violations = validator.validate(orgDto)
        
        // Then
        assertEquals(1, violations.size)
        assertEquals("Org type name cannot be empty", violations.first().message)
    }
    
    @Test
    fun `should validate both required fields are not empty`() {
        // Given
        val orgDto = OrgDto(
            name = "",
            orgTypeName = ""
        )
        
        // When
        val violations = validator.validate(orgDto)
        
        // Then
        assertEquals(2, violations.size)
        val messages = violations.map { it.message }.toSet()
        assertEquals(setOf("Name cannot be empty", "Org type name cannot be empty"), messages)
    }
    
    @Test
    fun `should pass validation with valid data`() {
        // Given
        val orgDto = OrgDto(
            name = "Test Organization",
            orgTypeName = "Corporation"
        )
        
        // When
        val violations = validator.validate(orgDto)
        
        // Then
        assertEquals(0, violations.size)
    }
}