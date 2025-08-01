package io.cpk.be.tasks.repository

import io.cpk.be.tasks.entity.Task
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface TaskRepository : JpaRepository<Task, Long> {
    fun findAllByCenterId(centerId: Int, pageable: Pageable): Page<Task>

    fun findAllByCenterIdAndStatusContainingAndSubjectContaining(
        centerId: Int,
        status: String,
        subject: String,
        pageable: Pageable
    ): Page<Task>

    @Query("SELECT CASE WHEN COUNT(t) > 0 THEN true ELSE false END FROM Task t WHERE t.id = :id AND t.centerId = :centerId")
    fun existsByIdAndCenterId(@Param("id") id: Long, @Param("centerId") centerId: Int): Boolean
}