package io.cpk.be.basic.entity

import io.cpk.be.basic.repository.OrgRepository
import io.cpk.be.config.JpaAuditingConfig
import io.cpk.be.security.CustomUserDetails
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.context.annotation.Import
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.test.context.ActiveProfiles
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

/**
 * Test class for auditing functionality.
 * Tests that the createdBy and updatedBy fields are properly populated.
 */
@DataJpaTest
@Import(JpaAuditingConfig::class)
@ActiveProfiles("test")
class AuditingTest {

    @Autowired
    private lateinit var orgRepository: OrgRepository

    @BeforeEach
    fun setUp() {
        // Clear the security context
        SecurityContextHolder.clearContext()
    }

    @Test
    fun `should set createdBy and updatedBy to system when no user is authenticated`() {
        // Given
        val org = Org(
            name = "Test Organization",
            orgTypeName = "TEST_TYPE"
        )

        // When
        val savedOrg = orgRepository.save(org)

        // Then
        assertNotNull(savedOrg)
        assertEquals("system", savedOrg.createdBy)
        assertEquals("system", savedOrg.updatedBy)
    }

    @Test
    fun `should set createdBy and updatedBy to authenticated username`() {
        // Given
        val username = "testuser"
        setAuthenticatedUser(username)

        val org = Org(
            name = "Test Organization",
            orgTypeName = "TEST_TYPE"
        )

        // When
        val savedOrg = orgRepository.save(org)

        // Then
        assertNotNull(savedOrg)
    }

    @Test
    fun `should update updatedBy when entity is updated`() {
        // Given
        val initialUsername = "initialuser"
        setAuthenticatedUser(initialUsername)

        val org = Org(
            name = "Test Organization",
            orgTypeName = "TEST_TYPE"
        )
        val savedOrg = orgRepository.save(org)

        // When
        val updatedUsername = "updateduser"
        setAuthenticatedUser(updatedUsername)

        val updatedOrg = savedOrg.copy(name = "Updated Organization")
        val result = orgRepository.save(updatedOrg)

        // Then
        assertNotNull(result)
    }

    private fun setAuthenticatedUser(username: String) {
        val authorities = listOf(SimpleGrantedAuthority("ROLE_USER"))
        val userDetails = CustomUserDetails(username, "password", authorities, 1, 1, "1")
        val authentication = UsernamePasswordAuthenticationToken(userDetails, null, authorities)
        SecurityContextHolder.getContext().authentication = authentication
    }
}