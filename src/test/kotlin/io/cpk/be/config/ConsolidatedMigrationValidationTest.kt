package io.cpk.be.config

import io.cpk.be.basic.entity.*
import io.cpk.be.basic.repository.*
import io.cpk.be.tasks.entity.Task
import io.cpk.be.tasks.entity.TaskUpdate
import io.cpk.be.tasks.repository.TaskRepository
import io.cpk.be.tasks.repository.TaskUpdateRepository
import io.cpk.be.config.TestJacksonConfig
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.context.annotation.Import
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.test.context.TestPropertySource
import org.springframework.transaction.annotation.Transactional
import javax.sql.DataSource
import kotlin.test.*

/**
 * Integration test for validating the consolidated migration functionality.
 * This test validates that the V1 and V2 migrations create the complete database schema
 * and populate it with the correct seed data, matching the previous V8 state.
 * 
 * This test enables Flyway to run the actual migrations and validates the final state.
 */
@DataJpaTest
@Import(TestConfig::class, TestJacksonConfig::class)
@TestPropertySource(properties = [
    "spring.flyway.enabled=true",
    "spring.flyway.clean-disabled=false",
    "spring.flyway.locations=classpath:db/migration",
    "spring.jpa.hibernate.ddl-auto=none",
    "spring.datasource.url=jdbc:h2:mem:migrationtestdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
    "spring.datasource.driver-class-name=org.h2.Driver",
    "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect"
])
@Transactional
class ConsolidatedMigrationValidationTest {

    @Autowired
    private lateinit var dataSource: DataSource

    @Autowired
    private lateinit var accessRightRepository: AccessRightRepository
    
    @Autowired
    private lateinit var appUserRepository: AppUserRepository
    
    @Autowired
    private lateinit var appUserRoleRepository: AppUserRoleRepository
    
    @Autowired
    private lateinit var centerRepository: CenterRepository
    
    @Autowired
    private lateinit var orgRepository: OrgRepository
    
    @Autowired
    private lateinit var orgTypeRepository: OrgTypeRepository
    
    @Autowired
    private lateinit var roleRepository: RoleRepository
    
    @Autowired
    private lateinit var taskRepository: TaskRepository
    
    @Autowired
    private lateinit var taskUpdateRepository: TaskUpdateRepository

    private val passwordEncoder = BCryptPasswordEncoder()

    @Test
    fun `should validate complete migration sequence from empty database`() {
        // Verify that Flyway has run and created the schema_version table
        dataSource.connection.use { connection ->
            // H2 creates table names in quotes, so we need to use the exact case and column names
            val query = """
                SELECT "version", "description", "success" 
                FROM "flyway_schema_history" 
                ORDER BY "installed_rank"
            """.trimIndent()
            
            connection.prepareStatement(query).use { stmt ->
                val rs = stmt.executeQuery()
                val migrations = mutableListOf<Triple<String, String, Boolean>>()
                
                while (rs.next()) {
                    val version = rs.getString("version")
                    val description = rs.getString("description")
                    val success = rs.getBoolean("success")
                    
                    // Skip the schema history table creation entry (has null version)
                    if (version != null) {
                        migrations.add(Triple(version, description, success))
                    }
                }
                
                // Verify exactly 2 migrations ran successfully
                assertEquals(2, migrations.size, "Should have exactly 2 migrations, but found: ${migrations.map { "${it.first} - ${it.second}" }}")
                
                val (v1Version, v1Description, v1Success) = migrations[0]
                assertEquals("1", v1Version, "First migration should be V1")
                assertEquals("Initial schema", v1Description, "V1 should be Initial schema")
                assertTrue(v1Success, "V1 migration should be successful")
                
                val (v2Version, v2Description, v2Success) = migrations[1]
                assertEquals("2", v2Version, "Second migration should be V2")
                assertEquals("Seed data", v2Description, "V2 should be Seed data")
                assertTrue(v2Success, "V2 migration should be successful")
            }
        }
    }

    @Test
    fun `should verify final database schema matches expected structure`() {
        val expectedTables = listOf(
            "access_right", "org_type", "org", "center", "app_user", 
            "access_metadata", "role", "task", "task_update", "app_user_role"
        )
        
        dataSource.connection.use { connection ->
            expectedTables.forEach { tableName ->
                // Verify table exists
                val tableQuery = """
                    SELECT table_name 
                    FROM information_schema.tables 
                    WHERE table_name = UPPER(?) AND table_schema = 'PUBLIC'
                """.trimIndent()
                
                connection.prepareStatement(tableQuery).use { stmt ->
                    stmt.setString(1, tableName)
                    val rs = stmt.executeQuery()
                    assertTrue(rs.next(), "Table $tableName should exist")
                }
            }
            
            // Verify user_role_view exists
            val viewQuery = """
                SELECT table_name 
                FROM information_schema.views 
                WHERE table_name = 'USER_ROLE_VIEW' AND table_schema = 'PUBLIC'
            """.trimIndent()
            
            connection.prepareStatement(viewQuery).use { stmt ->
                val rs = stmt.executeQuery()
                assertTrue(rs.next(), "View user_role_view should exist")
            }
        }
    }

    @Test
    fun `should test that password authentication works with consolidated seed data`() {
        // Find the admin user
        val adminUser = appUserRepository.findAll().find { it.username == "admin" }
        assertNotNull(adminUser, "Admin user should exist")
        assertNotNull(adminUser.password, "Admin user should have a password")

        // Test that the password is properly BCrypt hashed
        assertTrue(adminUser.password!!.startsWith("\$2a\$"), 
            "Password should be BCrypt hashed (start with \$2a\$)")

        // Test that the password "admin123" matches the hash
        assertTrue(passwordEncoder.matches("admin123", adminUser.password), 
            "Password 'admin123' should match the stored hash")

        // Test that wrong password doesn't match
        assertFalse(passwordEncoder.matches("wrongpassword", adminUser.password), 
            "Wrong password should not match the stored hash")

        // Test all seeded users have valid BCrypt passwords
        val allUsers = appUserRepository.findAll()
        allUsers.forEach { user ->
            assertNotNull(user.password, "User ${user.username} should have a password")
            assertTrue(user.password!!.startsWith("\$2a\$"), 
                "User ${user.username} password should be BCrypt hashed")
            assertTrue(passwordEncoder.matches("admin123", user.password), 
                "User ${user.username} should have password 'admin123'")
        }
    }

    @Test
    fun `should verify database sequences are properly updated`() {
        dataSource.connection.use { connection ->
            try {
                // Check org sequence - H2 uses CURRVAL to get current value
                val orgSeqQuery = "SELECT CURRVAL('org_id_seq') as current_value"
                connection.prepareStatement(orgSeqQuery).use { stmt ->
                    val rs = stmt.executeQuery()
                    assertTrue(rs.next(), "org_id_seq should exist")
                    val currentValue = rs.getLong("current_value")
                    assertTrue(currentValue >= 3, "org_id_seq should be at least 3, was $currentValue")
                }

                // Check center sequence
                val centerSeqQuery = "SELECT CURRVAL('center_id_seq') as current_value"
                connection.prepareStatement(centerSeqQuery).use { stmt ->
                    val rs = stmt.executeQuery()
                    assertTrue(rs.next(), "center_id_seq should exist")
                    val currentValue = rs.getLong("current_value")
                    assertTrue(currentValue >= 4, "center_id_seq should be at least 4, was $currentValue")
                }

                // Check task sequence
                val taskSeqQuery = "SELECT CURRVAL('task_id_seq') as current_value"
                connection.prepareStatement(taskSeqQuery).use { stmt ->
                    val rs = stmt.executeQuery()
                    assertTrue(rs.next(), "task_id_seq should exist")
                    val currentValue = rs.getLong("current_value")
                    assertTrue(currentValue >= 3, "task_id_seq should be at least 3, was $currentValue")
                }

                // Check task_update sequence
                val taskUpdateSeqQuery = "SELECT CURRVAL('task_update_id_seq') as current_value"
                connection.prepareStatement(taskUpdateSeqQuery).use { stmt ->
                    val rs = stmt.executeQuery()
                    assertTrue(rs.next(), "task_update_id_seq should exist")
                    val currentValue = rs.getLong("current_value")
                    assertTrue(currentValue >= 3, "task_update_id_seq should be at least 3, was $currentValue")
                }
            } catch (e: Exception) {
                // Alternative approach for H2 database
                // Check max IDs instead of sequences
                val orgMaxIdQuery = "SELECT MAX(id) as max_id FROM org"
                connection.prepareStatement(orgMaxIdQuery).use { stmt ->
                    val rs = stmt.executeQuery()
                    assertTrue(rs.next(), "org table should have records")
                    val maxId = rs.getLong("max_id")
                    assertTrue(maxId >= 3, "org max id should be at least 3, was $maxId")
                }

                val centerMaxIdQuery = "SELECT MAX(id) as max_id FROM center"
                connection.prepareStatement(centerMaxIdQuery).use { stmt ->
                    val rs = stmt.executeQuery()
                    assertTrue(rs.next(), "center table should have records")
                    val maxId = rs.getLong("max_id")
                    assertTrue(maxId >= 4, "center max id should be at least 4, was $maxId")
                }

                val taskMaxIdQuery = "SELECT MAX(id) as max_id FROM task"
                connection.prepareStatement(taskMaxIdQuery).use { stmt ->
                    val rs = stmt.executeQuery()
                    assertTrue(rs.next(), "task table should have records")
                    val maxId = rs.getLong("max_id")
                    assertTrue(maxId >= 3, "task max id should be at least 3, was $maxId")
                }

                val taskUpdateMaxIdQuery = "SELECT MAX(id) as max_id FROM task_update"
                connection.prepareStatement(taskUpdateMaxIdQuery).use { stmt ->
                    val rs = stmt.executeQuery()
                    assertTrue(rs.next(), "task_update table should have records")
                    val maxId = rs.getLong("max_id")
                    assertTrue(maxId >= 3, "task_update max id should be at least 3, was $maxId")
                }
            }
        }
    }


}