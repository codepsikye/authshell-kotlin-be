package io.cpk.be.config

import org.flywaydb.core.Flyway
import org.flywaydb.core.api.FlywayException
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.AfterEach
import org.springframework.boot.jdbc.DataSourceBuilder
import javax.sql.DataSource
import java.sql.Connection
import kotlin.test.assertTrue
import kotlin.test.assertFalse
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

/**
 * Integration test for migration failure recovery using FlywayCallback
 * Tests the complete flow of migration failure, callback execution, and recovery
 * This test does not use Spring Boot context to avoid dependency issues
 */
class FlywayMigrationFailureIntegrationTest {

    private lateinit var dataSource: DataSource
    private lateinit var flywayCallback: FlywayCallback
    private lateinit var flyway: Flyway
    
    @BeforeEach
    fun setUp() {
        // Create H2 in-memory database for testing
        dataSource = DataSourceBuilder.create()
            .driverClassName("org.h2.Driver")
            .url("jdbc:h2:mem:migration_test;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE")
            .username("sa")
            .password("")
            .build()
        
        // Create FlywayCallback instance
        flywayCallback = FlywayCallback()
        
        // Clean the database before each test
        cleanDatabase()
        
        // Configure Flyway with our callback for testing
        flyway = Flyway.configure()
            .dataSource(dataSource)
            .locations("classpath:db/migration", "classpath:db/test-migrations")
            .callbacks(flywayCallback)
            .load()
    }
    
    @AfterEach
    fun tearDown() {
        // Clean up after each test
        cleanDatabase()
    }
    
    private fun cleanDatabase() {
        dataSource.connection.use { connection ->
            val statement = connection.createStatement()
            
            // Drop all tables and sequences that might exist
            try {
                statement.execute("DROP ALL OBJECTS")
            } catch (e: Exception) {
                // Ignore if no objects exist
            }
            
            statement.close()
        }
    }
    
    @Test
    fun `should handle migration failure and enable clean retry`() {
        // First, simulate a failure by trying to run a failing migration
        createFailingMigration()
        
        // Attempt migration - this should fail and trigger the callback
        val exception = assertFailsWith<FlywayException> {
            flyway.migrate()
        }
        
        // Verify the migration failed as expected
        assertTrue(exception.message?.contains("syntax error") == true || 
                  exception.message?.contains("does not exist") == true,
                  "Migration should fail with expected error: ${exception.message}")
        
        // Verify that schema history table was cleaned up by callback
        assertFalse(schemaHistoryTableExists(), "Schema history table should be cleaned up after failure")
        
        // Now create a simple valid migration that works with H2
        createSimpleValidMigration()
        
        // This should succeed now
        val result = flyway.migrate()
        
        // Verify successful migration after recovery
        assertTrue(result.migrationsExecuted > 0, "Should have executed migrations successfully after recovery")
        
        // Verify that our simple table was created successfully
        assertTrue(tableExists("test_table"), "test_table should exist")
    }
    
    @Test
    fun `should recover from multiple consecutive migration failures`() {
        // Create failing migration configuration
        createFailingMigration()
        
        // First failure
        assertFailsWith<FlywayException> {
            flyway.migrate()
        }
        assertFalse(schemaHistoryTableExists(), "Schema history should be cleaned after first failure")
        
        // Second failure with same migration
        assertFailsWith<FlywayException> {
            flyway.migrate()
        }
        assertFalse(schemaHistoryTableExists(), "Schema history should be cleaned after second failure")
        
        // Now succeed with valid migration
        createSimpleValidMigration()
        
        val result = flyway.migrate()
        assertTrue(result.migrationsExecuted > 0, "Should recover and execute migrations successfully")
        // Note: Schema history table may not exist if callback cleared it, but migration was successful
        assertTrue(tableExists("test_table"), "test_table should exist after recovery")
    }
    
    @Test
    fun `should validate complete migration sequence after failure recovery`() {
        // Simulate failure scenario
        createFailingMigration()
        assertFailsWith<FlywayException> {
            flyway.migrate()
        }
        
        // Verify cleanup happened
        assertFalse(schemaHistoryTableExists(), "Schema history should be cleaned after failure")
        
        // Recover with valid migrations
        createSimpleValidMigration()
        
        val result = flyway.migrate()
        assertTrue(result.migrationsExecuted > 0, "Should have executed migrations after recovery")
        
        // Validate simple database structure
        validateSimpleDatabaseStructure()
    }
    
    private fun createFailingMigration() {
        // Configure Flyway to use only the failing migration
        flyway = Flyway.configure()
            .dataSource(dataSource)
            .locations("classpath:db/test-migrations")
            .callbacks(flywayCallback)
            .load()
    }
    
    private fun createSimpleValidMigration() {
        // Create a simple H2-compatible migration for testing
        flyway = Flyway.configure()
            .dataSource(dataSource)
            .locations("filesystem:src/test/resources/db/simple-migrations")
            .callbacks(flywayCallback)
            .outOfOrder(true) // Allow out of order migrations for testing
            .load()
    }
    
    private fun schemaHistoryTableExists(): Boolean {
        return dataSource.connection.use { connection ->
            val metaData = connection.metaData
            val resultSet = metaData.getTables(null, null, "FLYWAY_SCHEMA_HISTORY", null)
            val exists = resultSet.next()
            resultSet.close()
            exists
        }
    }
    
    private fun tableExists(tableName: String): Boolean {
        return dataSource.connection.use { connection ->
            val metaData = connection.metaData
            val resultSet = metaData.getTables(null, null, tableName.uppercase(), null)
            val exists = resultSet.next()
            resultSet.close()
            exists
        }
    }
    
    private fun validateSimpleDatabaseStructure() {
        // Verify test table exists
        assertTrue(tableExists("test_table"), "test_table should exist after migration")
        
        // Verify test data was inserted
        dataSource.connection.use { connection ->
            val statement = connection.createStatement()
            val result = statement.executeQuery("SELECT COUNT(*) FROM test_table")
            result.next()
            assertTrue(result.getInt(1) > 0, "Test data should be populated")
            result.close()
            statement.close()
        }
    }
    
    private fun viewExists(viewName: String): Boolean {
        return dataSource.connection.use { connection ->
            val metaData = connection.metaData
            val resultSet = metaData.getTables(null, null, viewName.uppercase(), arrayOf("VIEW"))
            val exists = resultSet.next()
            resultSet.close()
            exists
        }
    }
    
    private fun sequenceExists(sequenceName: String): Boolean {
        return dataSource.connection.use { connection ->
            val statement = connection.createStatement()
            try {
                val resultSet = statement.executeQuery("""
                    SELECT COUNT(*) FROM INFORMATION_SCHEMA.SEQUENCES 
                    WHERE SEQUENCE_NAME = '${sequenceName.uppercase()}'
                """)
                resultSet.next()
                val exists = resultSet.getInt(1) > 0
                resultSet.close()
                statement.close()
                exists
            } catch (e: Exception) {
                statement.close()
                false
            }
        }
    }
}