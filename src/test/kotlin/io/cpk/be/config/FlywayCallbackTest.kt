package io.cpk.be.config

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.mockk.justRun
import io.mockk.slot
import org.flywaydb.core.api.callback.Context
import org.flywaydb.core.api.callback.Event
import org.junit.jupiter.api.Test
import java.sql.Connection
import java.sql.SQLException
import java.sql.Statement
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.test.assertEquals

class FlywayCallbackTest {

    @Test
    fun `supports() should return true for AFTER_MIGRATE_ERROR event`() {
        // Given
        val callback = FlywayCallback()
        val context = mockk<Context>()
        
        // When
        val supportsEvent = callback.supports(Event.AFTER_MIGRATE_ERROR, context)
        
        // Then
        assertTrue(supportsEvent, "Callback should support AFTER_MIGRATE_ERROR event")
    }

    @Test
    fun `supports() should return false for BEFORE_MIGRATE event`() {
        // Given
        val callback = FlywayCallback()
        val context = mockk<Context>()
        
        // When
        val supportsEvent = callback.supports(Event.BEFORE_MIGRATE, context)
        
        // Then
        assertFalse(supportsEvent, "Callback should not support BEFORE_MIGRATE event")
    }

    @Test
    fun `supports() should return false for AFTER_MIGRATE event`() {
        // Given
        val callback = FlywayCallback()
        val context = mockk<Context>()
        
        // When
        val supportsEvent = callback.supports(Event.AFTER_MIGRATE, context)
        
        // Then
        assertFalse(supportsEvent, "Callback should not support AFTER_MIGRATE event")
    }

    @Test
    fun `supports() should return false for BEFORE_EACH_MIGRATE event`() {
        // Given
        val callback = FlywayCallback()
        val context = mockk<Context>()
        
        // When
        val supportsEvent = callback.supports(Event.BEFORE_EACH_MIGRATE, context)
        
        // Then
        assertFalse(supportsEvent, "Callback should not support BEFORE_EACH_MIGRATE event")
    }

    @Test
    fun `supports() should return false for AFTER_EACH_MIGRATE event`() {
        // Given
        val callback = FlywayCallback()
        val context = mockk<Context>()
        
        // When
        val supportsEvent = callback.supports(Event.AFTER_EACH_MIGRATE, context)
        
        // Then
        assertFalse(supportsEvent, "Callback should not support AFTER_EACH_MIGRATE event")
    }

    @Test
    fun `canHandleInTransaction() should return false for AFTER_MIGRATE_ERROR event`() {
        // Given
        val callback = FlywayCallback()
        val context = mockk<Context>()
        
        // When
        val canHandle = callback.canHandleInTransaction(Event.AFTER_MIGRATE_ERROR, context)
        
        // Then
        assertFalse(canHandle, "Callback should not handle AFTER_MIGRATE_ERROR in transaction")
    }

    @Test
    fun `canHandleInTransaction() should return false for other events`() {
        // Given
        val callback = FlywayCallback()
        val context = mockk<Context>()
        
        // When
        val canHandleBeforeMigrate = callback.canHandleInTransaction(Event.BEFORE_MIGRATE, context)
        val canHandleAfterMigrate = callback.canHandleInTransaction(Event.AFTER_MIGRATE, context)
        
        // Then
        assertFalse(canHandleBeforeMigrate, "Callback should not handle BEFORE_MIGRATE in transaction")
        assertFalse(canHandleAfterMigrate, "Callback should not handle AFTER_MIGRATE in transaction")
    }

    @Test
    fun `getCallbackName() should return correct name`() {
        // Given
        val callback = FlywayCallback()
        
        // When
        val name = callback.callbackName
        
        // Then
        assertEquals("Migration Failure Handler", name, "Callback name should be 'Migration Failure Handler'")
    }

    @Test
    fun `handle() should execute proper SQL cleanup statements for AFTER_MIGRATE_ERROR`() {
        // Given
        val callback = FlywayCallback()
        val context = mockk<Context>()
        val connection = mockk<Connection>()
        val statement = mockk<Statement>()
        
        every { context.connection } returns connection
        every { connection.createStatement() } returns statement
        every { statement.execute(any()) } returns true
        justRun { statement.close() }
        
        // When
        callback.handle(Event.AFTER_MIGRATE_ERROR, context)
        
        // Then
        verify { context.connection }
        verify { connection.createStatement() }
        verify { statement.execute("UPDATE flyway_schema_history SET checksum = NULL WHERE installed_rank > 0") }
        verify { statement.execute("DROP TABLE IF EXISTS schema_version CASCADE") }
        verify { statement.close() }
    }

    @Test
    fun `handle() should execute SQL statements in correct order`() {
        // Given
        val callback = FlywayCallback()
        val context = mockk<Context>()
        val connection = mockk<Connection>()
        val statement = mockk<Statement>()
        val sqlSlot = slot<String>()
        val capturedSqlStatements = mutableListOf<String>()
        
        every { context.connection } returns connection
        every { connection.createStatement() } returns statement
        every { statement.execute(capture(sqlSlot)) } answers {
            capturedSqlStatements.add(sqlSlot.captured)
            true
        }
        justRun { statement.close() }
        
        // When
        callback.handle(Event.AFTER_MIGRATE_ERROR, context)
        
        // Then
        assertEquals(2, capturedSqlStatements.size)
        assertEquals("UPDATE flyway_schema_history SET checksum = NULL WHERE installed_rank > 0", capturedSqlStatements[0])
        assertEquals("DROP TABLE IF EXISTS schema_version CASCADE", capturedSqlStatements[1])
    }

    @Test
    fun `should handle migration error gracefully when SQL execution fails`() {
        // Given
        val callback = FlywayCallback()
        val context = mockk<Context>()
        val connection = mockk<Connection>()
        val statement = mockk<Statement>()
        
        every { context.connection } returns connection
        every { connection.createStatement() } returns statement
        every { statement.execute("UPDATE flyway_schema_history SET checksum = NULL WHERE installed_rank > 0") } throws SQLException("Table not found")
        every { statement.execute("DROP TABLE IF EXISTS flyway_schema_history CASCADE") } returns true
        every { statement.execute("DROP TABLE IF EXISTS schema_version CASCADE") } returns true
        justRun { statement.close() }
        
        // When - should not throw exception
        callback.handle(Event.AFTER_MIGRATE_ERROR, context)
        
        // Then
        verify { context.connection }
        verify { connection.createStatement() }
        verify { statement.execute("UPDATE flyway_schema_history SET checksum = NULL WHERE installed_rank > 0") }
        verify { statement.execute("DROP TABLE IF EXISTS flyway_schema_history CASCADE") }
        verify { statement.execute("DROP TABLE IF EXISTS schema_version CASCADE") }
        verify { statement.close() }
    }

    @Test
    fun `should handle migration error gracefully when connection fails`() {
        // Given
        val callback = FlywayCallback()
        val context = mockk<Context>()
        
        every { context.connection } throws SQLException("Connection failed")
        
        // When - should not throw exception
        callback.handle(Event.AFTER_MIGRATE_ERROR, context)
        
        // Then
        verify { context.connection }
    }

    @Test
    fun `should not handle non-error events`() {
        // Given
        val callback = FlywayCallback()
        val context = mockk<Context>()
        
        // When
        callback.handle(Event.BEFORE_MIGRATE, context)
        
        // Then - no database operations should be performed
        verify(exactly = 0) { context.connection }
    }

    @Test
    fun `should capture SQL statements executed during cleanup`() {
        // Given
        val callback = FlywayCallback()
        val context = mockk<Context>()
        val connection = mockk<Connection>()
        val statement = mockk<Statement>()
        val sqlSlot = slot<String>()
        val capturedSqlStatements = mutableListOf<String>()
        
        every { context.connection } returns connection
        every { connection.createStatement() } returns statement
        every { statement.execute(capture(sqlSlot)) } answers {
            capturedSqlStatements.add(sqlSlot.captured)
            true
        }
        justRun { statement.close() }
        
        // When
        callback.handle(Event.AFTER_MIGRATE_ERROR, context)
        
        // Then
        assertEquals(2, capturedSqlStatements.size)
        assertTrue(capturedSqlStatements.contains("UPDATE flyway_schema_history SET checksum = NULL WHERE installed_rank > 0"))
        assertTrue(capturedSqlStatements.contains("DROP TABLE IF EXISTS schema_version CASCADE"))
    }
}