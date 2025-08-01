package io.cpk.be.tasks.repository

import io.cpk.be.tasks.entity.TaskUpdate
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface TaskUpdateRepository : JpaRepository<TaskUpdate, Long> {
    @Query("SELECT tu FROM TaskUpdate tu JOIN tu.task t WHERE t.centerId = :centerId")
    fun findAllByTaskCenterId(@Param("centerId") centerId: Int, pageable: Pageable): Page<TaskUpdate>

    @Query("SELECT CASE WHEN COUNT(tu) > 0 THEN true ELSE false END FROM TaskUpdate tu JOIN tu.task t WHERE tu.id = :id AND t.centerId = :centerId")
    fun existsByIdAndTaskCenterId(@Param("id") id: Long, @Param("centerId") centerId: Int): Boolean
}