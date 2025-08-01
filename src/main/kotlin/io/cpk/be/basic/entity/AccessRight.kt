package io.cpk.be.basic.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "access_right")
class AccessRight(
    @Id @Column(name = "name", nullable = false) val name: String,
) {
    // No-arg constructor required by JPA and the no-arg plugin
    constructor() : this("")

    // Add copy method to maintain data class functionality
    fun copy(
        name: String = this.name,
    ): AccessRight {
        val result = AccessRight(name)
        // We can't directly set createdAt, updatedAt, createdBy, and updatedBy as they are val properties
        // This is a limitation of our approach, but in most cases, these fields
        // shouldn't need to be copied with different values anyway
        return result
    }

    // Add equals, hashCode, and toString methods to maintain data class functionality
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is AccessRight) return false
        return name == other.name
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }

    override fun toString(): String {
        return "AccessRight(name='$name')"
    }
}
