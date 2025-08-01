package io.cpk.be.tasks.service

import io.cpk.be.tasks.dto.TaskDto
import io.cpk.be.tasks.entity.Task
import io.cpk.be.tasks.mapper.TaskMapper
import io.cpk.be.tasks.repository.TaskRepository
import io.mockk.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import java.time.LocalDateTime
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Unit tests for TaskService
 */
class TaskServiceTest {

    private val taskRepository = mockk<TaskRepository>()
    private val taskMapper = mockk<TaskMapper>()

    private lateinit var taskService: TaskService
    private lateinit var task: Task
    private lateinit var taskDto: TaskDto

    @BeforeEach
    fun setUp() {
        taskService = TaskService(taskRepository, taskMapper)

        val now = LocalDateTime.now()
        
        task = Task(
            id = 1L,
            subject = "Test Task",
            body = "This is a test task",
            status = "pending",
            centerId = 1
        )

        taskDto = TaskDto(
            id = 1L,
            subject = "Test Task",
            body = "This is a test task",
            status = "pending",
            centerId = 1
        )
    }

    @Test
    fun `should create task successfully`() {
        // Given
        val createDto = TaskDto(
            subject = "New Task",
            body = "This is a new task",
            status = "pending",
            centerId = 2
        )
        val entityToSave = Task(
            subject = "New Task",
            body = "This is a new task",
            status = "pending",
            centerId = 2
        )
        val savedEntity = Task(
            id = 2L,
            subject = "New Task",
            body = "This is a new task",
            status = "pending",
            centerId = 2
        )
        val resultDto = TaskDto(
            id = 2L,
            subject = "New Task",
            body = "This is a new task",
            status = "pending",
            centerId = 2
        )

        every { taskMapper.toEntity(createDto) } returns entityToSave
        every { taskRepository.save(entityToSave) } returns savedEntity
        every { taskMapper.toDto(savedEntity) } returns resultDto

        // When
        val result = taskService.create(createDto)

        // Then
        assertEquals(resultDto, result)
        verify { taskMapper.toEntity(createDto) }
        verify { taskRepository.save(entityToSave) }
        verify { taskMapper.toDto(savedEntity) }
    }

    @Test
    fun `should find all tasks`() {
        // Given
        val tasks = listOf(task)
        val taskDtos = listOf(taskDto)

        every { taskRepository.findAll() } returns tasks
        every { taskMapper.toDto(task) } returns taskDto

        // When
        val result = taskService.findAll()

        // Then
        assertEquals(taskDtos, result)
        verify { taskRepository.findAll() }
        verify { taskMapper.toDto(task) }
    }

    @Test
    fun `should find all tasks with pagination`() {
        // Given
        val pageable = PageRequest.of(0, 10)
        val tasks = listOf(task)
        val taskDtos = listOf(taskDto)
        val page = PageImpl(tasks, pageable, tasks.size.toLong())

        every { taskRepository.findAll(pageable) } returns page
        every { taskMapper.toDto(task) } returns taskDto

        // When
        val result = taskService.findAll(pageable)

        // Then
        assertEquals(taskDtos, result.content)
        assertEquals(1, result.totalElements)
        verify { taskRepository.findAll(pageable) }
        verify { taskMapper.toDto(task) }
    }

    @Test
    fun `should find all tasks by centerId with pagination`() {
        // Given
        val centerId = 1
        val pageable = PageRequest.of(0, 10)
        val tasks = listOf(task)
        val taskDtos = listOf(taskDto)
        val page = PageImpl(tasks, pageable, tasks.size.toLong())

        every { taskRepository.findAllByCenterId(centerId, pageable) } returns page
        every { taskMapper.toDto(task) } returns taskDto

        // When
        val result = taskService.findAll(centerId, pageable)

        // Then
        assertEquals(taskDtos, result.content)
        assertEquals(1, result.totalElements)
        verify { taskRepository.findAllByCenterId(centerId, pageable) }
        verify { taskMapper.toDto(task) }
    }

    @Test
    fun `should find all tasks by centerId, status, and subject with pagination`() {
        // Given
        val centerId = 1
        val status = "pending"
        val subject = "Test"
        val pageable = PageRequest.of(0, 10)
        val tasks = listOf(task)
        val taskDtos = listOf(taskDto)
        val page = PageImpl(tasks, pageable, tasks.size.toLong())

        every { 
            taskRepository.findAllByCenterIdAndStatusContainingAndSubjectContaining(
                centerId, status, subject, pageable
            ) 
        } returns page
        every { taskMapper.toDto(task) } returns taskDto

        // When
        val result = taskService.findAll(centerId, status, subject, pageable)

        // Then
        assertEquals(taskDtos, result.content)
        assertEquals(1, result.totalElements)
        verify { 
            taskRepository.findAllByCenterIdAndStatusContainingAndSubjectContaining(
                centerId, status, subject, pageable
            ) 
        }
        verify { taskMapper.toDto(task) }
    }

    @Test
    fun `should handle null status and subject in findAll with centerId, status, and subject parameters`() {
        // Given
        val centerId = 1
        val status: String? = null
        val subject: String? = null
        val pageable = PageRequest.of(0, 10)
        val tasks = listOf(task)
        val taskDtos = listOf(taskDto)
        val page = PageImpl(tasks, pageable, tasks.size.toLong())

        every { 
            taskRepository.findAllByCenterIdAndStatusContainingAndSubjectContaining(
                centerId, "", "", pageable
            ) 
        } returns page
        every { taskMapper.toDto(task) } returns taskDto

        // When
        val result = taskService.findAll(centerId, status, subject, pageable)

        // Then
        assertEquals(taskDtos, result.content)
        assertEquals(1, result.totalElements)
        verify { 
            taskRepository.findAllByCenterIdAndStatusContainingAndSubjectContaining(
                centerId, "", "", pageable
            ) 
        }
        verify { taskMapper.toDto(task) }
    }

    @Test
    fun `should find task by id successfully`() {
        // Given
        val id = 1L
        every { taskRepository.findById(id) } returns Optional.of(task)
        every { taskMapper.toDto(task) } returns taskDto

        // When
        val result = taskService.findById(id)

        // Then
        assertEquals(taskDto, result)
        verify { taskRepository.findById(id) }
        verify { taskMapper.toDto(task) }
    }

    @Test
    fun `should return null when task not found by id`() {
        // Given
        val id = 999L
        every { taskRepository.findById(id) } returns Optional.empty()

        // When
        val result = taskService.findById(id)

        // Then
        assertNull(result)
        verify { taskRepository.findById(id) }
        verify(exactly = 0) { taskMapper.toDto(any()) }
    }

    @Test
    fun `should update task successfully`() {
        // Given
        val id = 1L
        val updateDto = TaskDto(
            id = id,
            subject = "Updated Task",
            body = "This is an updated task",
            status = "completed",
            centerId = 1
        )
        val existingEntity = task
        val entityToUpdate = Task(
            id = null, // This will be replaced with the existing id
            subject = "Updated Task",
            body = "This is an updated task",
            status = "completed",
            centerId = 1
        )
        val updatedEntity = Task(
            id = id,
            subject = "Updated Task",
            body = "This is an updated task",
            status = "completed",
            centerId = 1
        )
        val resultDto = TaskDto(
            id = id,
            subject = "Updated Task",
            body = "This is an updated task",
            status = "completed",
            centerId = 1
        )

        every { taskRepository.findById(id) } returns Optional.of(existingEntity)
        every { taskMapper.toEntity(updateDto) } returns entityToUpdate
        every { taskRepository.save(any()) } returns updatedEntity
        every { taskMapper.toDto(updatedEntity) } returns resultDto

        // When
        val result = taskService.update(id, updateDto)

        // Then
        assertEquals(resultDto, result)
        verify { taskRepository.findById(id) }
        verify { taskMapper.toEntity(updateDto) }
        verify { taskRepository.save(any()) }
        verify { taskMapper.toDto(updatedEntity) }
    }

    @Test
    fun `should throw exception when updating non-existent task`() {
        // Given
        val id = 999L
        val updateDto = TaskDto(
            id = id,
            subject = "Updated Task",
            body = "This is an updated task",
            status = "completed",
            centerId = 1
        )
        every { taskRepository.findById(id) } returns Optional.empty()

        // When & Then
        val exception = assertThrows<RuntimeException> {
            taskService.update(id, updateDto)
        }
        assertEquals("Task not found", exception.message)
        verify { taskRepository.findById(id) }
        verify(exactly = 0) { taskRepository.save(any()) }
    }

    @Test
    fun `should delete task successfully`() {
        // Given
        val id = 1L
        every { taskRepository.deleteById(id) } just runs

        // When
        taskService.delete(id)

        // Then
        verify { taskRepository.deleteById(id) }
    }

    @Test
    fun `should return true when task exists by id and centerId`() {
        // Given
        val id = 1L
        val centerId = 1
        every { taskRepository.existsByIdAndCenterId(id, centerId) } returns true

        // When
        val result = taskService.existsByIdAndCenterId(id, centerId)

        // Then
        assertTrue(result)
        verify { taskRepository.existsByIdAndCenterId(id, centerId) }
    }

    @Test
    fun `should return false when task does not exist by id and centerId`() {
        // Given
        val id = 999L
        val centerId = 999
        every { taskRepository.existsByIdAndCenterId(id, centerId) } returns false

        // When
        val result = taskService.existsByIdAndCenterId(id, centerId)

        // Then
        assertFalse(result)
        verify { taskRepository.existsByIdAndCenterId(id, centerId) }
    }
}