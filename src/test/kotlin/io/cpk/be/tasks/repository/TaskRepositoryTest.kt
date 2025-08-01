package io.cpk.be.tasks.repository

import io.cpk.be.basic.entity.Center
import io.cpk.be.basic.entity.Org
import io.cpk.be.basic.entity.OrgType
import io.cpk.be.config.TestConfig
import io.cpk.be.tasks.entity.Task
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import org.springframework.context.annotation.Import
import org.springframework.data.domain.PageRequest
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Unit tests for TaskRepository
 */
@DataJpaTest
@Import(TestConfig::class)
class TaskRepositoryTest {

    @Autowired
    private lateinit var entityManager: TestEntityManager

    @Autowired
    private lateinit var taskRepository: TaskRepository

    private lateinit var testOrgType: OrgType
    private lateinit var testOrg: Org
    private lateinit var testCenter: Center

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
    }

    @Test
    fun `should save and find task`() {
        // Given
        val task = Task(
            subject = "Test Task",
            body = "This is a test task",
            status = "pending",
            centerId = testCenter.id!!
        )

        // When
        val savedTask = entityManager.persistAndFlush(task)
        val found = taskRepository.findById(savedTask.id!!)

        // Then
        assertTrue(found.isPresent)
        val foundTask = found.get()
        assertEquals(savedTask.id, foundTask.id)
        assertEquals("Test Task", foundTask.subject)
        assertEquals("This is a test task", foundTask.body)
        assertEquals("pending", foundTask.status)
        assertEquals(testCenter.id, foundTask.centerId)
    }

    @Test
    fun `should find all tasks`() {
        // Given
        val task1 = Task(
            subject = "Task One",
            body = "This is task one",
            status = "pending",
            centerId = testCenter.id!!
        )
        val task2 = Task(
            subject = "Task Two",
            body = "This is task two",
            status = "in-progress",
            centerId = testCenter.id!!
        )

        entityManager.persistAndFlush(task1)
        entityManager.persistAndFlush(task2)

        // When
        val tasks = taskRepository.findAll()

        // Then
        assertTrue(tasks.size >= 2)
        assertTrue(tasks.any { it.subject == "Task One" })
        assertTrue(tasks.any { it.subject == "Task Two" })
    }

    @Test
    fun `should find all tasks by center id with pagination`() {
        // Given
        val task1 = Task(
            subject = "Task One",
            body = "This is task one",
            status = "pending",
            centerId = testCenter.id!!
        )
        val task2 = Task(
            subject = "Task Two",
            body = "This is task two",
            status = "in-progress",
            centerId = testCenter.id!!
        )

        // Create another center and task
        val anotherCenter = Center(
            name = "Another Center",
            address = "456 Another St",
            phone = "555-5678",
            orgId = testOrg.id!!
        )
        entityManager.persistAndFlush(anotherCenter)

        val task3 = Task(
            subject = "Task Three",
            body = "This is task three",
            status = "completed",
            centerId = anotherCenter.id!!
        )

        entityManager.persistAndFlush(task1)
        entityManager.persistAndFlush(task2)
        entityManager.persistAndFlush(task3)

        // When
        val pageable = PageRequest.of(0, 10)
        val result = taskRepository.findAllByCenterId(testCenter.id!!, pageable)

        // Then
        assertEquals(2, result.totalElements)
        assertTrue(result.content.any { it.subject == "Task One" })
        assertTrue(result.content.any { it.subject == "Task Two" })
        assertTrue(result.content.none { it.subject == "Task Three" })
    }

    @Test
    fun `should find all tasks by center id and status and subject containing with pagination`() {
        // Given
        val task1 = Task(
            subject = "Urgent Report",
            body = "This is an urgent report",
            status = "pending",
            centerId = testCenter.id!!
        )
        val task2 = Task(
            subject = "Urgent Meeting",
            body = "This is an urgent meeting",
            status = "pending",
            centerId = testCenter.id!!
        )
        val task3 = Task(
            subject = "Regular Report",
            body = "This is a regular report",
            status = "pending",
            centerId = testCenter.id!!
        )
        val task4 = Task(
            subject = "Urgent Report",
            body = "This is an urgent report",
            status = "completed",
            centerId = testCenter.id!!
        )

        entityManager.persistAndFlush(task1)
        entityManager.persistAndFlush(task2)
        entityManager.persistAndFlush(task3)
        entityManager.persistAndFlush(task4)

        // When
        val pageable = PageRequest.of(0, 10)
        val result = taskRepository.findAllByCenterIdAndStatusContainingAndSubjectContaining(
            testCenter.id!!, "pending", "Urgent", pageable
        )

        // Then
        assertEquals(2, result.totalElements)
        assertTrue(result.content.any { it.subject == "Urgent Report" && it.status == "pending" })
        assertTrue(result.content.any { it.subject == "Urgent Meeting" && it.status == "pending" })
        assertTrue(result.content.none { it.subject == "Regular Report" })
        assertTrue(result.content.none { it.status == "completed" })
    }

    @Test
    fun `should check if task exists by id and center id`() {
        // Given
        val task = Task(
            subject = "Test Task",
            body = "This is a test task",
            status = "pending",
            centerId = testCenter.id!!
        )

        // Create another center
        val anotherCenter = Center(
            name = "Another Center",
            address = "456 Another St",
            phone = "555-5678",
            orgId = testOrg.id!!
        )
        entityManager.persistAndFlush(anotherCenter)

        val savedTask = entityManager.persistAndFlush(task)

        // When & Then
        assertTrue(taskRepository.existsByIdAndCenterId(savedTask.id!!, testCenter.id!!))
        assertFalse(taskRepository.existsByIdAndCenterId(savedTask.id!!, anotherCenter.id!!))
        assertFalse(taskRepository.existsByIdAndCenterId(999L, testCenter.id!!))
    }

    @Test
    fun `should delete task`() {
        // Given
        val task = Task(
            subject = "Task to Delete",
            body = "This task will be deleted",
            status = "pending",
            centerId = testCenter.id!!
        )

        val savedTask = entityManager.persistAndFlush(task)
        assertTrue(taskRepository.findById(savedTask.id!!).isPresent)

        // When
        taskRepository.deleteById(savedTask.id!!)
        entityManager.flush()

        // Then
        assertTrue(taskRepository.findById(savedTask.id!!).isEmpty)
    }

    @Test
    fun `should update task`() {
        // Given
        val task = Task(
            subject = "Task to Update",
            body = "This task will be updated",
            status = "pending",
            centerId = testCenter.id!!
        )

        val savedTask = entityManager.persistAndFlush(task)

        // When
        val updatedTask = savedTask.copy(
            subject = "Updated Task",
            body = "This task has been updated",
            status = "in-progress"
        )
        taskRepository.save(updatedTask)
        entityManager.flush()

        // Then
        val updated = taskRepository.findById(savedTask.id!!).get()
        assertEquals("Updated Task", updated.subject)
        assertEquals("This task has been updated", updated.body)
        assertEquals("in-progress", updated.status)
        assertEquals(testCenter.id, updated.centerId)
        assertTrue(updated.updatedAt != null)
    }
}