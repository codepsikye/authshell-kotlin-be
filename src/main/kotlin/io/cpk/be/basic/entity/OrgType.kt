package io.cpk.be.basic.entity

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import io.cpk.be.basic.dto.OrgConfig
import io.cpk.be.config.JacksonConfig
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes

@Entity
@Table(name = "org_type")
class OrgType(
    @Id @Column(name = "name", nullable = false) val name: String,
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "access_right", nullable = false, columnDefinition = "json")
    val accessRight: List<String> = emptyList(),
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "org_configs", nullable = false, columnDefinition = "json")
    val orgConfigs: OrgConfig
) : BaseAuditable() {
    // No-arg constructor required by JPA and the no-arg plugin
    constructor() : this("", emptyList(), OrgConfig())
    
    // Constructor for backward compatibility with tests
    constructor(name: String, accessRight: List<String> = emptyList(), orgConfigs: Map<String, Any>) : 
        this(name, accessRight, OrgConfig.fromMap(orgConfigs))

    // Add copy method to maintain data class functionality
    fun copy(
        name: String = this.name,
        accessRight: List<String> = this.accessRight,
        orgConfigs: OrgConfig = this.orgConfigs
    ): OrgType {
        return OrgType(name, accessRight, orgConfigs)
    }

    // Add equals, hashCode, and toString methods to maintain data class functionality
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is OrgType) return false
        if (name != other.name) return false
        if (accessRight != other.accessRight) return false
        // Compare OrgConfigs by their content
        if (orgConfigs.toMap() != other.orgConfigs.toMap()) return false
        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + accessRight.hashCode()
        result = 31 * result + orgConfigs.hashCode()
        return result
    }

    override fun toString(): String {
        return "OrgType(name='$name', accessRight=$accessRight, orgConfigs=$orgConfigs)"
    }
}
