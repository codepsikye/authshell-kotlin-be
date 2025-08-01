package io.cpk.be.basic.entity

import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class RoleTest {

    @Test
    fun `should create Role with required parameters`() {
        // Given
        val orgId = 1
        val name = "admin"
        
        // When
        val role = Role(
            orgId = orgId,
            name = name
        )
        
        // Then
        assertEquals(orgId, role.orgId)
        assertEquals(name, role.name)
        assertEquals(emptyList<String>(), role.accessRight)
        assertNotNull(role.createdAt)
        assertNotNull(role.updatedAt)
        assertNull(role.org)
    }
    
    @Test
    fun `should create Role with all parameters`() {
        // Given
        val orgId = 1
        val name = "admin"
        val accessRight = listOf("read", "write", "delete")
        val org = Org(
            id = orgId,
            name = "Test Org",
            orgTypeName = "test-type"
        )
        
        // When
        val role = Role(
            orgId = orgId,
            name = name,
            accessRight = accessRight,
            org = org
        )
        
        // Then
        assertEquals(orgId, role.orgId)
        assertEquals(name, role.name)
        assertEquals(accessRight, role.accessRight)
        assertNotNull(role.createdAt)
        assertNotNull(role.updatedAt)
        assertEquals(org, role.org)
    }
    
    @Test
    fun `should use default values for optional parameters if not provided`() {
        // Given
        val orgId = 1
        val name = "admin"
        val beforeCreation = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS)
        
        // When
        val role = Role(
            orgId = orgId,
            name = name
        )
        
        // Then
        val afterCreation = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS)
        
        assertEquals(emptyList<String>(), role.accessRight)
        assertNull(role.org)
        
        // Check that timestamps are between beforeCreation and afterCreation
        assert(role.createdAt.truncatedTo(ChronoUnit.SECONDS) >= beforeCreation)
        assert(role.createdAt.truncatedTo(ChronoUnit.SECONDS) <= afterCreation)
        assert(role.updatedAt.truncatedTo(ChronoUnit.SECONDS) >= beforeCreation)
        assert(role.updatedAt.truncatedTo(ChronoUnit.SECONDS) <= afterCreation)
    }
    
    @Test
    fun `should correctly implement equals and hashCode`() {
        // Given
        val role1 = Role(
            orgId = 1,
            name = "admin",
            accessRight = listOf("read", "write")
        )
        val role2 = Role(
            orgId = 1,
            name = "admin",
            accessRight = listOf("read", "write")
        )
        val role3 = Role(
            orgId = 1,
            name = "user",
            accessRight = listOf("read")
        )
        val role4 = Role(
            orgId = 2,
            name = "admin",
            accessRight = listOf("read", "write")
        )
        
        // Then
        assertEquals(role1, role2)
        assertEquals(role1.hashCode(), role2.hashCode())
        assertNotEquals(role1, role3)
        assertNotEquals(role1.hashCode(), role3.hashCode())
        assertNotEquals(role1, role4)
        assertNotEquals(role1.hashCode(), role4.hashCode())
    }
    
    @Test
    fun `should correctly implement copy`() {
        // Given
        val role = Role(
            orgId = 1,
            name = "admin",
            accessRight = listOf("read", "write")
        )
        
        // When
        val copied = role.copy(
            name = "super-admin",
            accessRight = listOf("read", "write", "delete", "admin")
        )
        
        // Then
        assertEquals(1, copied.orgId)
        assertEquals("super-admin", copied.name)
        assertEquals(listOf("read", "write", "delete", "admin"), copied.accessRight)
        assertNotNull(copied.createdAt)
        assertNotNull(copied.updatedAt)
    }
    
    @Test
    fun `should create RoleId with default values`() {
        // When
        val id = Role.RoleId()
        
        // Then
        assertEquals(0, id.orgId)
        assertEquals("", id.name)
    }
    
    @Test
    fun `should create RoleId with specified values`() {
        // Given
        val orgId = 1
        val name = "admin"
        
        // When
        val id = Role.RoleId(
            orgId = orgId,
            name = name
        )
        
        // Then
        assertEquals(orgId, id.orgId)
        assertEquals(name, id.name)
    }
    
    @Test
    fun `RoleId should correctly implement equals and hashCode`() {
        // Given
        val id1 = Role.RoleId(
            orgId = 1,
            name = "admin"
        )
        val id2 = Role.RoleId(
            orgId = 1,
            name = "admin"
        )
        val id3 = Role.RoleId(
            orgId = 1,
            name = "user"
        )
        val id4 = Role.RoleId(
            orgId = 2,
            name = "admin"
        )
        
        // Then
        assertEquals(id1, id2)
        assertEquals(id1.hashCode(), id2.hashCode())
        assertNotEquals(id1, id3)
        assertNotEquals(id1.hashCode(), id3.hashCode())
        assertNotEquals(id1, id4)
        assertNotEquals(id1.hashCode(), id4.hashCode())
    }
    
    @Test
    fun `RoleId should correctly implement copy`() {
        // Given
        val id = Role.RoleId(
            orgId = 1,
            name = "admin"
        )
        
        // When
        val copied = id.copy(
            orgId = 2,
            name = "super-admin"
        )
        
        // Then
        assertEquals(2, copied.orgId)
        assertEquals("super-admin", copied.name)
    }
}