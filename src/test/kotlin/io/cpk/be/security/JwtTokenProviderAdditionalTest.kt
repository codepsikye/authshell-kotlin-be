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
import org.springframework.test.util.ReflectionTestUtils
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull

/**
 * Additional tests for JwtTokenProvider to achieve 100% coverage
 */
class JwtTokenProviderAdditionalTest {

    private lateinit var jwtTokenProvider: JwtTokenProvider
    private lateinit var mockAppUserRepository: AppUserRepository
    private lateinit var mockAppUserRoleRepository: AppUserRoleRepository
    private lateinit var testUser: AppUser

    @BeforeEach
    fun setUp() {
        // Create mock repositories
        mockAppUserRepository = mockk<AppUserRepository>(relaxed = true)
        mockAppUserRoleRepository = mockk<AppUserRoleRepository>(relaxed = true)

        // Create a test AppUser
        testUser = AppUser.create(
            id = 1,
            orgId = 1,
            username = "testuser",
            fullname = "Test User",
            email = "test@example.com",
            orgAdmin = false
        )

        // Set up the mock to return the test user when findById is called with "testuser"
        every { mockAppUserRepository.findByUsername("testuser") } returns Optional.of(testUser)
        
        // Set up the mock behavior for appUserRoleRepository
        every { mockAppUserRoleRepository.hasUniqueCenterId(1) } returns false
        every { mockAppUserRoleRepository.getUniqueCenterId(1) } returns null

        // Initialize JwtTokenProvider with the mock repositories
        jwtTokenProvider = JwtTokenProvider(mockAppUserRepository, mockAppUserRoleRepository)

        // Set test values using reflection
        ReflectionTestUtils.setField(
            jwtTokenProvider,
            "jwtSecret",
            "myTestSecretKeyThatIsLongEnoughForHS512SignatureAlgorithm123456789"
        )
        ReflectionTestUtils.setField(jwtTokenProvider, "jwtExpirationInMs", 86400000L) // 24 hours
        ReflectionTestUtils.setField(
            jwtTokenProvider,
            "jwtRefreshExpirationInMs",
            604800000L
        ) // 7 days
    }

    @Test
    fun `should throw RuntimeException when user not found in generateTokenFromUsername`() {
        // Given
        val nonExistentUsername = "nonexistent"
        every { mockAppUserRepository.findByUsername(nonExistentUsername) } returns Optional.empty()

        // When & Then
        val exception = assertThrows<RuntimeException> {
            jwtTokenProvider.generateTokenFromUsername(nonExistentUsername)
        }

        assertEquals("User not found with username: nonexistent", exception.message)
        verify { mockAppUserRepository.findByUsername(nonExistentUsername) }
    }

    @Test
    fun `should throw RuntimeException when user not found in generateRefreshToken`() {
        // Given
        val nonExistentUsername = "nonexistent"
        every { mockAppUserRepository.findByUsername(nonExistentUsername) } returns Optional.empty()

        // When & Then
        val exception = assertThrows<RuntimeException> {
            jwtTokenProvider.generateRefreshToken(nonExistentUsername)
        }

        assertEquals("User not found with username: nonexistent", exception.message)
        verify { mockAppUserRepository.findByUsername(nonExistentUsername) }
    }

    @Test
    fun `should use unique centerId from repository when available`() {
        // Given
        val username = "testuser"
        val uniqueCenterId = 42
        every { mockAppUserRoleRepository.hasUniqueCenterId(1) } returns true
        every { mockAppUserRoleRepository.getUniqueCenterId(1) } returns uniqueCenterId

        // When
        val token = jwtTokenProvider.generateTokenFromUsername(username)
        
        // Then
        assertNotNull(token)
        assertEquals(uniqueCenterId, jwtTokenProvider.getCenterIdFromToken(token))
        verify { mockAppUserRoleRepository.hasUniqueCenterId(1) }
        verify { mockAppUserRoleRepository.getUniqueCenterId(1) }
    }

    @Test
    fun `should use unique centerId from repository in refresh token when available`() {
        // Given
        val username = "testuser"
        val uniqueCenterId = 42
        every { mockAppUserRoleRepository.hasUniqueCenterId(1) } returns true
        every { mockAppUserRoleRepository.getUniqueCenterId(1) } returns uniqueCenterId

        // When
        val token = jwtTokenProvider.generateRefreshToken(username)
        
        // Then
        assertNotNull(token)
        assertEquals(uniqueCenterId, jwtTokenProvider.getCenterIdFromToken(token))
        verify { mockAppUserRoleRepository.hasUniqueCenterId(1) }
        verify { mockAppUserRoleRepository.getUniqueCenterId(1) }
    }

    @Test
    fun `should use unique centerId from repository in generateTokenFromUsername with orgId`() {
        // Given
        val username = "testuser"
        val orgId = 2
        val uniqueCenterId = 42
        every { mockAppUserRoleRepository.hasUniqueCenterId(1) } returns true
        every { mockAppUserRoleRepository.getUniqueCenterId(1) } returns uniqueCenterId

        // When
        val token = jwtTokenProvider.generateTokenFromUsername(username, orgId)
        
        // Then
        assertNotNull(token)
        assertEquals(uniqueCenterId, jwtTokenProvider.getCenterIdFromToken(token))
        verify { mockAppUserRoleRepository.hasUniqueCenterId(1) }
        verify { mockAppUserRoleRepository.getUniqueCenterId(1) }
    }
    
    @Test
    fun `should use unique centerId from repository in generateRefreshToken with orgId`() {
        // Given
        val username = "testuser"
        val orgId = 2
        val uniqueCenterId = 42
        every { mockAppUserRoleRepository.hasUniqueCenterId(1) } returns true
        every { mockAppUserRoleRepository.getUniqueCenterId(1) } returns uniqueCenterId

        // When
        val token = jwtTokenProvider.generateRefreshToken(username, orgId)
        
        // Then
        assertNotNull(token)
        assertEquals(uniqueCenterId, jwtTokenProvider.getCenterIdFromToken(token))
        verify { mockAppUserRoleRepository.hasUniqueCenterId(1) }
        verify { mockAppUserRoleRepository.getUniqueCenterId(1) }
    }
    
    @Test
    fun `should handle exception in isRefreshToken`() {
        // Given
        val invalidToken = "invalid.token"
        
        // When
        val isRefresh = jwtTokenProvider.isRefreshToken(invalidToken)
        
        // Then
        assertFalse(isRefresh)
    }
    
    @Test
    fun `should extract orgId from token`() {
        // Given
        val username = "testuser"
        val orgId = 42
        val token = jwtTokenProvider.generateTokenFromUsername(username, orgId)
        
        // When
        val extractedOrgId = jwtTokenProvider.getOrgIdFromToken(token)
        
        // Then
        assertEquals(orgId, extractedOrgId)
    }
    
    @Test
    fun `should handle various exceptions in validateToken`() {
        // Since we can't easily trigger specific exceptions in validateToken,
        // we'll just test that it returns false for invalid tokens
        
        // Test with empty string (IllegalArgumentException)
        assertFalse(jwtTokenProvider.validateToken(""))
        
        // Test with malformed token (MalformedJwtException)
        assertFalse(jwtTokenProvider.validateToken("not.a.jwt.token"))
    }
    
    @Test
    fun `should return null for centerId when not present in token`() {
        // Given
        val username = "testuser"
        val orgId = 42
        // Create a token without centerId
        val token = jwtTokenProvider.generateTokenFromUsername(username, orgId)
        
        // When
        val centerId = jwtTokenProvider.getCenterIdFromToken(token)
        
        // Then
        assertEquals(null, centerId)
    }
}