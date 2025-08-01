package io.cpk.be.tasks.mapper

import io.cpk.be.tasks.dto.TaskUpdateDto
import io.cpk.be.tasks.entity.TaskUpdate
import org.springframework.stereotype.Component

/** Mapper for converting between TaskUpdate entity and TaskUpdateDto */
@Component
class TaskUpdateMapper {
    /** Converts a TaskUpdate entity to a TaskUpdateDto */
    fun toDto(entity: TaskUpdate): TaskUpdateDto {
        return TaskUpdateDto(
                id = entity.id,
                taskId = entity.taskId,
                body = entity.body,
                status = entity.status
        )
    }

    /**
     * Converts a TaskUpdateDto to a TaskUpdate entity Note: task relationship is set to null and
     * createdAt/updatedAt are set to default values Handles nullable fields in DTO with appropriate
     * defaults for the entity
     */
    fun toEntity(dto: TaskUpdateDto): TaskUpdate {
        return TaskUpdate(
                id = dto.id,
                taskId = dto.taskId ?: 0,
                body = dto.body,
                status = dto.status ?: ""
        )
    }

    /** Converts a list of TaskUpdate entities to a list of TaskUpdateDtos */
    fun toDtoList(entities: List<TaskUpdate>): List<TaskUpdateDto> {
        return entities.map { toDto(it) }
    }

    /** Converts a list of TaskUpdateDtos to a list of TaskUpdate entities */
    fun toEntityList(dtos: List<TaskUpdateDto>): List<TaskUpdate> {
        return dtos.map { toEntity(it) }
    }
}
