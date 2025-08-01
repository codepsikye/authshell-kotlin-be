package io.cpk.be.basic.dto

import com.fasterxml.jackson.annotation.JsonAnyGetter
import com.fasterxml.jackson.annotation.JsonAnySetter
import com.fasterxml.jackson.annotation.JsonIgnore

class OrgConfig {
    // Store all properties in a single map
    private val properties = mutableMapOf<String, Any>()
    
    constructor()
    
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
     * @return The OrgConfig instance for chaining
     */
    fun setProperty(key: String, value: Any): OrgConfig {
        properties[key] = value
        return this
    }
    
    /**
     * Remove a property
     * @param key The property key to remove
     * @return The OrgConfig instance for chaining
     */
    fun removeProperty(key: String): OrgConfig {
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
    
    /**
     * Get all properties as a map
     * @return Map of all properties
     */
    fun toMap(): Map<String, Any> {
        return properties.toMap()
    }
    
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is OrgConfig) return false
        
        // Compare properties by their content
        if (properties != other.properties) return false
        
        return true
    }
    
    override fun hashCode(): Int {
        return properties.hashCode()
    }
    
    override fun toString(): String {
        return "OrgConfig(${toMap()})"
    }
    
    companion object {
        // Utility method to help with test migrations
        fun fromMap(map: Map<String, Any>?): OrgConfig {
            return if (map == null) OrgConfig() else OrgConfig(map)
        }
    }
}