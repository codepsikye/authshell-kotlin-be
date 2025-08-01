package io.cpk.be.config

import io.mockk.mockk
import io.mockk.verify
import org.flywaydb.core.api.configuration.FluentConfiguration
import org.junit.jupiter.api.Test
import kotlin.test.assertNotNull

class FlywayConfigTest {

    @Test
    fun `should create FlywayConfigurationCustomizer bean`() {
        // Given
        val flywayConfig = FlywayConfig()
        val flywayCallback = mockk<FlywayCallback>()
        
        // When
        val customizer = flywayConfig.flywayConfigurationCustomizer(flywayCallback)
        
        // Then
        assertNotNull(customizer, "FlywayConfigurationCustomizer should not be null")
    }

    @Test
    fun `should register callback with Flyway configuration`() {
        // Given
        val flywayConfig = FlywayConfig()
        val flywayCallback = mockk<FlywayCallback>()
        val configuration = mockk<FluentConfiguration>(relaxed = true)
        
        // When
        val customizer = flywayConfig.flywayConfigurationCustomizer(flywayCallback)
        customizer.customize(configuration)
        
        // Then
        verify { configuration.callbacks(flywayCallback) }
    }
}