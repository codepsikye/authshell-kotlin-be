package io.cpk.be.basic.entity

import io.cpk.be.basic.dto.OrgConfig
import jakarta.persistence.*
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes

@Entity
@Table(name = "org")
class Org(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "org_id_seq")
    @SequenceGenerator(name = "org_id_seq", sequenceName = "org_id_seq", allocationSize = 1)
    @Column(name = "id", nullable = false)
    val id: Int? = null,

    @Column(name = "name", nullable = false)
    val name: String,

    @Column(name = "address")
    val address: String? = null,

    @Column(name = "phone")
    val phone: String? = null,

    @Column(name = "city")
    val city: String? = null,

    @Column(name = "country")
    val country: String? = null,

    @Column(name = "notes")
    val notes: String? = null,
    
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "org_configs", nullable = false, columnDefinition = "json")
    val orgConfigs: OrgConfig = OrgConfig(),

    @Column(name = "org_type_name")
    val orgTypeName: String = "",

    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "org_type_name", referencedColumnName = "name", insertable = false, updatable = false)
    val orgType: OrgType? = null,

) : BaseAuditable() {
    // No-arg constructor required by JPA and the no-arg plugin
    constructor() : this(null, "", null, null, null, null, null, OrgConfig(), "", null)

    // Constructor for backward compatibility with tests
    constructor(
        id: Int? = null,
        name: String,
        address: String? = null,
        phone: String? = null,
        city: String? = null,
        country: String? = null,
        notes: String? = null,
        orgConfigs: Map<String, Any>,
        orgTypeName: String = "",
        orgType: OrgType? = null
    ) : this(id, name, address, phone, city, country, notes, OrgConfig.fromMap(orgConfigs), orgTypeName, orgType)

    // Add copy method to maintain data class functionality
    fun copy(
        id: Int? = this.id,
        name: String = this.name,
        address: String? = this.address,
        phone: String? = this.phone,
        city: String? = this.city,
        country: String? = this.country,
        notes: String? = this.notes,
        orgConfigs: OrgConfig = this.orgConfigs,
        orgTypeName: String = this.orgTypeName,
        orgType: OrgType? = this.orgType,
    ): Org {
        return Org(id, name, address, phone, city, country, notes, orgConfigs, orgTypeName, orgType)
    }

    // Add equals, hashCode, and toString methods to maintain data class functionality
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Org) return false
        if (id != other.id) return false
        if (name != other.name) return false
        if (address != other.address) return false
        if (phone != other.phone) return false
        if (city != other.city) return false
        if (country != other.country) return false
        if (notes != other.notes) return false
        if (orgTypeName != other.orgTypeName) return false
        // Compare OrgConfigs by their content
        if (orgConfigs.toMap() != other.orgConfigs.toMap()) return false
        return true
    }

    override fun hashCode(): Int {
        var result = id?.hashCode() ?: 0
        result = 31 * result + name.hashCode()
        result = 31 * result + (address?.hashCode() ?: 0)
        result = 31 * result + (phone?.hashCode() ?: 0)
        result = 31 * result + (city?.hashCode() ?: 0)
        result = 31 * result + (country?.hashCode() ?: 0)
        result = 31 * result + (notes?.hashCode() ?: 0)
        result = 31 * result + orgTypeName.hashCode()
        result = 31 * result + orgConfigs.hashCode()
        return result
    }

    override fun toString(): String {
        return "Org(id=$id, name='$name', address=$address, phone=$phone, city=$city, " +
                "country=$country, notes=$notes, orgTypeName='$orgTypeName', orgType.name='${orgType?.name}', orgConfigs=$orgConfigs)"
    }
}