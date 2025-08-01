package io.cpk.be.config

import io.cpk.be.security.CustomUserDetailsService
import io.cpk.be.security.JwtAuthenticationFilter
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * Unit tests for SecurityConfig
 */
class SecurityConfigTest {

    private lateinit var customUserDetailsService: CustomUserDetailsService
    private lateinit var jwtAuthenticationFilter: JwtAuthenticationFilter
    private lateinit var authenticationConfiguration: AuthenticationConfiguration
    private lateinit var securityConfig: SecurityConfig

    @BeforeEach
    fun setUp() {
        customUserDetailsService = mockk(relaxed = true)
        jwtAuthenticationFilter = mockk(relaxed = true)
        authenticationConfiguration = mockk(relaxed = true)
        securityConfig = SecurityConfig(customUserDetailsService, jwtAuthenticationFilter)
    }

    @Test
    fun `should create password encoder`() {
        // When
        val passwordEncoder = securityConfig.passwordEncoder()

        // Then
        assertNotNull(passwordEncoder)
        assertTrue(passwordEncoder is BCryptPasswordEncoder)
    }

    @Test
    fun `should create authentication provider`() {
        // When
        val authenticationProvider = securityConfig.authenticationProvider()

        // Then
        assertNotNull(authenticationProvider)
        assertTrue(authenticationProvider is DaoAuthenticationProvider)
    }

    @Test
    fun `should create cors configuration source`() {
        // When
        val corsConfigurationSource = securityConfig.corsConfigurationSource()

        // Then
        assertNotNull(corsConfigurationSource)
        
        // Since we're using UrlBasedCorsConfigurationSource, we can directly check the configuration
        // by accessing the configuration for a specific path pattern
        val source = corsConfigurationSource as org.springframework.web.cors.UrlBasedCorsConfigurationSource
        val corsConfiguration = source.getCorsConfigurations()["/**"]
        
        // Verify CORS settings
        assertNotNull(corsConfiguration)
        assertEquals(listOf("*"), corsConfiguration.allowedOriginPatterns)
        corsConfiguration.allowedMethods?.let { methods ->
            assertTrue(methods.containsAll(listOf("GET", "POST", "PUT", "DELETE", "OPTIONS")))
        }
        assertEquals(listOf("*"), corsConfiguration.allowedHeaders)
        assertEquals(true, corsConfiguration.allowCredentials)
        assertEquals(3600L, corsConfiguration.maxAge)
    }
}