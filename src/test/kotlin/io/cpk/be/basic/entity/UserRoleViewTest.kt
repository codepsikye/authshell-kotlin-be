package io.cpk.be.basic.entity

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class UserRoleViewTest {

    @Test
    fun `should create UserRoleView with required parameters`() {
        // Given
        val orgId = 1
        val centerId = 2
        val roleName = "admin"
        val fullname = "Test User"
        val orgName = "Test Organization"
        val centerName = "Test Center"
        
        // When
        val userRoleView = UserRoleView(
            orgId = orgId,
            centerId = centerId,
            roleName = roleName,
            fullname = fullname,
            orgName = orgName,
            centerName = centerName
        )
        
        // Then
        assertEquals(orgId, userRoleView.orgId)
        assertEquals(centerId, userRoleView.centerId)
        assertEquals(roleName, userRoleView.roleName)
        assertEquals(fullname, userRoleView.fullname)
        assertEquals(orgName, userRoleView.orgName)
        assertEquals(centerName, userRoleView.centerName)
        assertEquals(emptyList<String>(), userRoleView.accessRight)
    }
    
    @Test
    fun `should create UserRoleView with all parameters`() {
        // Given
        val orgId = 1
        val centerId = 2
        val roleName = "admin"
        val fullname = "Test User"
        val orgName = "Test Organization"
        val centerName = "Test Center"
        val accessRight = listOf("read", "write", "delete")
        
        // When
        val userRoleView = UserRoleView(
            orgId = orgId,
            centerId = centerId,
            roleName = roleName,
            fullname = fullname,
            orgName = orgName,
            centerName = centerName,
            accessRight = accessRight
        )
        
        // Then
        assertEquals(orgId, userRoleView.orgId)
        assertEquals(centerId, userRoleView.centerId)
        assertEquals(roleName, userRoleView.roleName)
        assertEquals(fullname, userRoleView.fullname)
        assertEquals(orgName, userRoleView.orgName)
        assertEquals(centerName, userRoleView.centerName)
        assertEquals(accessRight, userRoleView.accessRight)
    }
    
    @Test
    fun `should correctly implement equals and hashCode`() {
        // Given
        val userRoleView1 = UserRoleView(
            orgId = 1,
            centerId = 2,
            roleName = "admin",
            fullname = "Test User",
            orgName = "Test Organization",
            centerName = "Test Center",
            accessRight = listOf("read", "write")
        )
        val userRoleView2 = UserRoleView(
            orgId = 1,
            centerId = 2,
            roleName = "admin",
            fullname = "Test User",
            orgName = "Test Organization",
            centerName = "Test Center",
            accessRight = listOf("read", "write")
        )
        val userRoleView3 = UserRoleView(
            orgId = 1,
            centerId = 2,
            roleName = "user",
            fullname = "Test User",
            orgName = "Test Organization",
            centerName = "Test Center",
            accessRight = listOf("read")
        )
        
        // Then
        assertEquals(userRoleView1, userRoleView2)
        assertEquals(userRoleView1.hashCode(), userRoleView2.hashCode())
        assertNotEquals(userRoleView1, userRoleView3)
        assertNotEquals(userRoleView1.hashCode(), userRoleView3.hashCode())
    }
    
    @Test
    fun `should correctly implement copy`() {
        // Given
        val userRoleView = UserRoleView(
            orgId = 1,
            centerId = 2,
            roleName = "admin",
            fullname = "Test User",
            orgName = "Test Organization",
            centerName = "Test Center",
            accessRight = listOf("read", "write")
        )
        
        // When
        val copied = userRoleView.copy(
            roleName = "super-admin",
            fullname = "New User",
            accessRight = listOf("read", "write", "delete", "admin")
        )
        
        // Then
        assertEquals(1, copied.orgId)
        assertEquals(2, copied.centerId)
        assertEquals("super-admin", copied.roleName)
        assertEquals("New User", copied.fullname)
        assertEquals("Test Organization", copied.orgName)
        assertEquals("Test Center", copied.centerName)
        assertEquals(listOf("read", "write", "delete", "admin"), copied.accessRight)
    }
    
    @Test
    fun `should create UserRoleViewId with default values`() {
        // When
        val id = UserRoleView.UserRoleViewId()
        
        // Then
        assertEquals(0, id.orgId)
        assertEquals(0, id.centerId)
        assertEquals("", id.roleName)
    }
    
    @Test
    fun `should create UserRoleViewId with specified values`() {
        // Given
        val orgId = 1
        val centerId = 2
        val roleName = "admin"
        
        // When
        val id = UserRoleView.UserRoleViewId(
            orgId = orgId,
            centerId = centerId,
            roleName = roleName
        )
        
        // Then
        assertEquals(orgId, id.orgId)
        assertEquals(centerId, id.centerId)
        assertEquals(roleName, id.roleName)
    }
    
    @Test
    fun `UserRoleViewId should correctly implement equals and hashCode`() {
        // Given
        val id1 = UserRoleView.UserRoleViewId(
            orgId = 1,
            centerId = 2,
            roleName = "admin"
        )
        val id2 = UserRoleView.UserRoleViewId(
            orgId = 1,
            centerId = 2,
            roleName = "admin"
        )
        val id3 = UserRoleView.UserRoleViewId(
            orgId = 1,
            centerId = 2,
            roleName = "user"
        )
        
        // Then
        assertEquals(id1, id2)
        assertEquals(id1.hashCode(), id2.hashCode())
        assertNotEquals(id1, id3)
        assertNotEquals(id1.hashCode(), id3.hashCode())
    }
    
    @Test
    fun `UserRoleViewId should correctly implement copy`() {
        // Given
        val id = UserRoleView.UserRoleViewId(
            orgId = 1,
            centerId = 2,
            roleName = "admin"
        )
        
        // When
        val copied = id.copy(
            orgId = 3,
            centerId = 4,
            roleName = "super-admin"
        )
        
        // Then
        assertEquals(3, copied.orgId)
        assertEquals(4, copied.centerId)
        assertEquals("super-admin", copied.roleName)
    }
}