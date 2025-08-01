package io.cpk.be.tasks.repository

import io.cpk.be.basic.entity.Center
import io.cpk.be.basic.entity.Org
import io.cpk.be.basic.entity.OrgType
import io.cpk.be.config.TestConfig
import io.cpk.be.tasks.entity.Task
import io.cpk.be.tasks.entity.TaskUpdate
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import org.springframework.context.annotation.Import
import org.springframework.data.domain.PageRequest
import java.time.LocalDateTime
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Unit tests for TaskUpdateRepository
 */
@DataJpaTest
@Import(TestConfig::class)
class TaskUpdateRepositoryTest {

    @Autowired
    private lateinit var entityManager: TestEntityManager

    @Autowired
    private lateinit var taskUpdateRepository: TaskUpdateRepository

    private lateinit var testOrgType: OrgType
    private lateinit var testOrg: Org
    private lateinit var testCenter: Center
    private lateinit var testTask: Task
    private lateinit var anotherCenter: Center
    private lateinit var anotherTask: Task

    @BeforeEach
    fun setUp() {
        // Create and persist OrgType
        testOrgType = OrgType(
            name = "test-org-type",
            accessRight = listOf("read", "write"),
            orgConfigs = mapOf("key" to "value")
        )
        entityManager.persistAndFlush(testOrgType)

        // Create and persist Org
        testOrg = Org(
            name = "Test Organization",
            address = "123 Org St",
            phone = "555-0000",
            city = "Test City",
            country = "Test Country",
            notes = "Test Notes",
            orgTypeName = testOrgType.name
        )
        entityManager.persistAndFlush(testOrg)

        // Create and persist Center
        testCenter = Center(
            name = "Test Center",
            address = "123 Center St",
            phone = "555-1234",
            orgId = testOrg.id!!
        )
        entityManager.persistAndFlush(testCenter)

        // Create another center
        anotherCenter = Center(
            name = "Another Center",
            address = "456 Another St",
            phone = "555-5678",
            orgId = testOrg.id!!
        )
        entityManager.persistAndFlush(anotherCenter)

        // Create and persist Task
        testTask = Task(
            subject = "Test Task",
            body = "This is a test task",
            status = "pending",
            centerId = testCenter.id!!
        )
        entityManager.persistAndFlush(testTask)

        // Create another task for a different center
        anotherTask = Task(
            subject = "Another Task",
            body = "This is another task",
            status = "pending",
            centerId = anotherCenter.id!!
        )
        entityManager.persistAndFlush(anotherTask)
    }

    @Test
    fun `should save and find task update`() {
        // Given
        val taskUpdate = TaskUpdate(
            taskId = testTask.id!!,
            body = "This is a task update",
            status = "in-progress"
        )

        // When
        val savedTaskUpdate = entityManager.persistAndFlush(taskUpdate)
        val found = taskUpdateRepository.findById(savedTaskUpdate.id!!)

        // Then
        assertTrue(found.isPresent)
        val foundTaskUpdate = found.get()
        assertEquals(savedTaskUpdate.id, foundTaskUpdate.id)
        assertEquals(testTask.id, foundTaskUpdate.taskId)
        assertEquals("This is a task update", foundTaskUpdate.body)
        assertEquals("in-progress", foundTaskUpdate.status)
    }

    @Test
    fun `should find all task updates`() {
        // Given
        val taskUpdate1 = TaskUpdate(
            taskId = testTask.id!!,
            body = "First update",
            status = "in-progress"
        )
        val taskUpdate2 = TaskUpdate(
            taskId = testTask.id!!,
            body = "Second update",
            status = "completed"
        )

        entityManager.persistAndFlush(taskUpdate1)
        entityManager.persistAndFlush(taskUpdate2)

        // When
        val taskUpdates = taskUpdateRepository.findAll()

        // Then
        assertTrue(taskUpdates.size >= 2)
        assertTrue(taskUpdates.any { it.body == "First update" })
        assertTrue(taskUpdates.any { it.body == "Second update" })
    }

    @Test
    fun `should find all task updates by task center id with pagination`() {
        // Given
        val taskUpdate1 = TaskUpdate(
            taskId = testTask.id!!,
            body = "Update for test task",
            status = "in-progress"
        )
        val taskUpdate2 = TaskUpdate(
            taskId = testTask.id!!,
            body = "Another update for test task",
            status = "completed"
        )
        val taskUpdate3 = TaskUpdate(
            taskId = anotherTask.id!!,
            body = "Update for another task",
            status = "in-progress"
        )

        entityManager.persistAndFlush(taskUpdate1)
        entityManager.persistAndFlush(taskUpdate2)
        entityManager.persistAndFlush(taskUpdate3)

        // When
        val pageable = PageRequest.of(0, 10)
        val result = taskUpdateRepository.findAllByTaskCenterId(testCenter.id!!, pageable)

        // Then
        assertEquals(2, result.totalElements)
        assertTrue(result.content.any { it.body == "Update for test task" })
        assertTrue(result.content.any { it.body == "Another update for test task" })
        assertTrue(result.content.none { it.body == "Update for another task" })
    }

    @Test
    fun `should check if task update exists by id and task center id`() {
        // Given
        val taskUpdate = TaskUpdate(
            taskId = testTask.id!!,
            body = "Test update",
            status = "in-progress"
        )

        val savedTaskUpdate = entityManager.persistAndFlush(taskUpdate)

        // When & Then
        assertTrue(taskUpdateRepository.existsByIdAndTaskCenterId(savedTaskUpdate.id!!, testCenter.id!!))
        assertFalse(taskUpdateRepository.existsByIdAndTaskCenterId(savedTaskUpdate.id!!, anotherCenter.id!!))
        assertFalse(taskUpdateRepository.existsByIdAndTaskCenterId(999L, testCenter.id!!))
    }

    @Test
    fun `should delete task update`() {
        // Given
        val taskUpdate = TaskUpdate(
            taskId = testTask.id!!,
            body = "Update to delete",
            status = "in-progress"
        )

        val savedTaskUpdate = entityManager.persistAndFlush(taskUpdate)
        assertTrue(taskUpdateRepository.findById(savedTaskUpdate.id!!).isPresent)

        // When
        taskUpdateRepository.deleteById(savedTaskUpdate.id!!)
        entityManager.flush()

        // Then
        assertTrue(taskUpdateRepository.findById(savedTaskUpdate.id!!).isEmpty)
    }

    @Test
    fun `should update task update`() {
        // Given
        val now = LocalDateTime.now()
        val taskUpdate = TaskUpdate(
            taskId = testTask.id!!,
            body = "Initial update",
            status = "in-progress",
        )

        val savedTaskUpdate = entityManager.persistAndFlush(taskUpdate)

        // When
        val updatedTaskUpdate = savedTaskUpdate.copy(
            body = "Updated update",
            status = "completed"
        )
        taskUpdateRepository.save(updatedTaskUpdate)
        entityManager.flush()

        // Then
        val updated = taskUpdateRepository.findById(savedTaskUpdate.id!!).get()
        assertEquals(testTask.id, updated.taskId)
        assertEquals("Updated update", updated.body)
        assertEquals("completed", updated.status)
    }
}