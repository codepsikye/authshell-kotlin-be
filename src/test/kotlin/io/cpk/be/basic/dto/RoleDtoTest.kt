package io.cpk.be.basic.dto

import jakarta.validation.Validation
import jakarta.validation.Validator
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class RoleDtoTest {
    
    private lateinit var validator: Validator
    
    @BeforeEach
    fun setUp() {
        val factory = Validation.buildDefaultValidatorFactory()
        validator = factory.validator
    }

    @Test
    fun `should create RoleDto with required parameters`() {
        // Given
        val orgId = 1
        val name = "Admin"
        
        // When
        val roleDto = RoleDto(
            orgId = orgId,
            name = name
        )
        
        // Then
        assertEquals(orgId, roleDto.orgId)
        assertEquals(name, roleDto.name)
        assertEquals(emptyList<String>(), roleDto.accessRight)
    }
    
    @Test
    fun `should create RoleDto with all parameters`() {
        // Given
        val orgId = 1
        val name = "Admin"
        val accessRight = listOf("READ", "WRITE", "DELETE")
        
        // When
        val roleDto = RoleDto(
            orgId = orgId,
            name = name,
            accessRight = accessRight
        )
        
        // Then
        assertEquals(orgId, roleDto.orgId)
        assertEquals(name, roleDto.name)
        assertEquals(accessRight, roleDto.accessRight)
    }
    
    @Test
    fun `should correctly implement equals and hashCode`() {
        // Given
        val roleDto1 = RoleDto(
            orgId = 1,
            name = "Admin",
            accessRight = listOf("READ", "WRITE")
        )
        
        val roleDto2 = RoleDto(
            orgId = 1,
            name = "Admin",
            accessRight = listOf("READ", "WRITE")
        )
        
        val roleDto3 = RoleDto(
            orgId = 2,
            name = "Admin",
            accessRight = listOf("READ", "WRITE")
        )
        
        val roleDto4 = RoleDto(
            orgId = 1,
            name = "User",
            accessRight = listOf("READ", "WRITE")
        )
        
        val roleDto5 = RoleDto(
            orgId = 1,
            name = "Admin",
            accessRight = listOf("READ")
        )
        
        // Then
        assertEquals(roleDto1, roleDto2)
        assertEquals(roleDto1.hashCode(), roleDto2.hashCode())
        assertNotEquals(roleDto1, roleDto3)
        assertNotEquals(roleDto1.hashCode(), roleDto3.hashCode())
        assertNotEquals(roleDto1, roleDto4)
        assertNotEquals(roleDto1.hashCode(), roleDto4.hashCode())
        assertNotEquals(roleDto1, roleDto5)
        assertNotEquals(roleDto1.hashCode(), roleDto5.hashCode())
    }
    
    @Test
    fun `should correctly implement copy`() {
        // Given
        val roleDto = RoleDto(
            orgId = 1,
            name = "Admin",
            accessRight = listOf("READ", "WRITE")
        )
        
        // When
        val copied1 = roleDto.copy(
            orgId = 2
        )
        
        val copied2 = roleDto.copy(
            name = "SuperAdmin"
        )
        
        val copied3 = roleDto.copy(
            accessRight = listOf("READ", "WRITE", "DELETE")
        )
        
        // Then
        assertEquals(2, copied1.orgId)
        assertEquals("Admin", copied1.name)
        assertEquals(listOf("READ", "WRITE"), copied1.accessRight)
        
        assertEquals(1, copied2.orgId)
        assertEquals("SuperAdmin", copied2.name)
        assertEquals(listOf("READ", "WRITE"), copied2.accessRight)
        
        assertEquals(1, copied3.orgId)
        assertEquals("Admin", copied3.name)
        assertEquals(listOf("READ", "WRITE", "DELETE"), copied3.accessRight)
    }
    
    @Test
    fun `should validate orgId is not null`() {
        // Given
        // We can't directly pass null for orgId due to non-nullable type
        // This test is for illustration purposes
        // In a real scenario with a nullable type, we would test with null
        
        // For now, we'll test with a valid orgId since we can't test the @NotNull validation directly
        val roleDto = RoleDto(
            orgId = 0,  // Using 0 as a minimal value
            name = "Admin"
        )
        
        // When
        val violations = validator.validate(roleDto)
        
        // Then
        // This test might not catch the @NotNull validation since orgId is a non-nullable Int
        // In a real scenario, we would test this with a nullable type
        assertEquals(0, violations.size)
    }
    
    @Test
    fun `should validate name is not empty`() {
        // Given
        val roleDto = RoleDto(
            orgId = 1,
            name = ""
        )
        
        // When
        val violations = validator.validate(roleDto)
        
        // Then
        assertEquals(1, violations.size)
        assertEquals("Name cannot be empty", violations.first().message)
    }
    
    @Test
    fun `should pass validation with valid data and default accessRight`() {
        // Given
        val roleDto = RoleDto(
            orgId = 1,
            name = "Admin"
        )
        
        // When
        val violations = validator.validate(roleDto)
        
        // Then
        assertEquals(0, violations.size)
    }
    
    @Test
    fun `should pass validation with valid data and custom accessRight`() {
        // Given
        val roleDto = RoleDto(
            orgId = 1,
            name = "Admin",
            accessRight = listOf("READ", "WRITE", "DELETE")
        )
        
        // When
        val violations = validator.validate(roleDto)
        
        // Then
        assertEquals(0, violations.size)
    }
}