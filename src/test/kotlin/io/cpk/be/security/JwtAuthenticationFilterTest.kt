package io.cpk.be.security

import io.mockk.*
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import java.io.IOException

class JwtAuthenticationFilterTest {

    private val tokenProvider = mockk<JwtTokenProvider>(relaxed = true)
    private val userDetailsService = mockk<CustomUserDetailsService>(relaxed = true)
    private val filterChain = mockk<FilterChain>(relaxed = true)
    
    private lateinit var jwtAuthenticationFilter: JwtAuthenticationFilter
    private lateinit var request: MockHttpServletRequest
    private lateinit var response: MockHttpServletResponse
    
    @BeforeEach
    fun setUp() {
        jwtAuthenticationFilter = JwtAuthenticationFilter(tokenProvider, userDetailsService)
        SecurityContextHolder.clearContext() // Clear security context before each test
        
        // Use Spring's mock implementations instead of MockK for request and response
        request = MockHttpServletRequest()
        response = MockHttpServletResponse()
        
        // Common mock behavior
        every { filterChain.doFilter(any(), any()) } just runs
    }
    
    @Test
    @Throws(ServletException::class, IOException::class)
    fun `should authenticate user when valid token with centerId is provided`() {
        // Given
        val token = "valid.jwt.token"
        val username = "testuser"
        val centerId = 123
        val authorities = listOf(SimpleGrantedAuthority("READ"), SimpleGrantedAuthority("WRITE"))
        val userDetails = CustomUserDetails(
            username = username,
            password = "password",
            authorities = authorities,
            orgId = 1,
            centerId = centerId
        )
        
        // Set Authorization header with Bearer token
        request.addHeader("Authorization", "Bearer $token")
        
        // Mock token provider behavior
        every { tokenProvider.validateToken(token) } returns true
        every { tokenProvider.getUsernameFromToken(token) } returns username
        every { tokenProvider.getCenterIdFromToken(token) } returns centerId
        
        // Mock user details service
        every { userDetailsService.loadUserByUsername(username, centerId) } returns userDetails
        
        // When
        jwtAuthenticationFilter.doFilter(request, response, filterChain)
        
        // Then
        verify { tokenProvider.validateToken(token) }
        verify { tokenProvider.getUsernameFromToken(token) }
        verify { tokenProvider.getCenterIdFromToken(token) }
        verify { userDetailsService.loadUserByUsername(username, centerId) }
        verify { filterChain.doFilter(any(), any()) }
        
        // Verify that authentication was set in the security context
        val authentication = SecurityContextHolder.getContext().authentication
        assert(authentication is UsernamePasswordAuthenticationToken)
        assert(authentication.principal == userDetails)
        assert(authentication.authorities == userDetails.authorities)
    }
    
    @Test
    @Throws(ServletException::class, IOException::class)
    fun `should authenticate user when valid token without centerId is provided`() {
        // Given
        val token = "valid.jwt.token"
        val username = "testuser"
        val authorities = listOf(SimpleGrantedAuthority("READ"))
        val userDetails = CustomUserDetails(
            username = username,
            password = "password",
            authorities = authorities,
            orgId = 1
        )
        
        // Set Authorization header with Bearer token
        request.addHeader("Authorization", "Bearer $token")
        
        // Mock token provider behavior
        every { tokenProvider.validateToken(token) } returns true
        every { tokenProvider.getUsernameFromToken(token) } returns username
        every { tokenProvider.getCenterIdFromToken(token) } returns null
        
        // Mock user details service
        every { userDetailsService.loadUserByUsername(username) } returns userDetails
        
        // When
        jwtAuthenticationFilter.doFilter(request, response, filterChain)
        
        // Then
        verify { tokenProvider.validateToken(token) }
        verify { tokenProvider.getUsernameFromToken(token) }
        verify { tokenProvider.getCenterIdFromToken(token) }
        verify { userDetailsService.loadUserByUsername(username) }
        verify { filterChain.doFilter(any(), any()) }
        
        // Verify that authentication was set in the security context
        val authentication = SecurityContextHolder.getContext().authentication
        assert(authentication is UsernamePasswordAuthenticationToken)
        assert(authentication.principal == userDetails)
        assert(authentication.authorities == userDetails.authorities)
    }
    
    @Test
    @Throws(ServletException::class, IOException::class)
    fun `should not authenticate user when invalid token is provided`() {
        // Given
        val token = "invalid.jwt.token"
        
        // Set Authorization header with Bearer token
        request.addHeader("Authorization", "Bearer $token")
        
        // Mock token provider to return false for validation
        every { tokenProvider.validateToken(token) } returns false
        
        // When
        jwtAuthenticationFilter.doFilter(request, response, filterChain)
        
        // Then
        verify { tokenProvider.validateToken(token) }
        verify(exactly = 0) { tokenProvider.getUsernameFromToken(any()) }
        verify(exactly = 0) { userDetailsService.loadUserByUsername(any()) }
        verify { filterChain.doFilter(any(), any()) }
        
        // Verify that no authentication was set in the security context
        assert(SecurityContextHolder.getContext().authentication == null)
    }
    
    @Test
    @Throws(ServletException::class, IOException::class)
    fun `should not authenticate user when no token is provided`() {
        // Given
        // No Authorization header is added to the request
        
        // When
        jwtAuthenticationFilter.doFilter(request, response, filterChain)
        
        // Then
        verify(exactly = 0) { tokenProvider.validateToken(any()) }
        verify(exactly = 0) { tokenProvider.getUsernameFromToken(any()) }
        verify(exactly = 0) { userDetailsService.loadUserByUsername(any()) }
        verify { filterChain.doFilter(any(), any()) }
        
        // Verify that no authentication was set in the security context
        assert(SecurityContextHolder.getContext().authentication == null)
    }
    
    @Test
    @Throws(ServletException::class, IOException::class)
    fun `should not authenticate user when token does not start with Bearer`() {
        // Given
        val token = "valid.jwt.token"
        
        // Set Authorization header without Bearer prefix
        request.addHeader("Authorization", token)
        
        // When
        jwtAuthenticationFilter.doFilter(request, response, filterChain)
        
        // Then
        verify(exactly = 0) { tokenProvider.validateToken(any()) }
        verify(exactly = 0) { tokenProvider.getUsernameFromToken(any()) }
        verify(exactly = 0) { userDetailsService.loadUserByUsername(any()) }
        verify { filterChain.doFilter(any(), any()) }
        
        // Verify that no authentication was set in the security context
        assert(SecurityContextHolder.getContext().authentication == null)
    }
    
    @Test
    @Throws(ServletException::class, IOException::class)
    fun `should continue filter chain when exception occurs during authentication`() {
        // Given
        val token = "valid.jwt.token"
        
        // Set Authorization header with Bearer token
        request.addHeader("Authorization", "Bearer $token")
        
        // Mock token provider to throw exception
        every { tokenProvider.validateToken(token) } throws RuntimeException("Test exception")
        
        // When
        jwtAuthenticationFilter.doFilter(request, response, filterChain)
        
        // Then
        verify { tokenProvider.validateToken(token) }
        verify(exactly = 0) { tokenProvider.getUsernameFromToken(any()) }
        verify(exactly = 0) { userDetailsService.loadUserByUsername(any()) }
        verify { filterChain.doFilter(any(), any()) }
        
        // Verify that no authentication was set in the security context
        assert(SecurityContextHolder.getContext().authentication == null)
    }
}