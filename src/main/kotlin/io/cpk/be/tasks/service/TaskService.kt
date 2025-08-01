package io.cpk.be.tasks.service

import io.cpk.be.tasks.dto.TaskDto
import io.cpk.be.tasks.mapper.TaskMapper
import io.cpk.be.tasks.repository.TaskRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class TaskService(
    private val taskRepository: TaskRepository,
    private val taskMapper: TaskMapper
) {
    fun create(taskDto: TaskDto): TaskDto {
        val task = taskMapper.toEntity(taskDto)
        val savedTask = taskRepository.save(task)
        return taskMapper.toDto(savedTask)
    }

    @Transactional(readOnly = true)
    fun findAll(): List<TaskDto> {
        return taskRepository.findAll().map(taskMapper::toDto)
    }

    @Transactional(readOnly = true)
    fun findAll(pageable: Pageable): Page<TaskDto> {
        val taskPage = taskRepository.findAll(pageable)
        val taskDtos = taskPage.content.map(taskMapper::toDto)
        return PageImpl(taskDtos, pageable, taskPage.totalElements)
    }

    @Transactional(readOnly = true)
    fun findAll(centerId: Int, pageable: Pageable): Page<TaskDto> {
        val taskPage = taskRepository.findAllByCenterId(centerId, pageable)
        val taskDtos = taskPage.content.map(taskMapper::toDto)
        return PageImpl(taskDtos, pageable, taskPage.totalElements)
    }

    @Transactional(readOnly = true)
    fun findAll(centerId: Int, status: String?, subject: String?, pageable: Pageable): Page<TaskDto> {
        val taskPage = taskRepository.findAllByCenterIdAndStatusContainingAndSubjectContaining(
            centerId,
            status.orEmpty(),
            subject.orEmpty(),
            pageable
        )
        val taskDtos = taskPage.content.map(taskMapper::toDto)
        return PageImpl(taskDtos, pageable, taskPage.totalElements)
    }

    @Transactional(readOnly = true)
    fun findById(id: Long): TaskDto? {
        return taskRepository.findById(id).map(taskMapper::toDto).orElse(null)
    }

    fun update(id: Long, taskDto: TaskDto): TaskDto {
        val existingTask = taskRepository.findById(id).orElseThrow { RuntimeException("Task not found") }
        val updatedTask = taskMapper.toEntity(taskDto).copy(id = existingTask.id)
        return taskMapper.toDto(taskRepository.save(updatedTask))
    }

    fun delete(id: Long) {
        taskRepository.deleteById(id)
    }

    @Transactional(readOnly = true)
    fun existsByIdAndCenterId(id: Long, centerId: Int): Boolean {
        return taskRepository.existsByIdAndCenterId(id, centerId)
    }
}
