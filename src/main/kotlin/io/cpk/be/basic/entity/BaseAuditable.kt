package io.cpk.be.basic.entity

import jakarta.persistence.Column
import jakarta.persistence.MappedSuperclass
import jakarta.persistence.Temporal
import jakarta.persistence.TemporalType
import org.springframework.data.annotation.CreatedBy
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedBy
import org.springframework.data.annotation.LastModifiedDate
import java.time.LocalDateTime

/**
 * Base class for entities that need auditing fields (createdAt, updatedAt, createdBy, updatedBy).
 * This class is meant to be extended by entity classes that need these fields.
 */
@MappedSuperclass
abstract class BaseAuditable(
    @CreatedDate
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", nullable = false, updatable = false)
    open val createdAt: LocalDateTime,

    @Temporal(TemporalType.TIMESTAMP)
    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    open val updatedAt: LocalDateTime,
    
    @CreatedBy
    @Column(name = "created_by", nullable = false, updatable = false, length = 50)
    open val createdBy: String,
    
    @LastModifiedBy
    @Column(name = "updated_by", nullable = false, length = 50)
    open val updatedBy: String
) {
    /**
     * Constructor with default values for createdAt, updatedAt, createdBy, and updatedBy
     */
    constructor() : this(LocalDateTime.now(), LocalDateTime.now(), "system", "system")
}
