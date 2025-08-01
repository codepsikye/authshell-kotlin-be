package io.cpk.be.basic.controller

import io.cpk.be.basic.dto.*
import io.cpk.be.basic.entity.AppUser
import io.cpk.be.basic.repository.AppUserRepository
import io.cpk.be.basic.repository.AppUserRoleRepository
import io.cpk.be.security.CustomUserDetails
import io.cpk.be.security.JwtTokenProvider
import io.mockk.Called
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class AuthControllerTest {

    private val authenticationManager = mockk<AuthenticationManager>()
    private val tokenProvider = mockk<JwtTokenProvider>()
    private val appUserRepository = mockk<AppUserRepository>()
    private val appUserRoleRepository = mockk<AppUserRoleRepository>()
    private val authentication = mockk<Authentication>()

    private lateinit var authController: AuthController
    private lateinit var testUser: AppUser
    
    // Define authorities for testing
    private lateinit var authorities: Collection<SimpleGrantedAuthority>

    @BeforeEach
    fun setUp() {
        authController = AuthController(authenticationManager, tokenProvider, appUserRepository, appUserRoleRepository)

        testUser = AppUser.create(
                id = "testuser",
                orgId = 1,
                username = "testuser",
                email = "test@example.com",
                fullname = "Test User",
                orgAdmin = false
        )
                
        // Initialize authorities with sample permissions
        authorities = listOf(
            SimpleGrantedAuthority("user_read"),
            SimpleGrantedAuthority("user_edit"),
            SimpleGrantedAuthority("center_read")
        )
    }

    @Test
    fun `should authenticate user successfully`() {
        // Given
        val loginRequest = LoginRequest("testuser", "password")
        val accessToken = "access.token.here"
        val refreshToken = "refresh.token.here"
        val expirationDate = Date(System.currentTimeMillis() + 86400000)
        val centerId = 1
        val userDetails = CustomUserDetails("testuser", "password", authorities, 1, centerId, "testuser")
        val authToken = UsernamePasswordAuthenticationToken(userDetails, null, authorities)

        every { authenticationManager.authenticate(any()) } returns authToken
        every { appUserRepository.findByUsername("testuser") } returns Optional.of(testUser)
        every { tokenProvider.generateTokenFromUsername("testuser", 1, centerId) } returns accessToken
        every { tokenProvider.generateRefreshToken("testuser", 1, centerId) } returns refreshToken
        every { tokenProvider.getExpirationDateFromToken(accessToken) } returns expirationDate
        // Mock appUserRoleRepository to return empty list for access rights
        every { appUserRoleRepository.findAccessRightsByUserIdAndCenterId("testuser", centerId) } returns emptyList()

        // When
        val response = authController.authenticateUser(loginRequest)

        // Then
        assertEquals(HttpStatus.OK, response.statusCode)
        assertNotNull(response.body)
        assertEquals(accessToken, response.body!!.accessToken)
        assertEquals(refreshToken, response.body!!.refreshToken)
        assertEquals("Bearer", response.body!!.tokenType)
        assertEquals("testuser", response.body!!.user.id)
        assertEquals("testuser", response.body!!.user.username)
        assertEquals("Test User", response.body!!.user.fullname)
        assertEquals("test@example.com", response.body!!.user.email)
        assertEquals(1, response.body!!.user.orgId)
        assertEquals(false, response.body!!.user.orgAdmin)

        verify { appUserRepository.findByUsername("testuser") }
        verify { tokenProvider.generateTokenFromUsername("testuser", 1, centerId) }
        verify { tokenProvider.generateRefreshToken("testuser", 1, centerId) }
        verify { tokenProvider.getExpirationDateFromToken(accessToken) }
        verify { appUserRoleRepository.findAccessRightsByUserIdAndCenterId("testuser", centerId) }
    }

    @Test
    fun `should return unauthorized when user not found`() {
        // Given
        val loginRequest = LoginRequest("nonexistent", "password")
        every { authenticationManager.authenticate(any()) } throws RuntimeException("Authentication failed")

        // When
        val response = authController.authenticateUser(loginRequest)

        // Then
        assertEquals(HttpStatus.UNAUTHORIZED, response.statusCode)
        verify { authenticationManager.authenticate(any()) }
        verify { tokenProvider wasNot Called }
    }

    @Test
    fun `should refresh token successfully`() {
        // Given
        val refreshRequest = RefreshTokenRequest("valid.refresh.token")
        val newAccessToken = "new.access.token"
        val newRefreshToken = "new.refresh.token"
        val expirationDate = Date(System.currentTimeMillis() + 86400000)

        every { tokenProvider.validateToken("valid.refresh.token") } returns true
        every { tokenProvider.isRefreshToken("valid.refresh.token") } returns true
        every { tokenProvider.getUsernameFromToken("valid.refresh.token") } returns "testuser"
        // Mock getOrgIdFromToken and getCenterIdFromToken
        every { tokenProvider.getOrgIdFromToken("valid.refresh.token") } returns 1
        val centerId = 1
        every { tokenProvider.getCenterIdFromToken("valid.refresh.token") } returns centerId
        every { appUserRepository.findByUsername("testuser") } returns Optional.of(testUser)
        every { tokenProvider.generateTokenFromUsername("testuser", 1, centerId) } returns newAccessToken
        every { tokenProvider.generateRefreshToken("testuser", 1, centerId) } returns newRefreshToken
        every { tokenProvider.getExpirationDateFromToken(newAccessToken) } returns expirationDate
        // Mock appUserRoleRepository to return empty list for access rights
        every { appUserRoleRepository.findAccessRightsByUserIdAndCenterId("testuser", centerId) } returns emptyList()

        // When
        val response = authController.refreshToken(refreshRequest)

        // Then
        assertEquals(HttpStatus.OK, response.statusCode)
        assertNotNull(response.body)
        assertEquals(newAccessToken, response.body!!.accessToken)
        assertEquals(newRefreshToken, response.body!!.refreshToken)

        verify { tokenProvider.validateToken("valid.refresh.token") }
        verify { tokenProvider.isRefreshToken("valid.refresh.token") }
        verify { tokenProvider.getUsernameFromToken("valid.refresh.token") }
        // Verify getOrgIdFromToken and getCenterIdFromToken
        verify { tokenProvider.getOrgIdFromToken("valid.refresh.token") }
        verify { tokenProvider.getCenterIdFromToken("valid.refresh.token") }
        verify { appUserRepository.findByUsername("testuser") }
        verify { tokenProvider.generateTokenFromUsername("testuser", 1, centerId) }
        verify { tokenProvider.generateRefreshToken("testuser", 1, centerId) }
        verify { appUserRoleRepository.findAccessRightsByUserIdAndCenterId("testuser", centerId) }
    }

    @Test
    fun `should return unauthorized for invalid refresh token`() {
        // Given
        val refreshRequest = RefreshTokenRequest("invalid.refresh.token")
        every { tokenProvider.validateToken("invalid.refresh.token") } returns false

        // When
        val response = authController.refreshToken(refreshRequest)

        // Then
        assertEquals(HttpStatus.UNAUTHORIZED, response.statusCode)
        verify { tokenProvider.validateToken("invalid.refresh.token") }
        // Don't verify that isRefreshToken was not called, as the implementation might call it anyway
        verify { appUserRepository wasNot Called }
    }

    @Test
    fun `should return unauthorized for non-refresh token`() {
        // Given
        val refreshRequest = RefreshTokenRequest("access.token.not.refresh")
        every { tokenProvider.validateToken("access.token.not.refresh") } returns true
        every { tokenProvider.isRefreshToken("access.token.not.refresh") } returns false

        // When
        val response = authController.refreshToken(refreshRequest)

        // Then
        assertEquals(HttpStatus.UNAUTHORIZED, response.statusCode)
        verify { tokenProvider.validateToken("access.token.not.refresh") }
        verify { tokenProvider.isRefreshToken("access.token.not.refresh") }
        verify { appUserRepository wasNot Called }
    }

    @Test
    fun `should logout successfully`() {
        // When
        val response = authController.logout()

        // Then
        assertEquals(HttpStatus.OK, response.statusCode)
        assertNotNull(response.body)
        assertEquals("Logout successful", response.body!!["message"])
    }

    @Test
    fun `should get current user successfully`() {
        // Given
        val centerId = 1
        val userDetails = CustomUserDetails("testuser", "password", authorities, 1, centerId, "testuser")
        
        every { authentication.name } returns "testuser"
        every { authentication.principal } returns userDetails
        every { appUserRepository.findByUsername("testuser") } returns Optional.of(testUser)
        every { appUserRoleRepository.findAccessRightsByUserIdAndCenterId("testuser", centerId) } returns emptyList()

        // When
        val response = authController.getCurrentUser(authentication)

        // Then
        assertEquals(HttpStatus.OK, response.statusCode)
        assertNotNull(response.body)
        assertEquals("testuser", response.body!!.id)
        assertEquals("testuser", response.body!!.username)
        assertEquals("Test User", response.body!!.fullname)
        assertEquals(centerId, response.body!!.centerId)

        verify { authentication.name }
        verify { authentication.principal }
        verify { appUserRepository.findByUsername("testuser") }
        verify { appUserRoleRepository.findAccessRightsByUserIdAndCenterId("testuser", centerId) }
    }

    @Test
    fun `should refresh token with centerId from request when token centerId is null`() {
        // Given
        val requestCenterId = 2
        val refreshRequest = RefreshTokenRequest("valid.refresh.token", requestCenterId)
        val newAccessToken = "new.access.token"
        val newRefreshToken = "new.refresh.token"
        val expirationDate = Date(System.currentTimeMillis() + 86400000)

        every { tokenProvider.validateToken("valid.refresh.token") } returns true
        every { tokenProvider.isRefreshToken("valid.refresh.token") } returns true
        every { tokenProvider.getUsernameFromToken("valid.refresh.token") } returns "testuser"
        // Mock getOrgIdFromToken to return a value and getCenterIdFromToken to return null
        every { tokenProvider.getOrgIdFromToken("valid.refresh.token") } returns 1
        every { tokenProvider.getCenterIdFromToken("valid.refresh.token") } returns null
        every { appUserRepository.findByUsername("testuser") } returns Optional.of(testUser)
        // The centerId used should be from the request (2) since the token centerId is null
        every { tokenProvider.generateTokenFromUsername("testuser", 1, requestCenterId) } returns newAccessToken
        every { tokenProvider.generateRefreshToken("testuser", 1, requestCenterId) } returns newRefreshToken
        every { tokenProvider.getExpirationDateFromToken(newAccessToken) } returns expirationDate
        // Mock appUserRoleRepository to return empty list for access rights
        every { appUserRoleRepository.findAccessRightsByUserIdAndCenterId("testuser", requestCenterId) } returns emptyList()

        // When
        val response = authController.refreshToken(refreshRequest)

        // Then
        assertEquals(HttpStatus.OK, response.statusCode)
        assertNotNull(response.body)
        assertEquals(newAccessToken, response.body!!.accessToken)
        assertEquals(newRefreshToken, response.body!!.refreshToken)

        verify { tokenProvider.validateToken("valid.refresh.token") }
        verify { tokenProvider.isRefreshToken("valid.refresh.token") }
        verify { tokenProvider.getUsernameFromToken("valid.refresh.token") }
        verify { tokenProvider.getOrgIdFromToken("valid.refresh.token") }
        verify { tokenProvider.getCenterIdFromToken("valid.refresh.token") }
        verify { appUserRepository.findByUsername("testuser") }
        // Verify that the centerId from the request is used
        verify { tokenProvider.generateTokenFromUsername("testuser", 1, requestCenterId) }
        verify { tokenProvider.generateRefreshToken("testuser", 1, requestCenterId) }
        verify { appUserRoleRepository.findAccessRightsByUserIdAndCenterId("testuser", requestCenterId) }
    }

    @Test
    fun `should return unauthorized when getting current user fails`() {
        // Given
        every { authentication.name } returns "testuser"
        every { appUserRepository.findByUsername("testuser") } returns Optional.empty()

        // When
        val response = authController.getCurrentUser(authentication)

        // Then
        assertEquals(HttpStatus.UNAUTHORIZED, response.statusCode)
        verify { authentication.name }
        verify { appUserRepository.findByUsername("testuser") }
    }
    
    @Test
    fun `should process password reset request successfully`() {
        // Given
        val request = PasswordResetRequest("test@example.com")
        
        // When
        val response = authController.requestPasswordReset(request)
        
        // Then
        assertEquals(HttpStatus.OK, response.statusCode)
        assertNotNull(response.body)
        assertEquals("Password reset request sent to email", response.body!!["message"])
    }
    
    @Test
    fun `should process password reset successfully`() {
        // Given
        val request = PasswordResetConfirm("reset-token", "newPassword")
        
        // When
        val response = authController.resetPassword(request)
        
        // Then
        assertEquals(HttpStatus.OK, response.statusCode)
        assertNotNull(response.body)
        assertEquals("Password reset successful", response.body!!["message"])
    }
    
    @Test
    fun `should set center successfully`() {
        // Given
        val centerId = 2
        val request = SetCenterRequest(centerId)
        val accessToken = "new.access.token"
        val refreshToken = "new.refresh.token"
        val expirationDate = Date(System.currentTimeMillis() + 86400000)
        val userDetails = CustomUserDetails("testuser", "password", authorities, 1, null, "testuser")
        
        every { authentication.name } returns "testuser"
        every { authentication.principal } returns userDetails
        every { appUserRoleRepository.findCenterIdsByUserId("testuser") } returns listOf(1, 2, 3)
        every { tokenProvider.generateTokenFromUsername("testuser", 1, centerId) } returns accessToken
        every { tokenProvider.generateRefreshToken("testuser", 1, centerId) } returns refreshToken
        every { tokenProvider.getExpirationDateFromToken(accessToken) } returns expirationDate
        every { appUserRepository.findByUsername("testuser") } returns Optional.of(testUser)
        every { appUserRoleRepository.findAccessRightsByUserIdAndCenterId("testuser", centerId) } returns emptyList()
        
        // When
        val response = authController.setCenter(request, authentication)
        
        // Then
        assertEquals(HttpStatus.OK, response.statusCode)
        assertNotNull(response.body)
        assertEquals(accessToken, response.body!!.accessToken)
        assertEquals(refreshToken, response.body!!.refreshToken)
        assertEquals(centerId, response.body!!.user.centerId)
        
        verify { authentication.name }
        verify { authentication.principal }
        verify { appUserRoleRepository.findCenterIdsByUserId("testuser") }
        verify { tokenProvider.generateTokenFromUsername("testuser", 1, centerId) }
        verify { tokenProvider.generateRefreshToken("testuser", 1, centerId) }
        verify { tokenProvider.getExpirationDateFromToken(accessToken) }
        verify { appUserRepository.findByUsername("testuser") }
        verify { appUserRoleRepository.findAccessRightsByUserIdAndCenterId("testuser", centerId) }
    }
    
    @Test
    fun `should return bad request when center id is invalid`() {
        // Given
        val invalidCenterId = 999
        val request = SetCenterRequest(invalidCenterId)
        val userDetails = CustomUserDetails("testuser", "password", authorities, 1, null, "testuser")
        
        every { authentication.name } returns "testuser"
        every { authentication.principal } returns userDetails
        every { appUserRoleRepository.findCenterIdsByUserId("testuser") } returns listOf(1, 2, 3)
        
        // When
        val response = authController.setCenter(request, authentication)
        
        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
        
        verify { authentication.name }
        verify { authentication.principal }
        verify { appUserRoleRepository.findCenterIdsByUserId("testuser") }
        verify(exactly = 0) { tokenProvider.generateTokenFromUsername(any(), any(), any()) }
    }
    
    @Test
    fun `should return unauthorized when set center fails`() {
        // Given
        val centerId = 2
        val request = SetCenterRequest(centerId)
        
        every { authentication.name } returns "testuser"
        // Simulate an exception when casting the principal to CustomUserDetails
        every { authentication.principal } throws RuntimeException("Authentication error")
        
        // When
        val response = authController.setCenter(request, authentication)
        
        // Then
        assertEquals(HttpStatus.UNAUTHORIZED, response.statusCode)
        
        verify { authentication.name }
        verify { authentication.principal }
    }
    @Test
    fun `should handle specific exception in authenticateUser`() {
        // Given
        val loginRequest = LoginRequest("testuser", "password")
        
        // Mock authentication to throw a specific exception
        every { authenticationManager.authenticate(any()) } throws RuntimeException("Authentication failed")
        
        // When
        val response = authController.authenticateUser(loginRequest)
        
        // Then
        assertEquals(HttpStatus.UNAUTHORIZED, response.statusCode)
        verify { authenticationManager.authenticate(any()) }
    }
    
    @Test
    fun `should handle refreshToken with non-null centerId from token`() {
        // Given
        val refreshRequest = RefreshTokenRequest("valid.refresh.token")
        val newAccessToken = "new.access.token"
        val newRefreshToken = "new.refresh.token"
        val expirationDate = Date(System.currentTimeMillis() + 86400000)
        val tokenCenterId = 3
        
        every { tokenProvider.validateToken("valid.refresh.token") } returns true
        every { tokenProvider.isRefreshToken("valid.refresh.token") } returns true
        every { tokenProvider.getUsernameFromToken("valid.refresh.token") } returns "testuser"
        every { tokenProvider.getOrgIdFromToken("valid.refresh.token") } returns 1
        every { tokenProvider.getCenterIdFromToken("valid.refresh.token") } returns tokenCenterId
        every { appUserRepository.findByUsername("testuser") } returns Optional.of(testUser)
        every { tokenProvider.generateTokenFromUsername("testuser", 1, tokenCenterId) } returns newAccessToken
        every { tokenProvider.generateRefreshToken("testuser", 1, tokenCenterId) } returns newRefreshToken
        every { tokenProvider.getExpirationDateFromToken(newAccessToken) } returns expirationDate
        every { appUserRoleRepository.findAccessRightsByUserIdAndCenterId("testuser", tokenCenterId) } returns emptyList()
        
        // When
        val response = authController.refreshToken(refreshRequest)
        
        // Then
        assertEquals(HttpStatus.OK, response.statusCode)
        assertNotNull(response.body)
        assertEquals(newAccessToken, response.body!!.accessToken)
        assertEquals(newRefreshToken, response.body!!.refreshToken)
        assertEquals(tokenCenterId, response.body!!.user.centerId)
        
        verify { tokenProvider.validateToken("valid.refresh.token") }
        verify { tokenProvider.isRefreshToken("valid.refresh.token") }
        verify { tokenProvider.getUsernameFromToken("valid.refresh.token") }
        verify { tokenProvider.getOrgIdFromToken("valid.refresh.token") }
        verify { tokenProvider.getCenterIdFromToken("valid.refresh.token") }
        verify { appUserRepository.findByUsername("testuser") }
        verify { tokenProvider.generateTokenFromUsername("testuser", 1, tokenCenterId) }
        verify { tokenProvider.generateRefreshToken("testuser", 1, tokenCenterId) }
        verify { appUserRoleRepository.findAccessRightsByUserIdAndCenterId("testuser", tokenCenterId) }
    }
    
    @Test
    fun `should handle getCurrentUser when principal is not CustomUserDetails`() {
        // Given
        every { authentication.name } returns "testuser"
        every { authentication.principal } returns "not-a-custom-user-details"
        every { appUserRepository.findByUsername("testuser") } returns Optional.of(testUser)
        
        // When
        val response = authController.getCurrentUser(authentication)
        
        // Then
        assertEquals(HttpStatus.OK, response.statusCode)
        assertNotNull(response.body)
        assertEquals("testuser", response.body!!.id)
        assertEquals("testuser", response.body!!.username)
        assertEquals("Test User", response.body!!.fullname)
        assertNotNull(response.body)
        assertEquals(null, response.body!!.centerId)
        assertEquals(emptyList<String>(), response.body!!.accessRight)
        
        verify { authentication.name }
        verify { authentication.principal }
        verify { appUserRepository.findByUsername("testuser") }
        verify(exactly = 0) { appUserRoleRepository.findAccessRightsByUserIdAndCenterId(any(), any()) }
    }
    
    @Test
    fun `should handle specific exception in refreshToken`() {
        // Given
        val refreshRequest = RefreshTokenRequest("valid.refresh.token")
        
        // Mock token validation to throw an exception
        every { tokenProvider.validateToken("valid.refresh.token") } throws RuntimeException("Token validation error")
        
        // When
        val response = authController.refreshToken(refreshRequest)
        
        // Then
        assertEquals(HttpStatus.UNAUTHORIZED, response.statusCode)
        verify { tokenProvider.validateToken("valid.refresh.token") }
    }
}