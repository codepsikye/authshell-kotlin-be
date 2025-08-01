package io.cpk.be.basic.entity

import jakarta.persistence.*

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

    @Column(name = "org_type_name", nullable = false)
    val orgTypeName: String,

    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "org_type_name", referencedColumnName = "name", insertable = false, updatable = false)
    val orgType: OrgType? = null,

) : BaseAuditable() {
    // No-arg constructor required by JPA and the no-arg plugin
    constructor() : this(null, "", null, null, null, null, null, "", null)

    // Add copy method to maintain data class functionality
    fun copy(
        id: Int? = this.id,
        name: String = this.name,
        address: String? = this.address,
        phone: String? = this.phone,
        city: String? = this.city,
        country: String? = this.country,
        notes: String? = this.notes,
        orgTypeName: String = this.orgTypeName,
        orgType: OrgType? = this.orgType,
    ): Org {
        return Org(id, name, address, phone, city, country, notes, orgTypeName, orgType)
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
        return result
    }

    override fun toString(): String {
        return "Org(id=$id, name='$name', address=$address, phone=$phone, city=$city, " +
                "country=$country, notes=$notes, orgTypeName='$orgTypeName')"
    }
}
