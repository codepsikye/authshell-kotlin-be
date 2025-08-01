package io.cpk.be.tasks.entity

import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import kotlin.test.*

class TaskUpdateTest {

    @Test
    fun `should create TaskUpdate with required parameters`() {
        // Given
        val taskId = 1L
        val status = "in-progress"

        // When
        val taskUpdate = TaskUpdate(
            taskId = taskId,
            status = status
        )

        // Then
        assertNull(taskUpdate.id)
        assertEquals(taskId, taskUpdate.taskId)
        assertNull(taskUpdate.body)
        assertEquals(status, taskUpdate.status)
        assertNotNull(taskUpdate.createdAt)
        assertNull(taskUpdate.task)
    }

    @Test
    fun `should create TaskUpdate with all parameters`() {
        // Given
        val id = 1L
        val taskId = 2L
        val body = "This is a task update"
        val status = "completed"
        val createdAt = LocalDateTime.now().minusHours(1)
        val task = Task(
            id = taskId,
            subject = "Test Task",
            centerId = 1
        )

        // When
        val taskUpdate = TaskUpdate(
            id = id,
            taskId = taskId,
            body = body,
            status = status,
            
            task = task
        )

        // Then
        assertEquals(id, taskUpdate.id)
        assertEquals(taskId, taskUpdate.taskId)
        assertEquals(body, taskUpdate.body)
        assertEquals(status, taskUpdate.status)
        assertEquals(task, taskUpdate.task)
    }

    @Test
    fun `should use default values for optional parameters if not provided`() {
        // Given
        val taskId = 1L
        val status = "in-progress"
        val beforeCreation = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS)

        // When
        val taskUpdate = TaskUpdate(
            taskId = taskId,
            status = status
        )

        // Then
        val afterCreation = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS)

        assertNull(taskUpdate.id)
        assertNull(taskUpdate.body)
        assertNull(taskUpdate.task)

        // Check that createdAt is between beforeCreation and afterCreation
        assert(taskUpdate.createdAt.truncatedTo(ChronoUnit.SECONDS) >= beforeCreation)
        assert(taskUpdate.createdAt.truncatedTo(ChronoUnit.SECONDS) <= afterCreation)
    }

    @Test
    fun `should correctly implement equals and hashCode`() {
        // Given
        val now = LocalDateTime.now()
        val taskUpdate1 = TaskUpdate(
            id = 1L,
            taskId = 2L,
            body = "This is a task update",
            status = "in-progress",

        )
        val taskUpdate2 = TaskUpdate(
            id = 1L,
            taskId = 2L,
            body = "This is a task update",
            status = "in-progress",
            
        )
        val taskUpdate3 = TaskUpdate(
            id = 3L,
            taskId = 2L,
            body = "This is a task update",
            status = "in-progress",
            
        )

        // Then
        assertEquals(taskUpdate1, taskUpdate2)
        assertEquals(taskUpdate1.hashCode(), taskUpdate2.hashCode())
        assertNotEquals(taskUpdate1, taskUpdate3)
        assertNotEquals(taskUpdate1.hashCode(), taskUpdate3.hashCode())
    }

    @Test
    fun `should correctly implement copy`() {
        // Given
        val taskUpdate = TaskUpdate(
            id = 1L,
            taskId = 2L,
            body = "This is a task update",
            status = "in-progress"
        )

        // When
        val copied = taskUpdate.copy(
            body = "Updated body",
            status = "completed"
        )

        // Then
        assertEquals(1L, copied.id)
        assertEquals(2L, copied.taskId)
        assertEquals("Updated body", copied.body)
        assertEquals("completed", copied.status)
    }

    @Test
    fun `should create TaskUpdate with no-arg constructor`() {
        // When
        val taskUpdate = TaskUpdate()

        // Then
        assertNull(taskUpdate.id)
        assertEquals(0L, taskUpdate.taskId)
        assertNull(taskUpdate.body)
        assertEquals("", taskUpdate.status)
        assertNull(taskUpdate.task)
        assertNotNull(taskUpdate.createdAt)
        assertNotNull(taskUpdate.updatedAt)
    }

    @Test
    fun `should correctly implement toString`() {
        // Given
        val id = 1L
        val taskId = 2L
        val body = "This is a task update"
        val status = "in-progress"
        val createdAt = LocalDateTime.of(2025, 7, 25, 10, 0)
        val updatedAt = LocalDateTime.of(2025, 7, 25, 11, 0)

        val taskUpdate = TaskUpdate(
            id = id,
            taskId = taskId,
            body = body,
            status = status,
            
            
        )

        // When
        val toString = taskUpdate.toString()

        // Then
        assertTrue(toString.contains("TaskUpdate"))
        assertTrue(toString.contains("id=$id"))
        assertTrue(toString.contains("taskId=$taskId"))
        assertTrue(toString.contains("body=$body"))
        assertTrue(toString.contains("status='$status'"))
    }

    @Test
    fun `should handle edge cases in equals method`() {
        // Given
        val taskUpdate = TaskUpdate(taskId = 1L, status = "in-progress")

        // Then
        assertEquals(taskUpdate, taskUpdate) // Same instance

        // Null comparison
        val nullTaskUpdate: TaskUpdate? = null
        assertNotEquals(taskUpdate, nullTaskUpdate)

        // Different type comparison
        val differentObject = Any()
        assertFalse(taskUpdate.equals(differentObject))
    }

    @Test
    fun `should test all branches in equals method`() {
        // Given
        val baseTaskUpdate = TaskUpdate(
            id = 1L,
            taskId = 2L,
            body = "This is a task update",
            status = "in-progress"
        )

        // Different id
        val taskUpdateDiffId = baseTaskUpdate.copy(id = 3L)
        assertNotEquals(baseTaskUpdate, taskUpdateDiffId)

        // Different taskId
        val taskUpdateDiffTaskId = baseTaskUpdate.copy(taskId = 4L)
        assertNotEquals(baseTaskUpdate, taskUpdateDiffTaskId)

        // Different body
        val taskUpdateDiffBody = baseTaskUpdate.copy(body = "Different body")
        assertNotEquals(baseTaskUpdate, taskUpdateDiffBody)

        // Different status
        val taskUpdateDiffStatus = baseTaskUpdate.copy(status = "completed")
        assertNotEquals(baseTaskUpdate, taskUpdateDiffStatus)
    }
}