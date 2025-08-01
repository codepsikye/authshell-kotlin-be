package io.cpk.be.basic.dto

import com.fasterxml.jackson.annotation.JsonAnyGetter
import com.fasterxml.jackson.annotation.JsonAnySetter
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import java.io.IOException

/**
 * Class to handle user preferences stored as JSON in the database
 * All properties are stored in a single map for maximum flexibility
 */
@JsonDeserialize(using = UserPrefs.UserPrefsDeserializer::class)
class UserPrefs {
    // Store all properties in a single map
    private val properties = mutableMapOf<String, Any>()
    
    constructor()
    
    @JsonCreator
    constructor(map: Map<String, Any>) {
        // Store all properties
        properties.putAll(map)
    }
    
    /**
     * Get a property value by key
     * @param key The property key
     * @return The property value or null if not found
     */
    fun getProperty(key: String): Any? {
        return properties[key]
    }
    
    /**
     * Add or update a property
     * @param key The property key
     * @param value The property value
     * @return The UserPrefs instance for chaining
     */
    fun setProperty(key: String, value: Any): UserPrefs {
        properties[key] = value
        return this
    }
    
    /**
     * Remove a property
     * @param key The property key to remove
     * @return The UserPrefs instance for chaining
     */
    fun removeProperty(key: String): UserPrefs {
        properties.remove(key)
        return this
    }
    
    /**
     * Check if a property exists
     * @param key The property key to check
     * @return True if the property exists
     */
    fun hasProperty(key: String): Boolean {
        return properties.containsKey(key)
    }
    
    /**
     * Get all properties as a map
     * @return Map of all properties
     */
    fun toMap(): Map<String, Any> {
        return properties.toMap()
    }
    
    // Allow Jackson to serialize all properties
    @JsonAnyGetter
    fun getProperties(): Map<String, Any> {
        return properties
    }
    
    // Allow Jackson to deserialize properties
    @JsonAnySetter
    fun addProperty(key: String, value: Any) {
        properties[key] = value
    }
    
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is UserPrefs) return false
        
        // Compare properties by their content
        if (properties != other.properties) return false
        
        return true
    }
    
    override fun hashCode(): Int {
        return properties.hashCode()
    }
    
    override fun toString(): String {
        return "UserPrefs($properties)"
    }
    
    companion object {
        // Utility method to help with conversions
        fun fromMap(map: Map<String, Any>?): UserPrefs {
            return if (map == null) UserPrefs() else UserPrefs(map)
        }
    }
    
    /**
     * Custom deserializer for UserPrefs to handle JSON strings from the database
     */
    class UserPrefsDeserializer : JsonDeserializer<UserPrefs>() {
        @Throws(IOException::class, JsonProcessingException::class)
        override fun deserialize(jp: JsonParser, ctxt: DeserializationContext): UserPrefs {
            val userPrefs = UserPrefs()
            
            // Handle empty JSON
            if (jp.currentToken == null) {
                return userPrefs
            }
            
            // Handle JSON string (common in database)
            if (jp.currentToken.isScalarValue) {
                val text = jp.text
                if (text.isNullOrEmpty() || text == "{}") {
                    return userPrefs
                }
                // If it's a JSON string, parse it as a new JSON object
                try {
                    // Try to parse the text as JSON
                    val objectMapper = ctxt.findInjectableValue("objectMapper", null, null) as? com.fasterxml.jackson.databind.ObjectMapper
                        ?: jp.codec as? com.fasterxml.jackson.databind.ObjectMapper
                    
                    if (objectMapper != null) {
                        val node = objectMapper.readValue(text, JsonNode::class.java)
                        // Handle all properties
                        node.fields().forEach { (key, value) ->
                            when {
                                value.isTextual -> userPrefs.properties[key] = value.asText()
                                value.isBoolean -> userPrefs.properties[key] = value.asBoolean()
                                value.isInt -> userPrefs.properties[key] = value.asInt()
                                value.isDouble -> userPrefs.properties[key] = value.asDouble()
                                value.isObject -> userPrefs.properties[key] = objectMapper.treeToValue(value, Map::class.java)
                                value.isArray -> userPrefs.properties[key] = objectMapper.treeToValue(value, List::class.java)
                            }
                        }
                    }
                } catch (e: Exception) {
                    // If parsing fails, just return empty preferences
                    return userPrefs
                }
                return userPrefs
            }
            
            // Handle JSON object
            val node = jp.codec.readTree<JsonNode>(jp)
            // Handle all properties
            node.fields().forEach { (key, value) ->
                when {
                    value.isTextual -> userPrefs.properties[key] = value.asText()
                    value.isBoolean -> userPrefs.properties[key] = value.asBoolean()
                    value.isInt -> userPrefs.properties[key] = value.asInt()
                    value.isDouble -> userPrefs.properties[key] = value.asDouble()
                    value.isObject -> userPrefs.properties[key] = jp.codec.treeToValue(value, Map::class.java)
                    value.isArray -> userPrefs.properties[key] = jp.codec.treeToValue(value, List::class.java)
                }
            }
            
            return userPrefs
        }
    }
}