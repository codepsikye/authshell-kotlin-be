package io.cpk.be.tasks.mapper

import io.cpk.be.tasks.dto.TaskUpdateDto
import io.cpk.be.tasks.entity.TaskUpdate
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

/**
 * Unit tests for TaskUpdateMapper
 */
class TaskUpdateMapperTest {

    private val taskUpdateMapper = TaskUpdateMapper()

    @Test
    fun `should map entity to dto`() {
        // Given
        val taskUpdate = TaskUpdate(
            id = 1L,
            taskId = 2L,
            body = "This is a task update",
            status = "in_progress"
        )

        // When
        val taskUpdateDto = taskUpdateMapper.toDto(taskUpdate)

        // Then
        assertEquals(1L, taskUpdateDto.id)
        assertEquals(2L, taskUpdateDto.taskId)
        assertEquals("This is a task update", taskUpdateDto.body)
        assertEquals("in_progress", taskUpdateDto.status)
    }

    @Test
    fun `should map entity list to dto list`() {
        // Given
        val taskUpdate1 = TaskUpdate(
            id = 1L,
            taskId = 2L,
            body = "Update 1",
            status = "pending"
        )
        val taskUpdate2 = TaskUpdate(
            id = 3L,
            taskId = 2L,
            body = "Update 2",
            status = "completed"
        )
        val taskUpdates = listOf(taskUpdate1, taskUpdate2)

        // When
        val taskUpdateDtos = taskUpdateMapper.toDtoList(taskUpdates)

        // Then
        assertEquals(2, taskUpdateDtos.size)

        assertEquals(1L, taskUpdateDtos[0].id)
        assertEquals(2L, taskUpdateDtos[0].taskId)
        assertEquals("Update 1", taskUpdateDtos[0].body)
        assertEquals("pending", taskUpdateDtos[0].status)

        assertEquals(3L, taskUpdateDtos[1].id)
        assertEquals(2L, taskUpdateDtos[1].taskId)
        assertEquals("Update 2", taskUpdateDtos[1].body)
        assertEquals("completed", taskUpdateDtos[1].status)
    }

    @Test
    fun `should handle null body correctly`() {
        // Given
        val taskUpdate = TaskUpdate(
            id = 1L,
            taskId = 2L,
            body = null,
            status = "pending"
        )

        // When
        val taskUpdateDto = taskUpdateMapper.toDto(taskUpdate)

        // Then
        assertEquals(1L, taskUpdateDto.id)
        assertEquals(2L, taskUpdateDto.taskId)
        assertNull(taskUpdateDto.body)
        assertEquals("pending", taskUpdateDto.status)
    }

    @Test
    fun `should map dto to entity with all non-null values`() {
        // Given
        val taskUpdateDto = TaskUpdateDto(
            id = 1L,
            taskId = 2L,
            body = "This is a task update",
            status = "in_progress"
        )

        // When
        val taskUpdate = taskUpdateMapper.toEntity(taskUpdateDto)

        // Then
        assertEquals(1L, taskUpdate.id)
        assertEquals(2L, taskUpdate.taskId)
        assertEquals("This is a task update", taskUpdate.body)
        assertEquals("in_progress", taskUpdate.status)
        // task relationship should be null
        assertNull(taskUpdate.task)
    }

    @Test
    fun `should map dto to entity with null taskId`() {
        // Given
        val taskUpdateDto = TaskUpdateDto(
            id = 1L,
            taskId = null,
            body = "This is a task update",
            status = "in_progress"
        )

        // When
        val taskUpdate = taskUpdateMapper.toEntity(taskUpdateDto)

        // Then
        assertEquals(1L, taskUpdate.id)
        assertEquals(0L, taskUpdate.taskId) // Default value for null taskId
        assertEquals("This is a task update", taskUpdate.body)
        assertEquals("in_progress", taskUpdate.status)
    }

    @Test
    fun `should map dto to entity with null status`() {
        // Given
        val taskUpdateDto = TaskUpdateDto(
            id = 1L,
            taskId = 2L,
            body = "This is a task update",
            status = null
        )

        // When
        val taskUpdate = taskUpdateMapper.toEntity(taskUpdateDto)

        // Then
        assertEquals(1L, taskUpdate.id)
        assertEquals(2L, taskUpdate.taskId)
        assertEquals("This is a task update", taskUpdate.body)
        assertEquals("", taskUpdate.status) // Default value for null status
    }

    @Test
    fun `should map dto to entity with null body`() {
        // Given
        val taskUpdateDto = TaskUpdateDto(
            id = 1L,
            taskId = 2L,
            body = null,
            status = "in_progress"
        )

        // When
        val taskUpdate = taskUpdateMapper.toEntity(taskUpdateDto)

        // Then
        assertEquals(1L, taskUpdate.id)
        assertEquals(2L, taskUpdate.taskId)
        assertNull(taskUpdate.body) // body remains null
        assertEquals("in_progress", taskUpdate.status)
    }

    @Test
    fun `should map dto to entity with all nullable fields set to null`() {
        // Given
        val taskUpdateDto = TaskUpdateDto(
            id = null,
            taskId = null,
            body = null,
            status = null
        )

        // When
        val taskUpdate = taskUpdateMapper.toEntity(taskUpdateDto)

        // Then
        assertNull(taskUpdate.id)
        assertEquals(0L, taskUpdate.taskId) // Default value for null taskId
        assertNull(taskUpdate.body)
        assertEquals("", taskUpdate.status) // Default value for null status
    }

    @Test
    fun `should map dto list to entity list`() {
        // Given
        val taskUpdateDto1 = TaskUpdateDto(
            id = 1L,
            taskId = 2L,
            body = "Update 1",
            status = "pending"
        )
        val taskUpdateDto2 = TaskUpdateDto(
            id = 3L,
            taskId = 2L,
            body = "Update 2",
            status = "completed"
        )
        val taskUpdateDtos = listOf(taskUpdateDto1, taskUpdateDto2)

        // When
        val taskUpdates = taskUpdateMapper.toEntityList(taskUpdateDtos)

        // Then
        assertEquals(2, taskUpdates.size)

        assertEquals(1L, taskUpdates[0].id)
        assertEquals(2L, taskUpdates[0].taskId)
        assertEquals("Update 1", taskUpdates[0].body)
        assertEquals("pending", taskUpdates[0].status)

        assertEquals(3L, taskUpdates[1].id)
        assertEquals(2L, taskUpdates[1].taskId)
        assertEquals("Update 2", taskUpdates[1].body)
        assertEquals("completed", taskUpdates[1].status)
    }

    @Test
    fun `should map dto list to entity list with mixed null values`() {
        // Given
        val taskUpdateDto1 = TaskUpdateDto(
            id = 1L,
            taskId = 2L,
            body = null,
            status = "pending"
        )
        val taskUpdateDto2 = TaskUpdateDto(
            id = 3L,
            taskId = null,
            body = "Update 2",
            status = null
        )
        val taskUpdateDtos = listOf(taskUpdateDto1, taskUpdateDto2)

        // When
        val taskUpdates = taskUpdateMapper.toEntityList(taskUpdateDtos)

        // Then
        assertEquals(2, taskUpdates.size)

        assertEquals(1L, taskUpdates[0].id)
        assertEquals(2L, taskUpdates[0].taskId)
        assertNull(taskUpdates[0].body)
        assertEquals("pending", taskUpdates[0].status)

        assertEquals(3L, taskUpdates[1].id)
        assertEquals(0L, taskUpdates[1].taskId) // Default value for null taskId
        assertEquals("Update 2", taskUpdates[1].body)
        assertEquals("", taskUpdates[1].status) // Default value for null status
    }

    @Test
    fun `should map empty dto list to empty entity list`() {
        // Given
        val emptyDtoList = emptyList<TaskUpdateDto>()

        // When
        val emptyEntityList = taskUpdateMapper.toEntityList(emptyDtoList)

        // Then
        assertEquals(0, emptyEntityList.size)
        assertEquals(emptyList(), emptyEntityList)
    }
}