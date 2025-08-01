package io.cpk.be.config

import io.cpk.be.security.JwtTokenProvider
import io.mockk.mockk
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary

/**
 * Test configuration for repository tests.
 * This provides a mock JwtTokenProvider to avoid circular dependency issues in tests.
 */
@TestConfiguration
class TestConfig {
    
    /**
     * Provides a mock JwtTokenProvider for tests.
     * This prevents the real JwtTokenProvider from being created in the test context,
     * which would require an AppUserRepository and cause circular dependency issues.
     */
    @Bean
    @Primary
    fun jwtTokenProvider(): JwtTokenProvider {
        // Create a mock JwtTokenProvider using MockK
        return mockk<JwtTokenProvider>(relaxed = true)
    }
}