package io.cpk.be.tasks.mapper

import io.cpk.be.tasks.dto.TaskDto
import io.cpk.be.tasks.entity.Task
import org.springframework.stereotype.Component

/** Mapper for converting between Task entity and TaskDto */
@Component
class TaskMapper {
    /** Converts a Task entity to a TaskDto */
    fun toDto(entity: Task): TaskDto {
        return TaskDto(
                id = entity.id,
                subject = entity.subject,
                body = entity.body,
                status = entity.status,
                centerId = entity.centerId
        )
    }

    /**
     * Converts a TaskDto to a Task entity Note: center relationship is set to null and
     * createdAt/updatedAt are set to default values Handles nullable fields in DTO with appropriate
     * defaults for the entity
     */
    fun toEntity(dto: TaskDto): Task {
        return Task(
                id = dto.id,
                subject = dto.subject ?: "",
                body = dto.body,
                status = dto.status ?: "pending",
                centerId = dto.centerId ?: 0
        )
    }

    /** Converts a list of Task entities to a list of TaskDtos */
    fun toDtoList(entities: List<Task>): List<TaskDto> {
        return entities.map { toDto(it) }
    }

    /** Converts a list of TaskDtos to a list of Task entities */
    fun toEntityList(dtos: List<TaskDto>): List<Task> {
        return dtos.map { toEntity(it) }
    }
}
