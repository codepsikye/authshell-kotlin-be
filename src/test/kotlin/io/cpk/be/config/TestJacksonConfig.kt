package io.cpk.be.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary

/**
 * Test-specific Jackson configuration for handling JSON serialization/deserialization
 */
@TestConfiguration
class TestJacksonConfig {

    /**
     * Configure the ObjectMapper with Kotlin module and custom settings for tests
     */
    @Bean
    @Primary
    fun objectMapper(): ObjectMapper {
        val objectMapper = ObjectMapper()
        
        // Register Kotlin module for better Kotlin support
        objectMapper.registerModule(KotlinModule.Builder().build())
        
        // Configure for testing
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
        
        // Enable default typing for Map<String, Any>
        objectMapper.enableDefaultTyping()
        
        return objectMapper
    }
}