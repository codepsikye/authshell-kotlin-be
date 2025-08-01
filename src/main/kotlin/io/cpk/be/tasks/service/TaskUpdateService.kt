package io.cpk.be.tasks.service

import io.cpk.be.tasks.dto.TaskUpdateDto
import io.cpk.be.tasks.mapper.TaskUpdateMapper
import io.cpk.be.tasks.repository.TaskUpdateRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class TaskUpdateService(
    private val taskUpdateRepository: TaskUpdateRepository,
    private val taskUpdateMapper: TaskUpdateMapper
) {
    fun create(taskUpdateDto: TaskUpdateDto): TaskUpdateDto {
        val taskUpdate = taskUpdateMapper.toEntity(taskUpdateDto)
        val savedTaskUpdate = taskUpdateRepository.save(taskUpdate)
        return taskUpdateMapper.toDto(savedTaskUpdate)
    }

    @Transactional(readOnly = true)
    fun findAll(): List<TaskUpdateDto> {
        return taskUpdateRepository.findAll().map(taskUpdateMapper::toDto)
    }

    @Transactional(readOnly = true)
    fun findAll(pageable: Pageable): Page<TaskUpdateDto> {
        val taskUpdatePage = taskUpdateRepository.findAll(pageable)
        val taskUpdateDtos = taskUpdatePage.content.map(taskUpdateMapper::toDto)
        return PageImpl(taskUpdateDtos, pageable, taskUpdatePage.totalElements)
    }

    @Transactional(readOnly = true)
    fun findAll(centerId: Int, pageable: Pageable): Page<TaskUpdateDto> {
        val taskUpdatePage = taskUpdateRepository.findAllByTaskCenterId(centerId, pageable)
        val taskUpdateDtos = taskUpdatePage.content.map(taskUpdateMapper::toDto)
        return PageImpl(taskUpdateDtos, pageable, taskUpdatePage.totalElements)
    }

    @Transactional(readOnly = true)
    fun findById(id: Long): TaskUpdateDto? {
        return taskUpdateRepository.findById(id).map(taskUpdateMapper::toDto).orElse(null)
    }

    @Transactional(readOnly = true)
    fun existsByIdAndTaskCenterId(id: Long, centerId: Int): Boolean {
        return taskUpdateRepository.existsByIdAndTaskCenterId(id, centerId)
    }

    fun update(id: Long, taskUpdateDto: TaskUpdateDto): TaskUpdateDto {
        val existingTaskUpdate =
            taskUpdateRepository.findById(id).orElseThrow { RuntimeException("TaskUpdate not found") }
        val updatedTaskUpdate = taskUpdateMapper.toEntity(taskUpdateDto).copy(id = existingTaskUpdate.id)
        return taskUpdateMapper.toDto(taskUpdateRepository.save(updatedTaskUpdate))
    }

    fun delete(id: Long) {
        taskUpdateRepository.deleteById(id)
    }
}