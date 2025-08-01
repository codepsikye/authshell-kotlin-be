package io.cpk.be.config

import org.flywaydb.core.api.configuration.FluentConfiguration
import org.springframework.boot.autoconfigure.flyway.FlywayConfigurationCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * Configuration class for Flyway customization
 * Registers the custom FlywayCallback to handle migration failures
 */
@Configuration
class FlywayConfig {
    
    /**
     * Creates a FlywayConfigurationCustomizer bean that registers our custom callback
     * This ensures the callback is automatically configured when Spring Boot initializes Flyway
     */
    @Bean
    fun flywayConfigurationCustomizer(flywayCallback: FlywayCallback): FlywayConfigurationCustomizer {
        return FlywayConfigurationCustomizer { configuration: FluentConfiguration ->
            configuration.callbacks(flywayCallback)
        }
    }
}