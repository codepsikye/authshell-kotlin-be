package io.cpk.be.tasks.mapper

import io.cpk.be.tasks.dto.TaskDto
import io.cpk.be.tasks.entity.Task
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

/**
 * Unit tests for TaskMapper
 */
class TaskMapperTest {

    private val taskMapper = TaskMapper()

    @Test
    fun `should map entity to dto`() {
        // Given
        val task = Task(
            id = 1L,
            subject = "Test Task",
            body = "This is a test task",
            status = "in_progress",
            centerId = 2
        )

        // When
        val taskDto = taskMapper.toDto(task)

        // Then
        assertEquals(1L, taskDto.id)
        assertEquals("Test Task", taskDto.subject)
        assertEquals("This is a test task", taskDto.body)
        assertEquals("in_progress", taskDto.status)
        assertEquals(2, taskDto.centerId)
    }

    @Test
    fun `should map entity list to dto list`() {
        // Given
        val task1 = Task(
            id = 1L,
            subject = "Task 1",
            body = "This is task 1",
            status = "pending",
            centerId = 2
        )
        val task2 = Task(
            id = 2L,
            subject = "Task 2",
            body = "This is task 2",
            status = "completed",
            centerId = 3
        )
        val tasks = listOf(task1, task2)

        // When
        val taskDtos = taskMapper.toDtoList(tasks)

        // Then
        assertEquals(2, taskDtos.size)

        assertEquals(1L, taskDtos[0].id)
        assertEquals("Task 1", taskDtos[0].subject)
        assertEquals("This is task 1", taskDtos[0].body)
        assertEquals("pending", taskDtos[0].status)
        assertEquals(2, taskDtos[0].centerId)

        assertEquals(2L, taskDtos[1].id)
        assertEquals("Task 2", taskDtos[1].subject)
        assertEquals("This is task 2", taskDtos[1].body)
        assertEquals("completed", taskDtos[1].status)
        assertEquals(3, taskDtos[1].centerId)
    }

    @Test
    fun `should handle null body correctly`() {
        // Given
        val task = Task(
            id = 1L,
            subject = "Test Task",
            body = null,
            status = "pending",
            centerId = 2
        )

        // When
        val taskDto = taskMapper.toDto(task)

        // Then
        assertEquals(1L, taskDto.id)
        assertEquals("Test Task", taskDto.subject)
        assertNull(taskDto.body)
        assertEquals("pending", taskDto.status)
        assertEquals(2, taskDto.centerId)
    }

    @Test
    fun `should map dto to entity with all non-null values`() {
        // Given
        val taskDto = TaskDto(
            id = 1L,
            subject = "Test Task",
            body = "This is a test task",
            status = "in_progress",
            centerId = 2
        )

        // When
        val task = taskMapper.toEntity(taskDto)

        // Then
        assertEquals(1L, task.id)
        assertEquals("Test Task", task.subject)
        assertEquals("This is a test task", task.body)
        assertEquals("in_progress", task.status)
        assertEquals(2, task.centerId)
        // center relationship should be null
        assertNull(task.center)
    }

    @Test
    fun `should map dto to entity with null subject`() {
        // Given
        val taskDto = TaskDto(
            id = 1L,
            subject = null,
            body = "This is a test task",
            status = "in_progress",
            centerId = 2
        )

        // When
        val task = taskMapper.toEntity(taskDto)

        // Then
        assertEquals(1L, task.id)
        assertEquals("", task.subject) // Default value for null subject
        assertEquals("This is a test task", task.body)
        assertEquals("in_progress", task.status)
        assertEquals(2, task.centerId)
    }

    @Test
    fun `should map dto to entity with null body`() {
        // Given
        val taskDto = TaskDto(
            id = 1L,
            subject = "Test Task",
            body = null,
            status = "in_progress",
            centerId = 2
        )

        // When
        val task = taskMapper.toEntity(taskDto)

        // Then
        assertEquals(1L, task.id)
        assertEquals("Test Task", task.subject)
        assertNull(task.body) // body remains null
        assertEquals("in_progress", task.status)
        assertEquals(2, task.centerId)
    }

    @Test
    fun `should map dto to entity with null status`() {
        // Given
        val taskDto = TaskDto(
            id = 1L,
            subject = "Test Task",
            body = "This is a test task",
            status = null,
            centerId = 2
        )

        // When
        val task = taskMapper.toEntity(taskDto)

        // Then
        assertEquals(1L, task.id)
        assertEquals("Test Task", task.subject)
        assertEquals("This is a test task", task.body)
        assertEquals("pending", task.status) // Default value for null status
        assertEquals(2, task.centerId)
    }

    @Test
    fun `should map dto to entity with null centerId`() {
        // Given
        val taskDto = TaskDto(
            id = 1L,
            subject = "Test Task",
            body = "This is a test task",
            status = "in_progress",
            centerId = null
        )

        // When
        val task = taskMapper.toEntity(taskDto)

        // Then
        assertEquals(1L, task.id)
        assertEquals("Test Task", task.subject)
        assertEquals("This is a test task", task.body)
        assertEquals("in_progress", task.status)
        assertEquals(0, task.centerId) // Default value for null centerId
    }

    @Test
    fun `should map dto to entity with all nullable fields set to null`() {
        // Given
        val taskDto = TaskDto(
            id = null,
            subject = null,
            body = null,
            status = null,
            centerId = null
        )

        // When
        val task = taskMapper.toEntity(taskDto)

        // Then
        assertNull(task.id)
        assertEquals("", task.subject) // Default value for null subject
        assertNull(task.body)
        assertEquals("pending", task.status) // Default value for null status
        assertEquals(0, task.centerId) // Default value for null centerId
    }

    @Test
    fun `should map dto list to entity list`() {
        // Given
        val taskDto1 = TaskDto(
            id = 1L,
            subject = "Task 1",
            body = "This is task 1",
            status = "pending",
            centerId = 2
        )
        val taskDto2 = TaskDto(
            id = 2L,
            subject = "Task 2",
            body = "This is task 2",
            status = "completed",
            centerId = 3
        )
        val taskDtos = listOf(taskDto1, taskDto2)

        // When
        val tasks = taskMapper.toEntityList(taskDtos)

        // Then
        assertEquals(2, tasks.size)

        assertEquals(1L, tasks[0].id)
        assertEquals("Task 1", tasks[0].subject)
        assertEquals("This is task 1", tasks[0].body)
        assertEquals("pending", tasks[0].status)
        assertEquals(2, tasks[0].centerId)

        assertEquals(2L, tasks[1].id)
        assertEquals("Task 2", tasks[1].subject)
        assertEquals("This is task 2", tasks[1].body)
        assertEquals("completed", tasks[1].status)
        assertEquals(3, tasks[1].centerId)
    }

    @Test
    fun `should map dto list to entity list with mixed null values`() {
        // Given
        val taskDto1 = TaskDto(
            id = 1L,
            subject = null,
            body = null,
            status = "pending",
            centerId = 2
        )
        val taskDto2 = TaskDto(
            id = 2L,
            subject = "Task 2",
            body = "This is task 2",
            status = null,
            centerId = null
        )
        val taskDtos = listOf(taskDto1, taskDto2)

        // When
        val tasks = taskMapper.toEntityList(taskDtos)

        // Then
        assertEquals(2, tasks.size)

        assertEquals(1L, tasks[0].id)
        assertEquals("", tasks[0].subject) // Default value for null subject
        assertNull(tasks[0].body)
        assertEquals("pending", tasks[0].status)
        assertEquals(2, tasks[0].centerId)

        assertEquals(2L, tasks[1].id)
        assertEquals("Task 2", tasks[1].subject)
        assertEquals("This is task 2", tasks[1].body)
        assertEquals("pending", tasks[1].status) // Default value for null status
        assertEquals(0, tasks[1].centerId) // Default value for null centerId
    }

    @Test
    fun `should map empty dto list to empty entity list`() {
        // Given
        val emptyDtoList = emptyList<TaskDto>()

        // When
        val emptyEntityList = taskMapper.toEntityList(emptyDtoList)

        // Then
        assertEquals(0, emptyEntityList.size)
    }
}