package io.cpk.be.basic.dto

import com.fasterxml.jackson.annotation.JsonAnyGetter
import com.fasterxml.jackson.annotation.JsonAnySetter
import com.fasterxml.jackson.annotation.JsonIgnore

class OrgConfig {
    var maxUsers: Int = 10
    var maxCenter: Int = 3
    var defaultCenter: Int = 1
    // other real configs
    
    // Store custom properties - make it accessible to Jackson
    @get:JsonIgnore
    private val customProperties = mutableMapOf<String, Any>()
    
    constructor()
    
    constructor(map: Map<String, Any>) {
        map["maxUsers"]?.let { if (it is Int) maxUsers = it else if (it is String) maxUsers = it.toIntOrNull() ?: maxUsers }
        map["maxCenter"]?.let { if (it is Int) maxCenter = it else if (it is String) maxCenter = it.toIntOrNull() ?: maxCenter }
        map["defaultCenter"]?.let { if (it is Int) defaultCenter = it else if (it is String) defaultCenter = it.toIntOrNull() ?: defaultCenter }
        
        // Store all properties that aren't standard fields
        map.forEach { (key, value) ->
            if (key !in listOf("maxUsers", "maxCenter", "defaultCenter")) {
                customProperties[key] = value
            }
        }
    }
    
    // Allow Jackson to serialize custom properties
    @JsonAnyGetter
    fun getCustomProperties(): Map<String, Any> {
        return customProperties
    }
    
    // Allow Jackson to deserialize custom properties
    @JsonAnySetter
    fun setCustomProperty(key: String, value: Any) {
        if (key !in listOf("maxUsers", "maxCenter", "defaultCenter")) {
            customProperties[key] = value
        }
    }
    
    fun toMap(): Map<String, Any> {
        val result = mutableMapOf<String, Any>()
        
        // Add standard properties
        result["maxUsers"] = maxUsers
        result["maxCenter"] = maxCenter
        result["defaultCenter"] = defaultCenter
        
        // Add all custom properties
        result.putAll(customProperties)
        
        return result
    }
    
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is OrgConfig) return false
        
        if (maxUsers != other.maxUsers) return false
        if (maxCenter != other.maxCenter) return false
        if (defaultCenter != other.defaultCenter) return false
        
        // Compare custom properties by their content
        if (customProperties != other.customProperties) return false
        
        return true
    }
    
    override fun hashCode(): Int {
        var result = maxUsers
        result = 31 * result + maxCenter
        result = 31 * result + defaultCenter
        result = 31 * result + customProperties.hashCode()
        return result
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