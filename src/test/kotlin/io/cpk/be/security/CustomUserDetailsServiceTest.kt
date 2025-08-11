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

class CustomUserDetailsServiceTest {

    private val appUserRepository = mockk<AppUserRepository>()
    private val appUserRoleRepository = mockk<AppUserRoleRepository>(relaxed = true)

    private lateinit var customUserDetailsService: CustomUserDetailsService
    private lateinit var testUser: AppUser

    @BeforeEach
    fun setUp() {
        customUserDetailsService = CustomUserDetailsService(appUserRepository, appUserRoleRepository)
        
        every { appUserRoleRepository.hasUniqueCenterId(1) } returns false
        every { appUserRoleRepository.getUniqueCenterId(1) } returns null

        testUser = AppUser.create(
            id = 1,
            orgId = 1,
            username = "testuser",
            email = "test@example.com",
            fullname = "Test User",
            orgAdmin = false
        )
    }

    @Test
    fun `should load user by username successfully`() {
        // Given
        every { appUserRepository.findByUsername("testuser") } returns Optional.of(testUser)

        // When
        val userDetails = customUserDetailsService.loadUserByUsername("testuser")

        // Then
        assertEquals("testuser", userDetails.username)
        assertTrue(userDetails.isAccountNonExpired)
        assertTrue(userDetails.isAccountNonLocked)
        assertTrue(userDetails.isCredentialsNonExpired)
        assertTrue(userDetails.isEnabled)

        verify { appUserRepository.findByUsername("testuser") }
    }

    @Test
    fun `should load admin user with admin role`() {
        // Given
        val adminUser = testUser.copy(orgAdmin = true)
        every { appUserRepository.findByUsername("testuser") } returns Optional.of(adminUser)

        // When
        val userDetails = customUserDetailsService.loadUserByUsername("testuser")

        // Then
        assertEquals("testuser", userDetails.username)

        verify { appUserRepository.findByUsername("testuser") }
    }

    @Test
    fun `should throw UsernameNotFoundException when user not found`() {
        // Given
        every { appUserRepository.findByUsername("nonexistent") } returns Optional.empty()

        // When & Then
        val exception = assertThrows<UsernameNotFoundException> {
            customUserDetailsService.loadUserByUsername("nonexistent")
        }

        assertEquals("User not found with username: nonexistent", exception.message)
        verify { appUserRepository.findByUsername("nonexistent") }
    }
}