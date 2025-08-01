package io.cpk.be.security

import io.cpk.be.basic.entity.AppUser
import io.cpk.be.basic.repository.AppUserRepository
import io.cpk.be.basic.repository.AppUserRoleRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.security.core.userdetails.UsernameNotFoundException
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Additional tests for CustomUserDetailsService to achieve 100% coverage
 */
class CustomUserDetailsServiceAdditionalTest {

    private val appUserRepository = mockk<AppUserRepository>()
    private val appUserRoleRepository = mockk<AppUserRoleRepository>(relaxed = true)

    private lateinit var customUserDetailsService: CustomUserDetailsService
    private lateinit var testUser: AppUser

    @BeforeEach
    fun setUp() {
        customUserDetailsService = CustomUserDetailsService(appUserRepository, appUserRoleRepository)
        
        testUser = AppUser.create(
            id = "testuser",
            orgId = 1,
            password = "password",
            username = "testuser",
            email = "test@example.com",
            fullname = "Test User",
            orgAdmin = false
        )
    }

    @Test
    fun `should throw UsernameNotFoundException with correct message when user not found with centerId`() {
        // Given
        val username = "testuser"
        val centerId = 123
        every { appUserRepository.findByUsername(username) } returns Optional.of(testUser)
        every { appUserRoleRepository.findCenterIdsByUserId(testUser.id) } returns listOf(456) // Different centerId

        // When & Then
        val exception = assertThrows<UsernameNotFoundException> {
            customUserDetailsService.loadUserByUsername(username, centerId)
        }

        // Verify the exact exception message
        assertEquals("User not found with username: testuser and centerId: 123", exception.message)
        verify { appUserRepository.findByUsername(username) }
        verify { appUserRoleRepository.findCenterIdsByUserId(testUser.id) }
    }
    
    @Test
    fun `should load user with access rights when centerId is valid`() {
        // Given
        val username = "testuser"
        val centerId = 123
        val accessRights = listOf("READ", "WRITE")
        
        every { appUserRepository.findByUsername(username) } returns Optional.of(testUser)
        every { appUserRoleRepository.findCenterIdsByUserId(testUser.id) } returns listOf(123) // Valid centerId
        every { appUserRoleRepository.findAccessRightsByUserIdAndCenterId(testUser.id, centerId) } returns listOf(accessRights)
        
        // When
        val userDetails = customUserDetailsService.loadUserByUsername(username, centerId) as CustomUserDetails
        
        // Then
        assertEquals(username, userDetails.username)
        assertEquals(centerId, userDetails.centerId)
        assertEquals(testUser.id, userDetails.id)
        assertEquals(testUser.orgId, userDetails.orgId)
        assertEquals(2, userDetails.authorities.size)
        assertTrue(userDetails.authorities.any { it.authority == "READ" })
        assertTrue(userDetails.authorities.any { it.authority == "WRITE" })
        
        verify { appUserRepository.findByUsername(username) }
        verify { appUserRoleRepository.findCenterIdsByUserId(testUser.id) }
        verify { appUserRoleRepository.findAccessRightsByUserIdAndCenterId(testUser.id, centerId) }
    }
    
    @Test
    fun `should throw UsernameNotFoundException when user not found`() {
        // Given
        val username = "nonexistent"
        every { appUserRepository.findByUsername(username) } returns Optional.empty()
        
        // When & Then
        val exception = assertThrows<UsernameNotFoundException> {
            customUserDetailsService.loadUserByUsername(username, 123)
        }
        
        assertEquals("User not found with username: nonexistent", exception.message)
        verify { appUserRepository.findByUsername(username) }
    }
    
    @Test
    fun `should load user without centerId`() {
        // Given
        val username = "testuser"
        every { appUserRepository.findByUsername(username) } returns Optional.of(testUser)
        
        // When
        val userDetails = customUserDetailsService.loadUserByUsername(username) as CustomUserDetails
        
        // Then
        assertEquals(username, userDetails.username)
        assertEquals(null, userDetails.centerId)
        assertEquals(testUser.id, userDetails.id)
        assertEquals(testUser.orgId, userDetails.orgId)
        assertEquals(0, userDetails.authorities.size)
        
        verify { appUserRepository.findByUsername(username) }
    }
    
    @Test
    fun `should load user with unique centerId`() {
        // Given
        val username = "testuser"
        val centerId = 123
        every { appUserRepository.findByUsername(username) } returns Optional.of(testUser)
        every { appUserRoleRepository.hasUniqueCenterId(testUser.id) } returns true
        every { appUserRoleRepository.getUniqueCenterId(testUser.id) } returns centerId
        every { appUserRoleRepository.findCenterIdsByUserId(testUser.id) } returns listOf(centerId)
        every { appUserRoleRepository.findAccessRightsByUserIdAndCenterId(testUser.id, centerId) } returns listOf(listOf("READ"))
        
        // When
        val userDetails = customUserDetailsService.loadUserByUsername(username) as CustomUserDetails
        
        // Then
        assertEquals(username, userDetails.username)
        assertEquals(centerId, userDetails.centerId)
        assertEquals(testUser.id, userDetails.id)
        assertEquals(testUser.orgId, userDetails.orgId)
        assertEquals(1, userDetails.authorities.size)
        assertTrue(userDetails.authorities.any { it.authority == "READ" })
        
        verify { appUserRepository.findByUsername(username) }
        verify { appUserRoleRepository.hasUniqueCenterId(testUser.id) }
        verify { appUserRoleRepository.getUniqueCenterId(testUser.id) }
        verify { appUserRoleRepository.findCenterIdsByUserId(testUser.id) }
        verify { appUserRoleRepository.findAccessRightsByUserIdAndCenterId(testUser.id, centerId) }
    }
    
    @Test
    fun `should load user with explicitly null centerId`() {
        // Given
        val username = "testuser"
        every { appUserRepository.findByUsername(username) } returns Optional.of(testUser)
        
        // When
        val userDetails = customUserDetailsService.loadUserByUsername(username, null) as CustomUserDetails
        
        // Then
        assertEquals(username, userDetails.username)
        assertEquals(null, userDetails.centerId)
        assertEquals(testUser.id, userDetails.id)
        assertEquals(testUser.orgId, userDetails.orgId)
        assertEquals(0, userDetails.authorities.size)
        
        verify { appUserRepository.findByUsername(username) }
        // Verify that no center-related methods are called
        verify(exactly = 0) { appUserRoleRepository.findCenterIdsByUserId(any()) }
        verify(exactly = 0) { appUserRoleRepository.findAccessRightsByUserIdAndCenterId(any(), any()) }
    }
}