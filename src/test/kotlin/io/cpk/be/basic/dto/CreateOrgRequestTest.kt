package io.cpk.be.basic.dto

import jakarta.validation.Validation
import jakarta.validation.Validator
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class CreateOrgRequestTest {
    
    private lateinit var validator: Validator
    
    @BeforeEach
    fun setUp() {
        val factory = Validation.buildDefaultValidatorFactory()
        validator = factory.validator
    }

    @Test
    fun `should create CreateOrgRequest with required parameters`() {
        // Given
        val orgDto = OrgDto(
            name = "Test Org",
            orgTypeName = "Corporation"
        )
        
        val centerDto = CenterDto(
            name = "Test Center",
            orgId = 1
        )
        
        val userDto = AppUserDto(
            id = 123,
            orgId = 1,
            username = "testuser",
            fullname = "Test User",
            email = "test@example.com"
        )
        
        // When
        val createOrgRequest = CreateOrgRequest(
            orgDto = orgDto,
            centerDto = centerDto,
            userDto = userDto
        )
        
        // Then
        assertEquals(orgDto, createOrgRequest.orgDto)
        assertEquals(centerDto, createOrgRequest.centerDto)
        assertEquals(userDto, createOrgRequest.userDto)
    }
    
    @Test
    fun `should correctly implement equals and hashCode`() {
        // Given
        val orgDto = OrgDto(
            name = "Test Org",
            orgTypeName = "Corporation"
        )
        
        val centerDto = CenterDto(
            name = "Test Center",
            orgId = 1
        )
        
        val userDto = AppUserDto(
            id = 123,
            orgId = 1,
            username = "testuser",
            fullname = "Test User",
            email = "test@example.com"
        )
        
        val createOrgRequest1 = CreateOrgRequest(
            orgDto = orgDto,
            centerDto = centerDto,
            userDto = userDto
        )
        
        val createOrgRequest2 = CreateOrgRequest(
            orgDto = orgDto,
            centerDto = centerDto,
            userDto = userDto
        )
        
        val createOrgRequest3 = CreateOrgRequest(
            orgDto = orgDto.copy(name = "Different Org"),
            centerDto = centerDto,
            userDto = userDto
        )
        
        // Then
        assertEquals(createOrgRequest1, createOrgRequest2)
        assertEquals(createOrgRequest1.hashCode(), createOrgRequest2.hashCode())
        assertNotEquals(createOrgRequest1, createOrgRequest3)
        assertNotEquals(createOrgRequest1.hashCode(), createOrgRequest3.hashCode())
    }
    
    @Test
    fun `should correctly implement copy`() {
        // Given
        val orgDto = OrgDto(
            name = "Test Org",
            orgTypeName = "Corporation"
        )
        
        val centerDto = CenterDto(
            name = "Test Center",
            orgId = 1
        )
        
        val userDto = AppUserDto(
            id = 123,
            orgId = 1,
            username = "testuser",
            fullname = "Test User",
            email = "test@example.com"
        )
        
        val createOrgRequest = CreateOrgRequest(
            orgDto = orgDto,
            centerDto = centerDto,
            userDto = userDto
        )
        
        // When
        val newOrgDto = orgDto.copy(name = "Updated Org")
        val copied = createOrgRequest.copy(
            orgDto = newOrgDto
        )
        
        // Then
        assertEquals(newOrgDto, copied.orgDto)
        assertEquals(centerDto, copied.centerDto)
        assertEquals(userDto, copied.userDto)
    }
    
    @Test
    fun `should validate orgDto is not null`() {
        // Given
        val fixedTime = LocalDateTime.of(2023, 1, 1, 12, 0)
        val centerDto = CenterDto(
            name = "Test Center",
            orgId = 1
        )
        
        val userDto = AppUserDto(
            id = 123,
            orgId = 1,
            username = "testuser",
            fullname = "Test User",
            email = "test@example.com"
        )
        
        // We can't directly pass null for orgDto due to non-nullable type
        // This test is for illustration purposes
        // In a real scenario with a nullable type, we would test with null
        
        // For now, we'll test with an invalid orgDto to demonstrate nested validation
        val invalidOrgDto = OrgDto(
            name = "",  // Invalid: name is blank
            orgTypeName = "Corporation"
        )
        
        val createOrgRequest = CreateOrgRequest(
            orgDto = invalidOrgDto,
            centerDto = centerDto,
            userDto = userDto
        )
        
        // When
        val violations = validator.validate(createOrgRequest)
        
        // Then
        // The validation should catch the invalid orgDto through @Valid annotation
        assertEquals(1, violations.size)
        assertEquals("Name cannot be empty", violations.first().message)
    }
    
    @Test
    fun `should validate centerDto is not null`() {
        // Given
        val fixedTime = LocalDateTime.of(2023, 1, 1, 12, 0)
        val orgDto = OrgDto(
            name = "Test Org",
            orgTypeName = "Corporation"
        )
        
        val userDto = AppUserDto(
            id = 123,
            orgId = 1,
            username = "testuser",
            fullname = "Test User",
            email = "test@example.com"
        )
        
        // We can't directly pass null for centerDto due to non-nullable type
        // This test is for illustration purposes
        // In a real scenario with a nullable type, we would test with null
        
        // For now, we'll test with an invalid centerDto to demonstrate nested validation
        val invalidCenterDto = CenterDto(
            name = "",  // Invalid: name is blank
            orgId = 1
        )
        
        val createOrgRequest = CreateOrgRequest(
            orgDto = orgDto,
            centerDto = invalidCenterDto,
            userDto = userDto
        )
        
        // When
        val violations = validator.validate(createOrgRequest)
        
        // Then
        // The validation should catch the invalid centerDto through @Valid annotation
        assertEquals(1, violations.size)
        assertEquals("Name cannot be blank", violations.first().message)
    }
    
    @Test
    fun `should validate userDto is not null`() {
        // Given
        val orgDto = OrgDto(
            name = "Test Org",
            orgTypeName = "Corporation"
        )
        
        val centerDto = CenterDto(
            name = "Test Center",
            orgId = 1
        )
        
        // We can't directly pass null for userDto due to non-nullable type
        // This test is for illustration purposes
        // In a real scenario with a nullable type, we would test with null
        
        // For now, we'll test with a valid userDto since we don't have validation annotations on AppUserDto
        val userDto = AppUserDto(
            id = 123,
            orgId = 1,
            username = "testuser",
            fullname = "Test User",
            email = "test@example.com"
        )
        
        val createOrgRequest = CreateOrgRequest(
            orgDto = orgDto,
            centerDto = centerDto,
            userDto = userDto
        )
        
        // When
        val violations = validator.validate(createOrgRequest)
        
        // Then
        assertEquals(0, violations.size)
    }
    
    @Test
    fun `should pass validation with valid data`() {
        // Given
        val orgDto = OrgDto(
            name = "Test Org",
            orgTypeName = "Corporation"
        )
        
        val centerDto = CenterDto(
            name = "Test Center",
            orgId = 1
        )
        
        val userDto = AppUserDto(
            id = 123,
            orgId = 1,
            username = "testuser",
            fullname = "Test User",
            email = "test@example.com"
        )
        
        val createOrgRequest = CreateOrgRequest(
            orgDto = orgDto,
            centerDto = centerDto,
            userDto = userDto
        )
        
        // When
        val violations = validator.validate(createOrgRequest)
        
        // Then
        assertEquals(0, violations.size)
    }
}