package io.cpk.be.basic.entity

import jakarta.persistence.*

@Entity
@Table(name = "center")
class Center(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "center_id_seq")
    @SequenceGenerator(
        name = "center_id_seq",
        sequenceName = "center_id_seq",
        allocationSize = 1
    )
    @Column(name = "id", nullable = false)
    val id: Int? = null,
    @Column(name = "name", nullable = false) val name: String,
    @Column(name = "address") val address: String? = null,
    @Column(name = "phone") val phone: String? = null,
    @Column(name = "org_id") val orgId: Int? = null,
    // Relationships
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "org_id", insertable = false, updatable = false)
    val org: Org? = null,

) : BaseAuditable() {
    // No-arg constructor required by JPA and the no-arg plugin
    constructor() : this(null, "", null, null, null, null)

    // Add copy method to maintain data class functionality
    fun copy(
        id: Int? = this.id,
        name: String = this.name,
        address: String? = this.address,
        phone: String? = this.phone,
        orgId: Int? = this.orgId,
        org: Org? = this.org
    ): Center {
        return Center(id, name, address, phone, orgId, org)
    }

    // Add equals, hashCode, and toString methods to maintain data class functionality
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Center) return false
        if (id != other.id) return false
        if (name != other.name) return false
        if (address != other.address) return false
        if (phone != other.phone) return false
        if (orgId != other.orgId) return false
        return true
    }

    override fun hashCode(): Int {
        var result = id?.hashCode() ?: 0
        result = 31 * result + name.hashCode()
        result = 31 * result + (address?.hashCode() ?: 0)
        result = 31 * result + (phone?.hashCode() ?: 0)
        result = 31 * result + (orgId?.hashCode() ?: 0)
        return result
    }

    override fun toString(): String {
        return "Center(id=$id, name='$name', address=$address, phone=$phone, orgId=$orgId)"
    }
}
