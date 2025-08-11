package io.cpk.be.basic.dto

import jakarta.validation.Validation
import jakarta.validation.Validator
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class AppUserRoleDtoTest {
    
    private lateinit var validator: Validator
    
    @BeforeEach
    fun setUp() {
        val factory = Validation.buildDefaultValidatorFactory()
        validator = factory.validator
    }

    @Test
    fun `should create AppUserRoleDto with required parameters`() {
        // Given
        val userId = 123
        val orgId = 1
        val centerId = 2
        val roleName = "ADMIN"
        
        // When
        val appUserRoleDto = AppUserRoleDto(
            userId = userId,
            orgId = orgId,
            centerId = centerId,
            roleName = roleName
        )
        
        // Then
        assertEquals(userId, appUserRoleDto.userId)
        assertEquals(orgId, appUserRoleDto.orgId)
        assertEquals(centerId, appUserRoleDto.centerId)
        assertEquals(roleName, appUserRoleDto.roleName)
    }
    
    @Test
    fun `should correctly implement equals and hashCode`() {
        // Given
        val appUserRoleDto1 = AppUserRoleDto(
            userId = 123,
            orgId = 1,
            centerId = 2,
            roleName = "ADMIN"
        )
        
        val appUserRoleDto2 = AppUserRoleDto(
            userId = 123,
            orgId = 1,
            centerId = 2,
            roleName = "ADMIN"
        )
        
        val appUserRoleDto3 = AppUserRoleDto(
            userId = 456,
            orgId = 1,
            centerId = 2,
            roleName = "ADMIN"
        )
        
        // Then
        assertEquals(appUserRoleDto1, appUserRoleDto2)
        assertEquals(appUserRoleDto1.hashCode(), appUserRoleDto2.hashCode())
        assertNotEquals(appUserRoleDto1, appUserRoleDto3)
        assertNotEquals(appUserRoleDto1.hashCode(), appUserRoleDto3.hashCode())
    }
    
    @Test
    fun `should correctly implement copy`() {
        // Given
        val appUserRoleDto = AppUserRoleDto(
            userId = 123,
            orgId = 1,
            centerId = 2,
            roleName = "ADMIN"
        )
        
        // When
        val copied = appUserRoleDto.copy(
            roleName = "USER",
            centerId = 3
        )
        
        // Then
        assertEquals(123, copied.userId)
        assertEquals(1, copied.orgId)
        assertEquals(3, copied.centerId)
        assertEquals("USER", copied.roleName)
    }
    
    @Test
    fun `should pass validation with valid fields`() {
        // Given
        val appUserRoleDto = AppUserRoleDto(
            userId = 123,
            orgId = 1,
            centerId = 2,
            roleName = "ADMIN"
        )
        
        // When
        val violations = validator.validate(appUserRoleDto)
        
        // Then
        assertEquals(0, violations.size)
    }
    
    @Test
    fun `should validate roleName is not empty`() {
        // Given
        val appUserRoleDto = AppUserRoleDto(
            userId = 123,
            orgId = 1,
            centerId = 2,
            roleName = ""
        )
        
        // When
        val violations = validator.validate(appUserRoleDto)
        
        // Then
        assertEquals(1, violations.size)
        assertEquals("Role name cannot be empty", violations.first().message)
    }
    
    @Test
    fun `should pass validation with valid data`() {
        // Given
        val appUserRoleDto = AppUserRoleDto(
            userId = 123,
            orgId = 1,
            centerId = 2,
            roleName = "ADMIN"
        )
        
        // When
        val violations = validator.validate(appUserRoleDto)
        
        // Then
        assertEquals(0, violations.size)
    }
}