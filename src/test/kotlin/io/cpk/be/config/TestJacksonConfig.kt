package io.cpk.be.config

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonToken
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import java.io.IOException

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
        
        // Register custom module for handling Map<String, Any>
        val module = SimpleModule()
        module.addDeserializer(Map::class.java, MapDeserializer())
        objectMapper.registerModule(module)
        
        return objectMapper
    }
    
    /**
     * Custom deserializer for Map<String, Any> to handle JSON objects
     */
    class MapDeserializer : JsonDeserializer<Map<String, Any>>() {
        @Throws(IOException::class)
        override fun deserialize(p: JsonParser, ctxt: DeserializationContext): Map<String, Any> {
            val result = mutableMapOf<String, Any>()
            
            if (p.currentToken == JsonToken.START_OBJECT) {
                while (p.nextToken() != JsonToken.END_OBJECT) {
                    val fieldName = p.currentName
                    p.nextToken()
                    
                    // Handle different value types
                    val value = when (p.currentToken) {
                        JsonToken.VALUE_STRING -> p.valueAsString
                        JsonToken.VALUE_NUMBER_INT -> p.valueAsLong
                        JsonToken.VALUE_NUMBER_FLOAT -> p.valueAsDouble
                        JsonToken.VALUE_TRUE -> true
                        JsonToken.VALUE_FALSE -> false
                        JsonToken.VALUE_NULL -> "" // Use empty string for null values
                        JsonToken.START_OBJECT -> deserialize(p, ctxt)
                        JsonToken.START_ARRAY -> {
                            val list = mutableListOf<Any>()
                            while (p.nextToken() != JsonToken.END_ARRAY) {
                                when (p.currentToken) {
                                    JsonToken.VALUE_STRING -> list.add(p.valueAsString)
                                    JsonToken.VALUE_NUMBER_INT -> list.add(p.valueAsLong)
                                    JsonToken.VALUE_NUMBER_FLOAT -> list.add(p.valueAsDouble)
                                    JsonToken.VALUE_TRUE -> list.add(true)
                                    JsonToken.VALUE_FALSE -> list.add(false)
                                    JsonToken.VALUE_NULL -> list.add("") // Use empty string for null values
                                    else -> list.add(p.readValueAs(Any::class.java))
                                }
                            }
                            list
                        }
                        else -> p.readValueAs(Any::class.java)
                    }
                    
                    result[fieldName] = value
                }
            }
            
            return result
        }
    }
}