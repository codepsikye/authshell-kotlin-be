package io.cpk.be.security

import io.cpk.be.basic.entity.AppUser
import io.cpk.be.basic.repository.AppUserRepository
import io.cpk.be.basic.repository.AppUserRoleRepository
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.test.util.ReflectionTestUtils
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class JwtTokenProviderTest {

    private lateinit var jwtTokenProvider: JwtTokenProvider
    private lateinit var mockAppUserRepository: AppUserRepository
    private lateinit var mockAppUserRoleRepository: AppUserRoleRepository
    private lateinit var testUser: AppUser

    @BeforeEach
    fun setUp() {
        mockAppUserRepository = mockk<AppUserRepository>(relaxed = true)
        mockAppUserRoleRepository = mockk<AppUserRoleRepository>(relaxed = true)

        // Create a test AppUser with orgId
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

        // Set test values using reflection - need at least 256 bits (32 chars) for HS512
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
    fun `should generate token from username`() {
        val username = "testuser"

        val token = jwtTokenProvider.generateTokenFromUsername(username)

        assertNotNull(token)
        assertTrue(token.isNotEmpty())
    }

    @Test
    fun `should generate token from authentication`() {
        val username = "testuser"
        val authorities = emptyList<SimpleGrantedAuthority>()
        // Use CustomUserDetails instead of User
        val userDetails = CustomUserDetails(username, "password", authorities, 1, 1, 1)
        val authentication = UsernamePasswordAuthenticationToken(userDetails, null, authorities)

        val token = jwtTokenProvider.generateToken(authentication)

        assertNotNull(token)
        assertTrue(token.isNotEmpty())
    }

    @Test
    fun `should generate refresh token`() {
        val username = "testuser"

        val refreshToken = jwtTokenProvider.generateRefreshToken(username)

        assertNotNull(refreshToken)
        assertTrue(refreshToken.isNotEmpty())
        assertTrue(jwtTokenProvider.isRefreshToken(refreshToken))
    }

    @Test
    fun `should extract username from token`() {
        val username = "testuser"
        val token = jwtTokenProvider.generateTokenFromUsername(username)

        val extractedUsername = jwtTokenProvider.getUsernameFromToken(token)

        assertEquals(username, extractedUsername)
    }

    @Test
    fun `should validate valid token`() {
        val username = "testuser"
        val token = jwtTokenProvider.generateTokenFromUsername(username)

        val isValid = jwtTokenProvider.validateToken(token)

        assertTrue(isValid)
    }

    @Test
    fun `should reject invalid token`() {
        val invalidToken = "invalid.token.here"

        val isValid = jwtTokenProvider.validateToken(invalidToken)

        assertFalse(isValid)
    }

    @Test
    fun `should reject empty token`() {
        val isValid = jwtTokenProvider.validateToken("")

        assertFalse(isValid)
    }

    @Test
    fun `should identify access token correctly`() {
        val username = "testuser"
        val accessToken = jwtTokenProvider.generateTokenFromUsername(username)

        assertFalse(jwtTokenProvider.isRefreshToken(accessToken))
    }

    @Test
    fun `should identify refresh token correctly`() {
        val username = "testuser"
        val refreshToken = jwtTokenProvider.generateRefreshToken(username)

        assertTrue(jwtTokenProvider.isRefreshToken(refreshToken))
    }

    @Test
    fun `should get expiration date from token`() {
        val username = "testuser"
        val token = jwtTokenProvider.generateTokenFromUsername(username)

        val expirationDate = jwtTokenProvider.getExpirationDateFromToken(token)

        assertNotNull(expirationDate)
        assertTrue(expirationDate.time > System.currentTimeMillis())
    }

    @Test
    fun `should return null expiration for invalid token`() {
        val invalidToken = "invalid.token.here"

        val expirationDate = jwtTokenProvider.getExpirationDateFromToken(invalidToken)

        assertEquals(null, expirationDate)
    }
}