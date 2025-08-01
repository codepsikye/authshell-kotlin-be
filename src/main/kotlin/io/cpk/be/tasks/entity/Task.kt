package io.cpk.be.tasks.entity

import io.cpk.be.basic.entity.BaseAuditable
import io.cpk.be.basic.entity.Center
import jakarta.persistence.*

@Entity
@Table(name = "task")
class Task(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "task_id_seq")
    @SequenceGenerator(name = "task_id_seq", sequenceName = "task_id_seq", allocationSize = 1)
    @Column(name = "id", nullable = false)
    val id: Long? = null,

    @Column(name = "subject", nullable = false)
    val subject: String,

    @Column(name = "body")
    val body: String? = null,

    @Column(name = "status", nullable = false)
    val status: String = "pending",

    @Column(name = "center_id", nullable = false)
    val centerId: Int,

    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "center_id", insertable = false, updatable = false)
    val center: Center? = null,

) : BaseAuditable() {
    // No-arg constructor required by JPA and the no-arg plugin
    constructor() : this(null, "", null, "pending", 0, null)

    // Add copy method to maintain data class functionality
    fun copy(
        id: Long? = this.id,
        subject: String = this.subject,
        body: String? = this.body,
        status: String = this.status,
        centerId: Int = this.centerId,
        center: Center? = this.center,
    ): Task {
        return Task(id, subject, body, status, centerId, center)
    }

    // Add equals, hashCode, and toString methods to maintain data class functionality
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Task) return false
        if (id != other.id) return false
        if (subject != other.subject) return false
        if (body != other.body) return false
        if (status != other.status) return false
        if (centerId != other.centerId) return false
        return true
    }

    override fun hashCode(): Int {
        var result = id?.hashCode() ?: 0
        result = 31 * result + subject.hashCode()
        result = 31 * result + (body?.hashCode() ?: 0)
        result = 31 * result + status.hashCode()
        result = 31 * result + centerId.hashCode()
        return result
    }

    override fun toString(): String {
        return "Task(id=$id, subject='$subject', body=$body, status='$status', centerId=$centerId, " +
                "createdAt=$createdAt, updatedAt=$updatedAt)"
    }
}