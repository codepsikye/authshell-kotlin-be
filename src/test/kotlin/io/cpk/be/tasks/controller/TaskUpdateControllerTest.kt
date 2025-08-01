package io.cpk.be.tasks.controller

import io.cpk.be.security.CustomUserDetails
import io.cpk.be.tasks.dto.TaskUpdateDto
import io.cpk.be.tasks.service.TaskService
import io.cpk.be.tasks.service.TaskUpdateService
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

class TaskUpdateControllerTest {

    private val taskUpdateService = mockk<TaskUpdateService>()
    private val taskService = mockk<TaskService>()
    private lateinit var taskUpdateController: TaskUpdateController
    private lateinit var userDetails: CustomUserDetails

    @BeforeEach
    fun setUp() {
        taskUpdateController = TaskUpdateController(taskUpdateService, taskService)
        
        // Create mock user details with centerId = 1
        val authorities = listOf(
            SimpleGrantedAuthority("task_update_create"),
            SimpleGrantedAuthority("task_update_read"),
            SimpleGrantedAuthority("task_update_edit"),
            SimpleGrantedAuthority("task_update_remove")
        )
        userDetails = CustomUserDetails(
            username = "testuser",
            password = "password",
            authorities = authorities,
            orgId = 1,
            centerId = 1,
            id = "user1"
        )
    }

    @Test
    fun `should create task update successfully`() {
        // Given
        val taskId = 1L
        val taskUpdateDto = TaskUpdateDto(
            taskId = taskId,
            body = "This is a task update",
            status = "in-progress"
        )
        
        val createdTaskUpdateDto = TaskUpdateDto(
            id = 1L,
            taskId = taskId,
            body = "This is a task update",
            status = "in-progress"
        )
        
        every { taskService.existsByIdAndCenterId(taskId, 1) } returns true
        every { taskUpdateService.create(taskUpdateDto) } returns createdTaskUpdateDto
        
        // When
        val response = taskUpdateController.create(taskUpdateDto, userDetails)
        
        // Then
        assertEquals(HttpStatus.CREATED, response.statusCode)
        assertNotNull(response.body)
        assertEquals(1L, response.body?.id)
        assertEquals(taskId, response.body?.taskId)
        assertEquals("This is a task update", response.body?.body)
        assertEquals("in-progress", response.body?.status)
        
        verify { taskService.existsByIdAndCenterId(taskId, 1) }
        verify { taskUpdateService.create(taskUpdateDto) }
    }
    
    @Test
    fun `should return forbidden when centerId is null for create`() {
        // Given
        val taskId = 1L
        val taskUpdateDto = TaskUpdateDto(
            taskId = taskId,
            body = "This is a task update",
            status = "in-progress"
        )
        
        // Create user details with null centerId
        val authorities = listOf(SimpleGrantedAuthority("task_update_create"))
        val userDetailsWithNullCenter = CustomUserDetails(
            username = "testuser",
            password = "password",
            authorities = authorities,
            orgId = 1,
            centerId = null,
            id = "user1"
        )
        
        // When
        val response = taskUpdateController.create(taskUpdateDto, userDetailsWithNullCenter)
        
        // Then
        assertEquals(HttpStatus.FORBIDDEN, response.statusCode)
        assertNull(response.body)
        
        verify(exactly = 0) { taskService.existsByIdAndCenterId(any(), any()) }
        verify(exactly = 0) { taskUpdateService.create(any()) }
    }
    
    @Test
    fun `should return not found when task does not exist for create`() {
        // Given
        val taskId = 999L
        val taskUpdateDto = TaskUpdateDto(
            taskId = taskId,
            body = "This is a task update",
            status = "in-progress"
        )
        
        every { taskService.existsByIdAndCenterId(taskId, 1) } returns false
        
        // When
        val response = taskUpdateController.create(taskUpdateDto, userDetails)
        
        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertNull(response.body)
        
        verify { taskService.existsByIdAndCenterId(taskId, 1) }
        verify(exactly = 0) { taskUpdateService.create(any()) }
    }
    
    @Test
    fun `should return not found when taskId is null for create`() {
        // Given
        val taskUpdateDto = TaskUpdateDto(
            taskId = null,
            body = "This is a task update",
            status = "in-progress"
        )
        
        // When
        val response = taskUpdateController.create(taskUpdateDto, userDetails)
        
        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertNull(response.body)
        
        verify(exactly = 0) { taskService.existsByIdAndCenterId(any(), any()) }
        verify(exactly = 0) { taskUpdateService.create(any()) }
    }
    
    @Test
    fun `should find all task updates successfully`() {
        // Given
        val page = 0
        val size = 10
        val pageable = PageRequest.of(page, size)
        
        val taskUpdateDto1 = TaskUpdateDto(id = 1L, taskId = 1L, status = "pending")
        val taskUpdateDto2 = TaskUpdateDto(id = 2L, taskId = 1L, status = "in-progress")
        val taskUpdates = listOf(taskUpdateDto1, taskUpdateDto2)
        
        val pageResult = PageImpl(taskUpdates, pageable, taskUpdates.size.toLong())
        
        every { taskUpdateService.findAll(1, pageable) } returns pageResult
        
        // When
        val response = taskUpdateController.findAll(page, size, userDetails)
        
        // Then
        assertEquals(HttpStatus.OK, response.statusCode)
        assertNotNull(response.body)
        assertEquals(2, response.body?.content?.size)
        assertEquals(2, response.body?.totalElements)
        assertEquals(1L, response.body?.content?.get(0)?.id)
        assertEquals(2L, response.body?.content?.get(1)?.id)
        
        verify { taskUpdateService.findAll(1, pageable) }
    }
    
    @Test
    fun `should return forbidden when centerId is null for findAll`() {
        // Given
        val page = 0
        val size = 10
        
        // Create user details with null centerId
        val authorities = listOf(SimpleGrantedAuthority("task_update_read"))
        val userDetailsWithNullCenter = CustomUserDetails(
            username = "testuser",
            password = "password",
            authorities = authorities,
            orgId = 1,
            centerId = null,
            id = "user1"
        )
        
        // When
        val response = taskUpdateController.findAll(page, size, userDetailsWithNullCenter)
        
        // Then
        assertEquals(HttpStatus.FORBIDDEN, response.statusCode)
        assertNull(response.body)
        
        verify(exactly = 0) { taskUpdateService.findAll(any(), any()) }
    }
    
    @Test
    fun `should find task update by id successfully`() {
        // Given
        val taskUpdateId = 1L
        val taskUpdateDto = TaskUpdateDto(
            id = taskUpdateId,
            taskId = 1L,
            body = "This is a task update",
            status = "in-progress"
        )
        
        every { taskUpdateService.findById(taskUpdateId) } returns taskUpdateDto
        every { taskUpdateService.existsByIdAndTaskCenterId(taskUpdateId, 1) } returns true
        
        // When
        val response = taskUpdateController.findById(taskUpdateId, userDetails)
        
        // Then
        assertEquals(HttpStatus.OK, response.statusCode)
        assertNotNull(response.body)
        assertEquals(taskUpdateId, response.body?.id)
        assertEquals(1L, response.body?.taskId)
        assertEquals("This is a task update", response.body?.body)
        assertEquals("in-progress", response.body?.status)
        
        verify { taskUpdateService.findById(taskUpdateId) }
        verify { taskUpdateService.existsByIdAndTaskCenterId(taskUpdateId, 1) }
    }
    
    @Test
    fun `should return forbidden when centerId is null for findById`() {
        // Given
        val taskUpdateId = 1L
        
        // Create user details with null centerId
        val authorities = listOf(SimpleGrantedAuthority("task_update_read"))
        val userDetailsWithNullCenter = CustomUserDetails(
            username = "testuser",
            password = "password",
            authorities = authorities,
            orgId = 1,
            centerId = null,
            id = "user1"
        )
        
        // Need to mock findById because the controller calls it before checking centerId
        val taskUpdateDto = TaskUpdateDto(
            id = taskUpdateId,
            taskId = 1L,
            body = "This is a task update",
            status = "in-progress"
        )
        every { taskUpdateService.findById(taskUpdateId) } returns taskUpdateDto
        
        // When
        val response = taskUpdateController.findById(taskUpdateId, userDetailsWithNullCenter)
        
        // Then
        assertEquals(HttpStatus.FORBIDDEN, response.statusCode)
        assertNull(response.body)
        
        verify { taskUpdateService.findById(taskUpdateId) }
        verify(exactly = 0) { taskUpdateService.existsByIdAndTaskCenterId(any(), any()) }
    }
    
    @Test
    fun `should return not found when task update does not exist`() {
        // Given
        val taskUpdateId = 999L
        
        every { taskUpdateService.findById(taskUpdateId) } returns null
        
        // When
        val response = taskUpdateController.findById(taskUpdateId, userDetails)
        
        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertNull(response.body)
        
        verify { taskUpdateService.findById(taskUpdateId) }
        verify(exactly = 0) { taskUpdateService.existsByIdAndTaskCenterId(any(), any()) }
    }
    
    @Test
    fun `should return not found when task update's task is not in user's center`() {
        // Given
        val taskUpdateId = 1L
        val taskUpdateDto = TaskUpdateDto(
            id = taskUpdateId,
            taskId = 1L,
            body = "This is a task update",
            status = "in-progress"
        )
        
        every { taskUpdateService.findById(taskUpdateId) } returns taskUpdateDto
        every { taskUpdateService.existsByIdAndTaskCenterId(taskUpdateId, 1) } returns false
        
        // When
        val response = taskUpdateController.findById(taskUpdateId, userDetails)
        
        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertNull(response.body)
        
        verify { taskUpdateService.findById(taskUpdateId) }
        verify { taskUpdateService.existsByIdAndTaskCenterId(taskUpdateId, 1) }
    }
    
    @Test
    fun `should update task update successfully`() {
        // Given
        val taskUpdateId = 1L
        val taskId = 1L
        val taskUpdateDto = TaskUpdateDto(
            id = taskUpdateId,
            taskId = taskId,
            body = "This is an updated task update",
            status = "completed"
        )
        
        val existingTaskUpdateDto = TaskUpdateDto(
            id = taskUpdateId,
            taskId = taskId,
            body = "This is a task update",
            status = "in-progress"
        )
        
        val updatedTaskUpdateDto = TaskUpdateDto(
            id = taskUpdateId,
            taskId = taskId,
            body = "This is an updated task update",
            status = "completed"
        )
        
        every { taskUpdateService.findById(taskUpdateId) } returns existingTaskUpdateDto
        every { taskUpdateService.existsByIdAndTaskCenterId(taskUpdateId, 1) } returns true
        every { taskService.existsByIdAndCenterId(taskId, 1) } returns true
        every { taskUpdateService.update(taskUpdateId, taskUpdateDto) } returns updatedTaskUpdateDto
        
        // When
        val response = taskUpdateController.update(taskUpdateId, taskUpdateDto, userDetails)
        
        // Then
        assertEquals(HttpStatus.OK, response.statusCode)
        assertNotNull(response.body)
        assertEquals(taskUpdateId, response.body?.id)
        assertEquals(taskId, response.body?.taskId)
        assertEquals("This is an updated task update", response.body?.body)
        assertEquals("completed", response.body?.status)
        
        verify { taskUpdateService.findById(taskUpdateId) }
        verify { taskUpdateService.existsByIdAndTaskCenterId(taskUpdateId, 1) }
        verify { taskService.existsByIdAndCenterId(taskId, 1) }
        verify { taskUpdateService.update(taskUpdateId, taskUpdateDto) }
    }
    
    @Test
    fun `should return forbidden when centerId is null for update`() {
        // Given
        val taskUpdateId = 1L
        val taskUpdateDto = TaskUpdateDto(
            id = taskUpdateId,
            taskId = 1L,
            body = "This is an updated task update",
            status = "completed"
        )
        
        // Create user details with null centerId
        val authorities = listOf(SimpleGrantedAuthority("task_update_edit"))
        val userDetailsWithNullCenter = CustomUserDetails(
            username = "testuser",
            password = "password",
            authorities = authorities,
            orgId = 1,
            centerId = null,
            id = "user1"
        )
        
        // Need to mock findById because the controller calls it before checking centerId
        val existingTaskUpdateDto = TaskUpdateDto(
            id = taskUpdateId,
            taskId = 1L,
            body = "This is a task update",
            status = "in-progress"
        )
        every { taskUpdateService.findById(taskUpdateId) } returns existingTaskUpdateDto
        
        // When
        val response = taskUpdateController.update(taskUpdateId, taskUpdateDto, userDetailsWithNullCenter)
        
        // Then
        assertEquals(HttpStatus.FORBIDDEN, response.statusCode)
        assertNull(response.body)
        
        verify { taskUpdateService.findById(taskUpdateId) }
        verify(exactly = 0) { taskUpdateService.existsByIdAndTaskCenterId(any(), any()) }
        verify(exactly = 0) { taskService.existsByIdAndCenterId(any(), any()) }
        verify(exactly = 0) { taskUpdateService.update(any(), any()) }
    }
    
    @Test
    fun `should return not found when task update does not exist for update`() {
        // Given
        val taskUpdateId = 999L
        val taskUpdateDto = TaskUpdateDto(
            id = taskUpdateId,
            taskId = 1L,
            body = "This is an updated task update",
            status = "completed"
        )
        
        every { taskUpdateService.findById(taskUpdateId) } returns null
        
        // When
        val response = taskUpdateController.update(taskUpdateId, taskUpdateDto, userDetails)
        
        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertNull(response.body)
        
        verify { taskUpdateService.findById(taskUpdateId) }
        verify(exactly = 0) { taskUpdateService.existsByIdAndTaskCenterId(any(), any()) }
        verify(exactly = 0) { taskService.existsByIdAndCenterId(any(), any()) }
        verify(exactly = 0) { taskUpdateService.update(any(), any()) }
    }
    
    @Test
    fun `should return not found when task update's task is not in user's center for update`() {
        // Given
        val taskUpdateId = 1L
        val taskId = 1L
        val taskUpdateDto = TaskUpdateDto(
            id = taskUpdateId,
            taskId = taskId,
            body = "This is an updated task update",
            status = "completed"
        )
        
        val existingTaskUpdateDto = TaskUpdateDto(
            id = taskUpdateId,
            taskId = taskId,
            body = "This is a task update",
            status = "in-progress"
        )
        
        every { taskUpdateService.findById(taskUpdateId) } returns existingTaskUpdateDto
        every { taskUpdateService.existsByIdAndTaskCenterId(taskUpdateId, 1) } returns false
        
        // When
        val response = taskUpdateController.update(taskUpdateId, taskUpdateDto, userDetails)
        
        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertNull(response.body)
        
        verify { taskUpdateService.findById(taskUpdateId) }
        verify { taskUpdateService.existsByIdAndTaskCenterId(taskUpdateId, 1) }
        verify(exactly = 0) { taskService.existsByIdAndCenterId(any(), any()) }
        verify(exactly = 0) { taskUpdateService.update(any(), any()) }
    }
    
    @Test
    fun `should return not found when task does not exist in user's center for update`() {
        // Given
        val taskUpdateId = 1L
        val taskId = 1L
        val taskUpdateDto = TaskUpdateDto(
            id = taskUpdateId,
            taskId = taskId,
            body = "This is an updated task update",
            status = "completed"
        )
        
        val existingTaskUpdateDto = TaskUpdateDto(
            id = taskUpdateId,
            taskId = taskId,
            body = "This is a task update",
            status = "in-progress"
        )
        
        every { taskUpdateService.findById(taskUpdateId) } returns existingTaskUpdateDto
        every { taskUpdateService.existsByIdAndTaskCenterId(taskUpdateId, 1) } returns true
        every { taskService.existsByIdAndCenterId(taskId, 1) } returns false
        
        // When
        val response = taskUpdateController.update(taskUpdateId, taskUpdateDto, userDetails)
        
        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertNull(response.body)
        
        verify { taskUpdateService.findById(taskUpdateId) }
        verify { taskUpdateService.existsByIdAndTaskCenterId(taskUpdateId, 1) }
        verify { taskService.existsByIdAndCenterId(taskId, 1) }
        verify(exactly = 0) { taskUpdateService.update(any(), any()) }
    }
    
    @Test
    fun `should return not found when taskId is null for update`() {
        // Given
        val taskUpdateId = 1L
        val taskUpdateDto = TaskUpdateDto(
            id = taskUpdateId,
            taskId = null,  // This is what we're testing - taskId is null
            body = "This is an updated task update",
            status = "completed"
        )
        
        val existingTaskUpdateDto = TaskUpdateDto(
            id = taskUpdateId,
            taskId = 1L,
            body = "This is a task update",
            status = "in-progress"
        )
        
        every { taskUpdateService.findById(taskUpdateId) } returns existingTaskUpdateDto
        every { taskUpdateService.existsByIdAndTaskCenterId(taskUpdateId, 1) } returns true
        
        // When
        val response = taskUpdateController.update(taskUpdateId, taskUpdateDto, userDetails)
        
        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertNull(response.body)
        
        verify { taskUpdateService.findById(taskUpdateId) }
        // The controller checks taskId != null before calling existsByIdAndTaskCenterId
        // Since taskId is null, existsByIdAndTaskCenterId should not be called
        verify(exactly = 0) { taskUpdateService.existsByIdAndTaskCenterId(any(), any()) }
        verify(exactly = 0) { taskService.existsByIdAndCenterId(any(), any()) }
        verify(exactly = 0) { taskUpdateService.update(any(), any()) }
    }
    
    @Test
    fun `should delete task update successfully`() {
        // Given
        val taskUpdateId = 1L
        
        every { taskUpdateService.existsByIdAndTaskCenterId(taskUpdateId, 1) } returns true
        every { taskUpdateService.delete(taskUpdateId) } just runs
        
        // When
        val response = taskUpdateController.delete(taskUpdateId, userDetails)
        
        // Then
        assertEquals(HttpStatus.NO_CONTENT, response.statusCode)
        assertNull(response.body)
        
        verify { taskUpdateService.existsByIdAndTaskCenterId(taskUpdateId, 1) }
        verify { taskUpdateService.delete(taskUpdateId) }
    }
    
    @Test
    fun `should return forbidden when centerId is null for delete`() {
        // Given
        val taskUpdateId = 1L
        
        // Create user details with null centerId
        val authorities = listOf(SimpleGrantedAuthority("task_update_remove"))
        val userDetailsWithNullCenter = CustomUserDetails(
            username = "testuser",
            password = "password",
            authorities = authorities,
            orgId = 1,
            centerId = null,
            id = "user1"
        )
        
        // When
        val response = taskUpdateController.delete(taskUpdateId, userDetailsWithNullCenter)
        
        // Then
        assertEquals(HttpStatus.FORBIDDEN, response.statusCode)
        assertNull(response.body)
        
        verify(exactly = 0) { taskUpdateService.existsByIdAndTaskCenterId(any(), any()) }
        verify(exactly = 0) { taskUpdateService.delete(any()) }
    }
    
    @Test
    fun `should return not found when task update does not exist for delete`() {
        // Given
        val taskUpdateId = 999L
        
        every { taskUpdateService.existsByIdAndTaskCenterId(taskUpdateId, 1) } returns false
        
        // When
        val response = taskUpdateController.delete(taskUpdateId, userDetails)
        
        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertNull(response.body)
        
        verify { taskUpdateService.existsByIdAndTaskCenterId(taskUpdateId, 1) }
        verify(exactly = 0) { taskUpdateService.delete(any()) }
    }
}