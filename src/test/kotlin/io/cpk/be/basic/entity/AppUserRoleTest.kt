package io.cpk.be.basic.entity

import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import kotlin.test.*

class AppUserRoleTest {

    @Test
    fun `should create AppUserRole with required parameters`() {
        // Given
        val userId = "user123"
        val orgId = 1
        val centerId = 2
        val roleName = "admin"
        
        // When
        val appUserRole = AppUserRole(
            userId = userId,
            orgId = orgId,
            centerId = centerId,
            roleName = roleName
        )
        
        // Then
        assertEquals(userId, appUserRole.userId)
        assertEquals(orgId, appUserRole.orgId)
        assertEquals(centerId, appUserRole.centerId)
        assertEquals(roleName, appUserRole.roleName)
        assertNotNull(appUserRole.createdAt)
        assertNotNull(appUserRole.updatedAt)
        assertNull(appUserRole.user)
        assertNull(appUserRole.org)
        assertNull(appUserRole.center)
        assertNull(appUserRole.role)
    }
    
    @Test
    fun `should create AppUserRole with all parameters`() {
        // Given
        val userId = "user123"
        val orgId = 1
        val centerId = 2
        val roleName = "admin"
        val org = Org(
            id = orgId,
            name = "Test Org",
            orgTypeName = "test-type"
        )
        val user = AppUser(
            id = userId,
            org = org,
            username = "testuser",
            fullname = "Test User",
            email = "test@example.com"
        )
        val center = Center(
            id = centerId,
            name = "Test Center",
            org = org
        )
        val role = Role(
            orgId = orgId,
            name = roleName
        )
        
        // When
        val appUserRole = AppUserRole(
            userId = userId,
            orgId = orgId,
            centerId = centerId,
            roleName = roleName,
            user = user,
            org = org,
            center = center,
            role = role
        )
        
        // Then
        assertEquals(userId, appUserRole.userId)
        assertEquals(orgId, appUserRole.orgId)
        assertEquals(centerId, appUserRole.centerId)
        assertEquals(roleName, appUserRole.roleName)
        assertNotNull(appUserRole.createdAt)
        assertNotNull(appUserRole.updatedAt)
        assertEquals(user, appUserRole.user)
        assertEquals(org, appUserRole.org)
        assertEquals(center, appUserRole.center)
        assertEquals(role, appUserRole.role)
    }
    
    @Test
    fun `should use default values for timestamps if not provided`() {
        // Given
        val userId = "user123"
        val orgId = 1
        val centerId = 2
        val roleName = "admin"
        val beforeCreation = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS)
        
        // When
        val appUserRole = AppUserRole(
            userId = userId,
            orgId = orgId,
            centerId = centerId,
            roleName = roleName
        )
        
        // Then
        val afterCreation = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS)
        
        // Check that timestamps are between beforeCreation and afterCreation
        assert(appUserRole.createdAt.truncatedTo(ChronoUnit.SECONDS) >= beforeCreation)
        assert(appUserRole.createdAt.truncatedTo(ChronoUnit.SECONDS) <= afterCreation)
        assert(appUserRole.updatedAt.truncatedTo(ChronoUnit.SECONDS) >= beforeCreation)
        assert(appUserRole.updatedAt.truncatedTo(ChronoUnit.SECONDS) <= afterCreation)
    }
    
    @Test
    fun `should correctly implement equals and hashCode`() {
        // Given
        val appUserRole1 = AppUserRole(
            userId = "user123",
            orgId = 1,
            centerId = 2,
            roleName = "admin"
        )
        val appUserRole2 = AppUserRole(
            userId = "user123",
            orgId = 1,
            centerId = 2,
            roleName = "admin"
        )
        val appUserRole3 = AppUserRole(
            userId = "user456",
            orgId = 1,
            centerId = 2,
            roleName = "admin"
        )
        
        // Then
        assertEquals(appUserRole1, appUserRole2)
        assertEquals(appUserRole1.hashCode(), appUserRole2.hashCode())
        assertNotEquals(appUserRole1, appUserRole3)
        assertNotEquals(appUserRole1.hashCode(), appUserRole3.hashCode())
    }
    
    @Test
    fun `should correctly implement copy`() {
        // Given
        val appUserRole = AppUserRole(
            userId = "user123",
            orgId = 1,
            centerId = 2,
            roleName = "admin"
        )
        
        // When
        val copied = appUserRole.copy(
            userId = "user456",
            roleName = "user"
        )
        
        // Then
        assertEquals("user456", copied.userId)
        assertEquals(1, copied.orgId)
        assertEquals(2, copied.centerId)
        assertEquals("user", copied.roleName)
    }
    
    @Test
    fun `should create AppUserRoleId with default values`() {
        // When
        val id = AppUserRole.AppUserRoleId()
        
        // Then
        assertEquals("", id.userId)
        assertEquals(0, id.orgId)
        assertEquals(0, id.centerId)
        assertEquals("", id.roleName)
    }
    
    @Test
    fun `should create AppUserRoleId with specified values`() {
        // Given
        val userId = "user123"
        val orgId = 1
        val centerId = 2
        val roleName = "admin"
        
        // When
        val id = AppUserRole.AppUserRoleId(
            userId = userId,
            orgId = orgId,
            centerId = centerId,
            roleName = roleName
        )
        
        // Then
        assertEquals(userId, id.userId)
        assertEquals(orgId, id.orgId)
        assertEquals(centerId, id.centerId)
        assertEquals(roleName, id.roleName)
    }
    
    @Test
    fun `AppUserRoleId should correctly implement equals and hashCode`() {
        // Given
        val id1 = AppUserRole.AppUserRoleId(
            userId = "user123",
            orgId = 1,
            centerId = 2,
            roleName = "admin"
        )
        val id2 = AppUserRole.AppUserRoleId(
            userId = "user123",
            orgId = 1,
            centerId = 2,
            roleName = "admin"
        )
        val id3 = AppUserRole.AppUserRoleId(
            userId = "user456",
            orgId = 1,
            centerId = 2,
            roleName = "admin"
        )
        
        // Then
        assertEquals(id1, id2)
        assertEquals(id1.hashCode(), id2.hashCode())
        assertNotEquals(id1, id3)
        assertNotEquals(id1.hashCode(), id3.hashCode())
    }
    
    @Test
    fun `AppUserRoleId should correctly implement copy`() {
        // Given
        val id = AppUserRole.AppUserRoleId(
            userId = "user123",
            orgId = 1,
            centerId = 2,
            roleName = "admin"
        )
        
        // When
        val copied = id.copy(
            userId = "user456",
            roleName = "user"
        )
        
        // Then
        assertEquals("user456", copied.userId)
        assertEquals(1, copied.orgId)
        assertEquals(2, copied.centerId)
        assertEquals("user", copied.roleName)
    }
    
    @Test
    fun `should create AppUserRole with no-arg constructor`() {
        // When
        val appUserRole = AppUserRole()
        
        // Then
        assertEquals("", appUserRole.userId)
        assertEquals(0, appUserRole.orgId)
        assertEquals(0, appUserRole.centerId)
        assertEquals("", appUserRole.roleName)
        assertNull(appUserRole.user)
        assertNull(appUserRole.org)
        assertNull(appUserRole.center)
        assertNull(appUserRole.role)
        assertNotNull(appUserRole.createdAt)
        assertNotNull(appUserRole.updatedAt)
    }
    
    @Test
    fun `should correctly implement toString`() {
        // Given
        val userId = "user123"
        val orgId = 1
        val centerId = 2
        val roleName = "admin"
        
        val appUserRole = AppUserRole(
            userId = userId,
            orgId = orgId,
            centerId = centerId,
            roleName = roleName
        )
        
        // When
        val toString = appUserRole.toString()
        
        // Then
        assertTrue(toString.contains("AppUserRole"))
        assertTrue(toString.contains("userId='$userId'"))
        assertTrue(toString.contains("orgId=$orgId"))
        assertTrue(toString.contains("centerId=$centerId"))
        assertTrue(toString.contains("roleName='$roleName'"))
    }
    
    @Test
    fun `should handle edge cases in equals method`() {
        // Given
        val appUserRole = AppUserRole(
            userId = "user123",
            orgId = 1,
            centerId = 2,
            roleName = "admin"
        )
        
        // Then
        assertEquals(appUserRole, appUserRole) // Same instance
        
        // Null comparison
        val nullAppUserRole: AppUserRole? = null
        assertNotEquals(appUserRole, nullAppUserRole)
        
        // Different type comparison
        val differentObject = Any()
        assertFalse(appUserRole.equals(differentObject))
    }
    
    @Test
    fun `should test all branches in equals method`() {
        // Given
        val baseAppUserRole = AppUserRole(
            userId = "user123",
            orgId = 1,
            centerId = 2,
            roleName = "admin"
        )
        
        // Different userId
        val appUserRoleDiffUserId = baseAppUserRole.copy(userId = "user456")
        assertNotEquals(baseAppUserRole, appUserRoleDiffUserId)
        
        // Different orgId
        val appUserRoleDiffOrgId = baseAppUserRole.copy(orgId = 3)
        assertNotEquals(baseAppUserRole, appUserRoleDiffOrgId)
        
        // Different centerId
        val appUserRoleDiffCenterId = baseAppUserRole.copy(centerId = 4)
        assertNotEquals(baseAppUserRole, appUserRoleDiffCenterId)
        
        // Different roleName
        val appUserRoleDiffRoleName = baseAppUserRole.copy(roleName = "user")
        assertNotEquals(baseAppUserRole, appUserRoleDiffRoleName)
    }
    
    @Test
    fun `AppUserRoleId should handle edge cases in equals method`() {
        // Given
        val id = AppUserRole.AppUserRoleId(
            userId = "user123",
            orgId = 1,
            centerId = 2,
            roleName = "admin"
        )
        
        // Then
        assertEquals(id, id) // Same instance
        
        // Null comparison
        val nullId: AppUserRole.AppUserRoleId? = null
        assertNotEquals(id, nullId)
        
        // Different type comparison
        val differentObject = Any()
        assertNotEquals(id, differentObject)
    }
    
    @Test
    fun `AppUserRoleId should correctly implement toString`() {
        // Given
        val userId = "user123"
        val orgId = 1
        val centerId = 2
        val roleName = "admin"
        
        val id = AppUserRole.AppUserRoleId(
            userId = userId,
            orgId = orgId,
            centerId = centerId,
            roleName = roleName
        )
        
        // When
        val toString = id.toString()
        
        // Then
        assertTrue(toString.contains("userId=$userId"))
        assertTrue(toString.contains("orgId=$orgId"))
        assertTrue(toString.contains("centerId=$centerId"))
        assertTrue(toString.contains("roleName=$roleName"))
    }
}