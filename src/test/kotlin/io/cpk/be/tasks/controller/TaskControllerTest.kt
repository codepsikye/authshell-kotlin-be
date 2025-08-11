package io.cpk.be.tasks.controller

import io.cpk.be.security.CustomUserDetails
import io.cpk.be.tasks.dto.TaskDto
import io.cpk.be.tasks.service.TaskService
import io.mockk.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.http.HttpStatus
import org.springframework.security.core.authority.SimpleGrantedAuthority
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class TaskControllerTest {

    private val taskService = mockk<TaskService>()
    private lateinit var taskController: TaskController
    private lateinit var userDetails: CustomUserDetails

    @BeforeEach
    fun setUp() {
        taskController = TaskController(taskService)
        
        // Create mock user details with centerId = 1
        val authorities = listOf(
            SimpleGrantedAuthority("task_create"),
            SimpleGrantedAuthority("task_read"),
            SimpleGrantedAuthority("task_edit"),
            SimpleGrantedAuthority("task_remove")
        )
        userDetails = CustomUserDetails(
            username = "testuser",
            password = "password",
            authorities = authorities,
            orgId = 1,
            centerId = 1,
            id = 1
        )
    }

    @Test
    fun `should create task successfully`() {
        // Given
        val taskDto = TaskDto(
            subject = "Test Task",
            body = "This is a test task",
            status = "pending",
            centerId = 1
        )
        
        val createdTaskDto = TaskDto(
            id = 1L,
            subject = "Test Task",
            body = "This is a test task",
            status = "pending",
            centerId = 1
        )
        
        every { taskService.create(taskDto) } returns createdTaskDto
        
        // When
        val response = taskController.create(taskDto, userDetails)
        
        // Then
        assertEquals(HttpStatus.CREATED, response.statusCode)
        assertNotNull(response.body)
        assertEquals(1L, response.body?.id)
        assertEquals("Test Task", response.body?.subject)
        assertEquals("This is a test task", response.body?.body)
        assertEquals("pending", response.body?.status)
        assertEquals(1, response.body?.centerId)
        
        verify { taskService.create(taskDto) }
    }
    
    @Test
    fun `should return forbidden when centerId is null`() {
        // Given
        val taskDto = TaskDto(
            subject = "Test Task",
            body = "This is a test task",
            status = "pending",
            centerId = 1
        )
        
        // Create user details with null centerId
        val authorities = listOf(SimpleGrantedAuthority("task_create"))
        val userDetailsWithNullCenter = CustomUserDetails(
            username = "testuser",
            password = "password",
            authorities = authorities,
            orgId = 1,
            centerId = null,
            id = 1
        )
        
        // When
        val response = taskController.create(taskDto, userDetailsWithNullCenter)
        
        // Then
        assertEquals(HttpStatus.FORBIDDEN, response.statusCode)
        assertNull(response.body)
        
        verify(exactly = 0) { taskService.create(any()) }
    }
    
    @Test
    fun `should return forbidden when task centerId doesn't match user centerId`() {
        // Given
        val taskDto = TaskDto(
            subject = "Test Task",
            body = "This is a test task",
            status = "pending",
            centerId = 2  // Different from user's centerId (1)
        )
        
        // When
        val response = taskController.create(taskDto, userDetails)
        
        // Then
        assertEquals(HttpStatus.FORBIDDEN, response.statusCode)
        assertNull(response.body)
        
        verify(exactly = 0) { taskService.create(any()) }
    }
    
    @Test
    fun `should find all tasks successfully`() {
        // Given
        val page = 0
        val size = 10
        val status: String? = null
        val subject: String? = null
        val pageable = PageRequest.of(page, size)
        
        val taskDto1 = TaskDto(id = 1L, subject = "Task 1", status = "pending", centerId = 1)
        val taskDto2 = TaskDto(id = 2L, subject = "Task 2", status = "in-progress", centerId = 1)
        val tasks = listOf(taskDto1, taskDto2)
        
        val pageResult = PageImpl(tasks, pageable, tasks.size.toLong())
        
        every { taskService.findAll(1, status, subject, pageable) } returns pageResult
        
        // When
        val response = taskController.findAll(page, size, status, subject, userDetails)
        
        // Then
        assertEquals(HttpStatus.OK, response.statusCode)
        assertNotNull(response.body)
        assertEquals(2, response.body?.content?.size)
        assertEquals(2, response.body?.totalElements)
        assertEquals("Task 1", response.body?.content?.get(0)?.subject)
        assertEquals("Task 2", response.body?.content?.get(1)?.subject)
        
        verify { taskService.findAll(1, status, subject, pageable) }
    }
    
    @Test
    fun `should return forbidden when finding all tasks with null centerId`() {
        // Given
        val page = 0
        val size = 10
        val status: String? = null
        val subject: String? = null
        
        // Create user details with null centerId
        val authorities = listOf(SimpleGrantedAuthority("task_read"))
        val userDetailsWithNullCenter = CustomUserDetails(
            username = "testuser",
            password = "password",
            authorities = authorities,
            orgId = 1,
            centerId = null,
            id = 1
        )
        
        // When
        val response = taskController.findAll(page, size, status, subject, userDetailsWithNullCenter)
        
        // Then
        assertEquals(HttpStatus.FORBIDDEN, response.statusCode)
        assertNull(response.body)
        
        verify(exactly = 0) { taskService.findAll(any(), any(), any(), any()) }
    }
    
    @Test
    fun `should find task by id successfully`() {
        // Given
        val taskId = 1L
        val taskDto = TaskDto(
            id = taskId,
            subject = "Test Task",
            body = "This is a test task",
            status = "pending",
            centerId = 1
        )
        
        every { taskService.findById(taskId) } returns taskDto
        
        // When
        val response = taskController.findById(taskId, userDetails)
        
        // Then
        assertEquals(HttpStatus.OK, response.statusCode)
        assertNotNull(response.body)
        assertEquals(taskId, response.body?.id)
        assertEquals("Test Task", response.body?.subject)
        assertEquals("This is a test task", response.body?.body)
        assertEquals("pending", response.body?.status)
        assertEquals(1, response.body?.centerId)
        
        verify { taskService.findById(taskId) }
    }
    
    @Test
    fun `should return forbidden when finding task by id with null centerId`() {
        // Given
        val taskId = 1L
        
        // Create user details with null centerId
        val authorities = listOf(SimpleGrantedAuthority("task_read"))
        val userDetailsWithNullCenter = CustomUserDetails(
            username = "testuser",
            password = "password",
            authorities = authorities,
            orgId = 1,
            centerId = null,
            id = 1
        )
        
        // Need to mock findById because the controller calls it before checking centerId
        val taskDto = TaskDto(
            id = taskId,
            subject = "Test Task",
            body = "This is a test task",
            status = "pending",
            centerId = 1
        )
        every { taskService.findById(taskId) } returns taskDto
        
        // When
        val response = taskController.findById(taskId, userDetailsWithNullCenter)
        
        // Then
        assertEquals(HttpStatus.FORBIDDEN, response.statusCode)
        assertNull(response.body)
        
        verify { taskService.findById(taskId) }
    }
    
    @Test
    fun `should return not found when task does not exist`() {
        // Given
        val taskId = 999L
        
        every { taskService.findById(taskId) } returns null
        
        // When
        val response = taskController.findById(taskId, userDetails)
        
        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertNull(response.body)
        
        verify { taskService.findById(taskId) }
    }
    
    @Test
    fun `should return not found when task centerId doesn't match user centerId`() {
        // Given
        val taskId = 1L
        val taskDto = TaskDto(
            id = taskId,
            subject = "Test Task",
            body = "This is a test task",
            status = "pending",
            centerId = 2  // Different from user's centerId (1)
        )
        
        every { taskService.findById(taskId) } returns taskDto
        
        // When
        val response = taskController.findById(taskId, userDetails)
        
        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertNull(response.body)
        
        verify { taskService.findById(taskId) }
    }
    
    @Test
    fun `should update task successfully`() {
        // Given
        val taskId = 1L
        val taskDto = TaskDto(
            id = taskId,
            subject = "Updated Task",
            body = "This is an updated task",
            status = "in-progress",
            centerId = 1
        )
        
        val existingTaskDto = TaskDto(
            id = taskId,
            subject = "Test Task",
            body = "This is a test task",
            status = "pending",
            centerId = 1
        )
        
        val updatedTaskDto = TaskDto(
            id = taskId,
            subject = "Updated Task",
            body = "This is an updated task",
            status = "in-progress",
            centerId = 1
        )
        
        every { taskService.findById(taskId) } returns existingTaskDto
        every { taskService.update(taskId, taskDto) } returns updatedTaskDto
        
        // When
        val response = taskController.update(taskId, taskDto, userDetails)
        
        // Then
        assertEquals(HttpStatus.OK, response.statusCode)
        assertNotNull(response.body)
        assertEquals(taskId, response.body?.id)
        assertEquals("Updated Task", response.body?.subject)
        assertEquals("This is an updated task", response.body?.body)
        assertEquals("in-progress", response.body?.status)
        assertEquals(1, response.body?.centerId)
        
        verify { taskService.findById(taskId) }
        verify { taskService.update(taskId, taskDto) }
    }
    
    @Test
    fun `should return forbidden when updating task with null centerId`() {
        // Given
        val taskId = 1L
        val taskDto = TaskDto(
            id = taskId,
            subject = "Updated Task",
            body = "This is an updated task",
            status = "in-progress",
            centerId = 1
        )
        
        // Create user details with null centerId
        val authorities = listOf(SimpleGrantedAuthority("task_edit"))
        val userDetailsWithNullCenter = CustomUserDetails(
            username = "testuser",
            password = "password",
            authorities = authorities,
            orgId = 1,
            centerId = null,
            id = 1
        )
        
        // Need to mock findById because the controller calls it before checking centerId
        val existingTaskDto = TaskDto(
            id = taskId,
            subject = "Test Task",
            body = "This is a test task",
            status = "pending",
            centerId = 1
        )
        every { taskService.findById(taskId) } returns existingTaskDto
        
        // When
        val response = taskController.update(taskId, taskDto, userDetailsWithNullCenter)
        
        // Then
        assertEquals(HttpStatus.FORBIDDEN, response.statusCode)
        assertNull(response.body)
        
        verify { taskService.findById(taskId) }
        verify(exactly = 0) { taskService.update(any(), any()) }
    }
    
    @Test
    fun `should return not found when updating non-existent task`() {
        // Given
        val taskId = 999L
        val taskDto = TaskDto(
            id = taskId,
            subject = "Updated Task",
            body = "This is an updated task",
            status = "in-progress",
            centerId = 1
        )
        
        every { taskService.findById(taskId) } returns null
        
        // When
        val response = taskController.update(taskId, taskDto, userDetails)
        
        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertNull(response.body)
        
        verify { taskService.findById(taskId) }
        verify(exactly = 0) { taskService.update(any(), any()) }
    }
    
    @Test
    fun `should return not found when updating task with mismatched centerId`() {
        // Given
        val taskId = 1L
        val taskDto = TaskDto(
            id = taskId,
            subject = "Updated Task",
            body = "This is an updated task",
            status = "in-progress",
            centerId = 1
        )
        
        val existingTaskDto = TaskDto(
            id = taskId,
            subject = "Test Task",
            body = "This is a test task",
            status = "pending",
            centerId = 2  // Different from user's centerId (1)
        )
        
        every { taskService.findById(taskId) } returns existingTaskDto
        
        // When
        val response = taskController.update(taskId, taskDto, userDetails)
        
        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertNull(response.body)
        
        verify { taskService.findById(taskId) }
        verify(exactly = 0) { taskService.update(any(), any()) }
    }
    
    @Test
    fun `should return not found when updating task with mismatched dto centerId`() {
        // Given
        val taskId = 1L
        val taskDto = TaskDto(
            id = taskId,
            subject = "Updated Task",
            body = "This is an updated task",
            status = "in-progress",
            centerId = 2  // Different from user's centerId (1)
        )
        
        val existingTaskDto = TaskDto(
            id = taskId,
            subject = "Test Task",
            body = "This is a test task",
            status = "pending",
            centerId = 1
        )
        
        every { taskService.findById(taskId) } returns existingTaskDto
        
        // When
        val response = taskController.update(taskId, taskDto, userDetails)
        
        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertNull(response.body)
        
        verify { taskService.findById(taskId) }
        verify(exactly = 0) { taskService.update(any(), any()) }
    }
    
    @Test
    fun `should delete task successfully`() {
        // Given
        val taskId = 1L
        val taskDto = TaskDto(
            id = taskId,
            subject = "Test Task",
            body = "This is a test task",
            status = "pending",
            centerId = 1
        )
        
        every { taskService.findById(taskId) } returns taskDto
        every { taskService.delete(taskId) } just runs
        
        // When
        val response = taskController.delete(taskId, userDetails)
        
        // Then
        assertEquals(HttpStatus.NO_CONTENT, response.statusCode)
        assertNull(response.body)
        
        verify { taskService.findById(taskId) }
        verify { taskService.delete(taskId) }
    }
    
    @Test
    fun `should return forbidden when deleting task with null centerId`() {
        // Given
        val taskId = 1L
        
        // Create user details with null centerId
        val authorities = listOf(SimpleGrantedAuthority("task_remove"))
        val userDetailsWithNullCenter = CustomUserDetails(
            username = "testuser",
            password = "password",
            authorities = authorities,
            orgId = 1,
            centerId = null,
            id = 1
        )
        
        // Need to mock findById because the controller calls it before checking centerId
        val taskDto = TaskDto(
            id = taskId,
            subject = "Test Task",
            body = "This is a test task",
            status = "pending",
            centerId = 1
        )
        every { taskService.findById(taskId) } returns taskDto
        
        // When
        val response = taskController.delete(taskId, userDetailsWithNullCenter)
        
        // Then
        assertEquals(HttpStatus.FORBIDDEN, response.statusCode)
        assertNull(response.body)
        
        verify { taskService.findById(taskId) }
        verify(exactly = 0) { taskService.delete(any()) }
    }
    
    @Test
    fun `should return not found when deleting non-existent task`() {
        // Given
        val taskId = 999L
        
        every { taskService.findById(taskId) } returns null
        
        // When
        val response = taskController.delete(taskId, userDetails)
        
        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertNull(response.body)
        
        verify { taskService.findById(taskId) }
        verify(exactly = 0) { taskService.delete(any()) }
    }
    
    @Test
    fun `should return not found when deleting task with mismatched centerId`() {
        // Given
        val taskId = 1L
        val taskDto = TaskDto(
            id = taskId,
            subject = "Test Task",
            body = "This is a test task",
            status = "pending",
            centerId = 2  // Different from user's centerId (1)
        )
        
        every { taskService.findById(taskId) } returns taskDto
        
        // When
        val response = taskController.delete(taskId, userDetails)
        
        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertNull(response.body)
        
        verify { taskService.findById(taskId) }
        verify(exactly = 0) { taskService.delete(any()) }
    }
}