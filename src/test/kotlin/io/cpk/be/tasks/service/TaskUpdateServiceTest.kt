package io.cpk.be.tasks.service

import io.cpk.be.tasks.dto.TaskUpdateDto
import io.cpk.be.tasks.entity.Task
import io.cpk.be.tasks.entity.TaskUpdate
import io.cpk.be.tasks.mapper.TaskUpdateMapper
import io.cpk.be.tasks.repository.TaskUpdateRepository
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
 * Unit tests for TaskUpdateService
 */
class TaskUpdateServiceTest {

    private val taskUpdateRepository = mockk<TaskUpdateRepository>()
    private val taskUpdateMapper = mockk<TaskUpdateMapper>()

    private lateinit var taskUpdateService: TaskUpdateService
    private lateinit var taskUpdate: TaskUpdate
    private lateinit var taskUpdateDto: TaskUpdateDto
    private lateinit var task: Task

    @BeforeEach
    fun setUp() {
        taskUpdateService = TaskUpdateService(taskUpdateRepository, taskUpdateMapper)

        val now = LocalDateTime.now()

        task = Task(
            id = 1L,
            subject = "Test Task",
            centerId = 1
        )

        taskUpdate = TaskUpdate(
            id = 1L,
            taskId = 1L,
            body = "This is a test update",
            status = "completed",
            task = task
        )

        taskUpdateDto = TaskUpdateDto(
            id = 1L,
            taskId = 1L,
            body = "This is a test update",
            status = "completed"
        )
    }

    @Test
    fun `should create task update successfully`() {
        // Given
        val createDto = TaskUpdateDto(
            taskId = 1L,
            body = "New update",
            status = "in_progress"
        )
        val entityToSave = TaskUpdate(
            taskId = 1L,
            body = "New update",
            status = "in_progress"
        )
        val savedEntity = TaskUpdate(
            id = 2L,
            taskId = 1L,
            body = "New update",
            status = "in_progress"
        )
        val resultDto = TaskUpdateDto(
            id = 2L,
            taskId = 1L,
            body = "New update",
            status = "in_progress"
        )

        every { taskUpdateMapper.toEntity(createDto) } returns entityToSave
        every { taskUpdateRepository.save(entityToSave) } returns savedEntity
        every { taskUpdateMapper.toDto(savedEntity) } returns resultDto

        // When
        val result = taskUpdateService.create(createDto)

        // Then
        assertEquals(resultDto, result)
        verify { taskUpdateMapper.toEntity(createDto) }
        verify { taskUpdateRepository.save(entityToSave) }
        verify { taskUpdateMapper.toDto(savedEntity) }
    }

    @Test
    fun `should find all task updates`() {
        // Given
        val taskUpdates = listOf(taskUpdate)
        val taskUpdateDtos = listOf(taskUpdateDto)

        every { taskUpdateRepository.findAll() } returns taskUpdates
        every { taskUpdateMapper.toDto(taskUpdate) } returns taskUpdateDto

        // When
        val result = taskUpdateService.findAll()

        // Then
        assertEquals(taskUpdateDtos, result)
        verify { taskUpdateRepository.findAll() }
        verify { taskUpdateMapper.toDto(taskUpdate) }
    }

    @Test
    fun `should find all task updates with pagination`() {
        // Given
        val pageable = PageRequest.of(0, 10)
        val taskUpdates = listOf(taskUpdate)
        val taskUpdateDtos = listOf(taskUpdateDto)
        val page = PageImpl(taskUpdates, pageable, taskUpdates.size.toLong())

        every { taskUpdateRepository.findAll(pageable) } returns page
        every { taskUpdateMapper.toDto(taskUpdate) } returns taskUpdateDto

        // When
        val result = taskUpdateService.findAll(pageable)

        // Then
        assertEquals(taskUpdateDtos, result.content)
        assertEquals(1, result.totalElements)
        verify { taskUpdateRepository.findAll(pageable) }
        verify { taskUpdateMapper.toDto(taskUpdate) }
    }

    @Test
    fun `should find all task updates by centerId with pagination`() {
        // Given
        val centerId = 1
        val pageable = PageRequest.of(0, 10)
        val taskUpdates = listOf(taskUpdate)
        val taskUpdateDtos = listOf(taskUpdateDto)
        val page = PageImpl(taskUpdates, pageable, taskUpdates.size.toLong())

        every { taskUpdateRepository.findAllByTaskCenterId(centerId, pageable) } returns page
        every { taskUpdateMapper.toDto(taskUpdate) } returns taskUpdateDto

        // When
        val result = taskUpdateService.findAll(centerId, pageable)

        // Then
        assertEquals(taskUpdateDtos, result.content)
        assertEquals(1, result.totalElements)
        verify { taskUpdateRepository.findAllByTaskCenterId(centerId, pageable) }
        verify { taskUpdateMapper.toDto(taskUpdate) }
    }

    @Test
    fun `should find task update by id successfully`() {
        // Given
        val id = 1L
        every { taskUpdateRepository.findById(id) } returns Optional.of(taskUpdate)
        every { taskUpdateMapper.toDto(taskUpdate) } returns taskUpdateDto

        // When
        val result = taskUpdateService.findById(id)

        // Then
        assertEquals(taskUpdateDto, result)
        verify { taskUpdateRepository.findById(id) }
        verify { taskUpdateMapper.toDto(taskUpdate) }
    }

    @Test
    fun `should return null when task update not found by id`() {
        // Given
        val id = 999L
        every { taskUpdateRepository.findById(id) } returns Optional.empty()

        // When
        val result = taskUpdateService.findById(id)

        // Then
        assertNull(result)
        verify { taskUpdateRepository.findById(id) }
        verify(exactly = 0) { taskUpdateMapper.toDto(any()) }
    }

    @Test
    fun `should return true when task update exists by id and task centerId`() {
        // Given
        val id = 1L
        val centerId = 1
        every { taskUpdateRepository.existsByIdAndTaskCenterId(id, centerId) } returns true

        // When
        val result = taskUpdateService.existsByIdAndTaskCenterId(id, centerId)

        // Then
        assertTrue(result)
        verify { taskUpdateRepository.existsByIdAndTaskCenterId(id, centerId) }
    }

    @Test
    fun `should return false when task update does not exist by id and task centerId`() {
        // Given
        val id = 999L
        val centerId = 999
        every { taskUpdateRepository.existsByIdAndTaskCenterId(id, centerId) } returns false

        // When
        val result = taskUpdateService.existsByIdAndTaskCenterId(id, centerId)

        // Then
        assertFalse(result)
        verify { taskUpdateRepository.existsByIdAndTaskCenterId(id, centerId) }
    }

    @Test
    fun `should update task update successfully`() {
        // Given
        val id = 1L
        val updateDto = TaskUpdateDto(
            id = id,
            taskId = 1L,
            body = "Updated body",
            status = "resolved"
        )
        val existingEntity = taskUpdate
        val entityToUpdate = TaskUpdate(
            id = null, // This will be replaced with the existing id
            taskId = 1L,
            body = "Updated body",
            status = "resolved"
        )
        val updatedEntity = TaskUpdate(
            id = id,
            taskId = 1L,
            body = "Updated body",
            status = "resolved"
        )
        val resultDto = TaskUpdateDto(
            id = id,
            taskId = 1L,
            body = "Updated body",
            status = "resolved"
        )

        every { taskUpdateRepository.findById(id) } returns Optional.of(existingEntity)
        every { taskUpdateMapper.toEntity(updateDto) } returns entityToUpdate
        every { taskUpdateRepository.save(any()) } returns updatedEntity
        every { taskUpdateMapper.toDto(updatedEntity) } returns resultDto

        // When
        val result = taskUpdateService.update(id, updateDto)

        // Then
        assertEquals(resultDto, result)
        verify { taskUpdateRepository.findById(id) }
        verify { taskUpdateMapper.toEntity(updateDto) }
        verify { taskUpdateRepository.save(any()) }
        verify { taskUpdateMapper.toDto(updatedEntity) }
    }

    @Test
    fun `should throw exception when updating non-existent task update`() {
        // Given
        val id = 999L
        val updateDto = TaskUpdateDto(
            id = id,
            taskId = 1L,
            body = "Updated body",
            status = "resolved"
        )
        every { taskUpdateRepository.findById(id) } returns Optional.empty()

        // When & Then
        val exception = assertThrows<RuntimeException> {
            taskUpdateService.update(id, updateDto)
        }
        assertEquals("TaskUpdate not found", exception.message)
        verify { taskUpdateRepository.findById(id) }
        verify(exactly = 0) { taskUpdateRepository.save(any()) }
    }

    @Test
    fun `should delete task update successfully`() {
        // Given
        val id = 1L
        every { taskUpdateRepository.deleteById(id) } just runs

        // When
        taskUpdateService.delete(id)

        // Then
        verify { taskUpdateRepository.deleteById(id) }
    }
}