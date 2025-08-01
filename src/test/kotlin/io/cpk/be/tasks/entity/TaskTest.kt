package io.cpk.be.tasks.entity

import io.cpk.be.basic.entity.Center
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import kotlin.test.*

class TaskTest {

    @Test
    fun `should create Task with required parameters`() {
        // Given
        val subject = "Test Task"
        val centerId = 1
        
        // When
        val task = Task(
            subject = subject,
            centerId = centerId
        )
        
        // Then
        assertNull(task.id)
        assertEquals(subject, task.subject)
        assertNull(task.body)
        assertEquals("pending", task.status)
        assertEquals(centerId, task.centerId)
        assertNotNull(task.createdAt)
        assertNotNull(task.updatedAt)
        assertNull(task.center)
    }
    
    @Test
    fun `should create Task with all parameters`() {
        // Given
        val id = 1L
        val subject = "Test Task"
        val body = "This is a test task"
        val status = "in-progress"
        val centerId = 1
        val createdAt = LocalDateTime.now().minusDays(1)
        val updatedAt = LocalDateTime.now()
        val center = Center(
            id = centerId,
            name = "Test Center",
            orgId = 1
        )
        
        // When
        val task = Task(
            id = id,
            subject = subject,
            body = body,
            status = status,
            centerId = centerId,
            

            center = center
        )
        
        // Then
        assertEquals(id, task.id)
        assertEquals(subject, task.subject)
        assertEquals(body, task.body)
        assertEquals(status, task.status)
        assertEquals(centerId, task.centerId)
        assertEquals(center, task.center)
    }
    
    @Test
    fun `should use default values for optional parameters if not provided`() {
        // Given
        val subject = "Test Task"
        val centerId = 1
        val beforeCreation = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS)
        
        // When
        val task = Task(
            subject = subject,
            centerId = centerId
        )
        
        // Then
        val afterCreation = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS)
        
        assertNull(task.id)
        assertNull(task.body)
        assertEquals("pending", task.status)
        assertNull(task.center)
        
        // Check that timestamps are between beforeCreation and afterCreation
        assert(task.createdAt.truncatedTo(ChronoUnit.SECONDS) >= beforeCreation)
        assert(task.createdAt.truncatedTo(ChronoUnit.SECONDS) <= afterCreation)
        assert(task.updatedAt.truncatedTo(ChronoUnit.SECONDS) >= beforeCreation)
        assert(task.updatedAt.truncatedTo(ChronoUnit.SECONDS) <= afterCreation)
    }
    
    @Test
    fun `should correctly implement equals and hashCode`() {
        // Given
        val now = LocalDateTime.now()
        val task1 = Task(
            id = 1L,
            subject = "Test Task",
            body = "This is a test task",
            status = "pending",
            centerId = 1,
            
            
        )
        val task2 = Task(
            id = 1L,
            subject = "Test Task",
            body = "This is a test task",
            status = "pending",
            centerId = 1,
            
            
        )
        val task3 = Task(
            id = 2L,
            subject = "Test Task",
            body = "This is a test task",
            status = "pending",
            centerId = 1,
            
            
        )
        
        // Then
        assertEquals(task1, task2)
        assertEquals(task1.hashCode(), task2.hashCode())
        assertNotEquals(task1, task3)
        assertNotEquals(task1.hashCode(), task3.hashCode())
    }
    
    @Test
    fun `should correctly implement copy`() {
        // Given
        val task = Task(
            id = 1L,
            subject = "Test Task",
            body = "This is a test task",
            status = "pending",
            centerId = 1
        )
        
        // When
        val copied = task.copy(
            subject = "Updated Task",
            body = "This task has been updated",
            status = "in-progress"
        )
        
        // Then
        assertEquals(1L, copied.id)
        assertEquals("Updated Task", copied.subject)
        assertEquals("This task has been updated", copied.body)
        assertEquals("in-progress", copied.status)
        assertEquals(1, copied.centerId)
    }
    
    @Test
    fun `should create Task with no-arg constructor`() {
        // When
        val task = Task()
        
        // Then
        assertNull(task.id)
        assertEquals("", task.subject)
        assertNull(task.body)
        assertEquals("pending", task.status)
        assertEquals(0, task.centerId)
        assertNull(task.center)
        assertNotNull(task.createdAt)
        assertNotNull(task.updatedAt)
    }
    
    @Test
    fun `should correctly implement toString`() {
        // Given
        val id = 1L
        val subject = "Test Task"
        val body = "This is a test task"
        val status = "in-progress"
        val centerId = 1
        val createdAt = LocalDateTime.of(2025, 7, 25, 10, 0)
        val updatedAt = LocalDateTime.of(2025, 7, 25, 11, 0)
        
        val task = Task(
            id = id,
            subject = subject,
            body = body,
            status = status,
            centerId = centerId,
            

        )
        
        // When
        val toString = task.toString()
        
        // Then
        assertTrue(toString.contains("Task"))
        assertTrue(toString.contains("id=$id"))
        assertTrue(toString.contains("subject='$subject'"))
        assertTrue(toString.contains("body=$body"))
        assertTrue(toString.contains("status='$status'"))
        assertTrue(toString.contains("centerId=$centerId"))
    }
    
    @Test
    fun `should handle edge cases in equals method`() {
        // Given
        val task = Task(subject = "Test Task", centerId = 1)
        
        // Then
        assertEquals(task, task) // Same instance
        
        // Null comparison
        val nullTask: Task? = null
        assertNotEquals(task, nullTask)
        
        // Different type comparison
        val differentObject = Any()
        assertFalse(task.equals(differentObject))
    }
    
    @Test
    fun `should test all branches in equals method`() {
        // Given
        val baseTask = Task(
            id = 1L,
            subject = "Test Task",
            body = "This is a test task",
            status = "pending",
            centerId = 1
        )
        
        // Different id
        val taskDiffId = baseTask.copy(id = 2L)
        assertNotEquals(baseTask, taskDiffId)
        
        // Different subject
        val taskDiffSubject = baseTask.copy(subject = "Different Task")
        assertNotEquals(baseTask, taskDiffSubject)
        
        // Different body
        val taskDiffBody = baseTask.copy(body = "Different body")
        assertNotEquals(baseTask, taskDiffBody)
        
        // Different status
        val taskDiffStatus = baseTask.copy(status = "in-progress")
        assertNotEquals(baseTask, taskDiffStatus)
        
        // Different centerId
        val taskDiffCenterId = baseTask.copy(centerId = 2)
        assertNotEquals(baseTask, taskDiffCenterId)
    }
}