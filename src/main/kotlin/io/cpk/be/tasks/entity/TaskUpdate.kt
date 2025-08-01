package io.cpk.be.tasks.entity

import io.cpk.be.basic.entity.BaseAuditable
import jakarta.persistence.*

@Entity
@Table(name = "task_update")
class TaskUpdate(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "task_update_id_seq")
    @SequenceGenerator(name = "task_update_id_seq", sequenceName = "task_update_id_seq", allocationSize = 1)
    @Column(name = "id", nullable = false)
    val id: Long? = null,

    @Column(name = "task_id", nullable = false)
    val taskId: Long,

    @Column(name = "body")
    val body: String? = null,

    @Column(name = "status", nullable = false)
    val status: String,

    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id", insertable = false, updatable = false)
    val task: Task? = null,
) : BaseAuditable() {
    // No-arg constructor required by JPA and the no-arg plugin
    constructor() : this(null, 0, null, "", null)

    // Add copy method to maintain data class functionality
    fun copy(
        id: Long? = this.id,
        taskId: Long = this.taskId,
        body: String? = this.body,
        status: String = this.status,
        task: Task? = this.task
    ): TaskUpdate {
        return TaskUpdate(id, taskId, body, status, task)
    }

    // Add equals, hashCode, and toString methods to maintain data class functionality
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is TaskUpdate) return false
        if (id != other.id) return false
        if (taskId != other.taskId) return false
        if (body != other.body) return false
        if (status != other.status) return false
        return true
    }

    override fun hashCode(): Int {
        var result = id?.hashCode() ?: 0
        result = 31 * result + taskId.hashCode()
        result = 31 * result + (body?.hashCode() ?: 0)
        result = 31 * result + status.hashCode()
        return result
    }

    override fun toString(): String {
        return "TaskUpdate(id=$id, taskId=$taskId, body=$body, status='$status', createdAt=$createdAt, updatedAt=$updatedAt)"
    }
}