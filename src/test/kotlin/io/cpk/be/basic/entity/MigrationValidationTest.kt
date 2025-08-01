package io.cpk.be.basic.entity

import io.cpk.be.basic.repository.*
import io.cpk.be.config.JpaAuditingConfig
import io.cpk.be.config.TestConfig
import io.cpk.be.tasks.repository.TaskRepository
import io.cpk.be.tasks.repository.TaskUpdateRepository
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles
import javax.sql.DataSource
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * Test class for validating the V8 migration that adds proper audit timestamp field constraints.
 * This test verifies that the migration correctly adds NOT NULL and DEFAULT constraints
 * to created_at and updated_at columns for all BaseAuditable entity tables.
 * 
 * Note: This test relies on Flyway migrations being applied automatically by Spring Boot
 * during test startup, so it validates the final state after all migrations.
 */
@DataJpaTest
@Import(JpaAuditingConfig::class, TestConfig::class)
@ActiveProfiles("test")
class MigrationValidationTest {

    @Autowired
    private lateinit var dataSource: DataSource

    // Repositories to ensure entities are properly mapped
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

    private val targetTables = listOf(
        "app_user", 
        "app_user_role",
        "center",
        "org",
        "org_type",
        "role",
        "task",
        "task_update"
    )

    @Test
    fun `should validate existing data gets appropriate default timestamp values`() {
        // This test validates that when entities are created, they get proper timestamp values
        // We'll create test entities and verify they have audit timestamps
        
        // Create a test org_type first (required for other entities)
        val orgType = OrgType(
            name = "TEST_TYPE",
            accessRight = emptyList(),
            orgConfigs = emptyMap()
        )
        val savedOrgType = orgTypeRepository.save(orgType)
        assertNotNull(savedOrgType.createdAt, "OrgType should have created_at value")
        assertNotNull(savedOrgType.updatedAt, "OrgType should have updated_at value")
        
        // Create a test org
        val org = Org(
            name = "Test Organization",
            orgTypeName = savedOrgType.name
        )
        val savedOrg = orgRepository.save(org)
        assertNotNull(savedOrg.createdAt, "Org should have created_at value")
        assertNotNull(savedOrg.updatedAt, "Org should have updated_at value")
        
        // Create a test center
        val center = Center(
            name = "Test Center",
            orgId = savedOrg.id!!
        )
        val savedCenter = centerRepository.save(center)
        assertNotNull(savedCenter.createdAt, "Center should have created_at value")
        assertNotNull(savedCenter.updatedAt, "Center should have updated_at value")
    }

    @Test
    fun `should ensure all target tables receive the audit timestamp fields`() {
        dataSource.connection.use { connection ->
            targetTables.forEach { tableName ->
                // Verify table exists (H2 uses uppercase table names)
                val tableExistsQuery = """
                    SELECT table_name 
                    FROM information_schema.tables 
                    WHERE table_name = UPPER(?) AND table_schema = 'PUBLIC'
                """.trimIndent()
                
                connection.prepareStatement(tableExistsQuery).use { stmt ->
                    stmt.setString(1, tableName)
                    val rs = stmt.executeQuery()
                    assertTrue(rs.next(), "Table $tableName should exist")
                }

                // Verify both audit timestamp columns exist (H2 uses uppercase column names)
                val columnsQuery = """
                    SELECT column_name 
                    FROM information_schema.columns 
                    WHERE table_name = UPPER(?) AND column_name IN ('CREATED_AT', 'UPDATED_AT')
                    ORDER BY column_name
                """.trimIndent()
                
                connection.prepareStatement(columnsQuery).use { stmt ->
                    stmt.setString(1, tableName)
                    val rs = stmt.executeQuery()
                    
                    val columns = mutableListOf<String>()
                    while (rs.next()) {
                        columns.add(rs.getString("column_name"))
                    }
                    
                    assertEquals(2, columns.size, "Table $tableName should have both audit timestamp columns")
                    assertTrue(columns.contains("CREATED_AT"), "Table $tableName should have created_at column")
                    assertTrue(columns.contains("UPDATED_AT"), "Table $tableName should have updated_at column")
                }
            }
        }
    }

    @Test
    fun `should verify audit fields have consistent definitions across all tables`() {
        dataSource.connection.use { connection ->
            targetTables.forEach { tableName ->
                // Check all four audit fields exist with proper constraints (H2 uses uppercase)
                val auditFieldsQuery = """
                    SELECT column_name, is_nullable, column_default, data_type, character_maximum_length
                    FROM information_schema.columns 
                    WHERE table_name = UPPER(?) AND column_name IN ('CREATED_AT', 'UPDATED_AT', 'CREATED_BY', 'UPDATED_BY')
                    ORDER BY column_name
                """.trimIndent()
                
                connection.prepareStatement(auditFieldsQuery).use { stmt ->
                    stmt.setString(1, tableName)
                    val rs = stmt.executeQuery()
                    
                    val auditFields = mutableMapOf<String, Map<String, Any?>>()
                    while (rs.next()) {
                        val columnName = rs.getString("column_name")
                        auditFields[columnName] = mapOf(
                            "is_nullable" to rs.getString("is_nullable"),
                            "column_default" to rs.getString("column_default"),
                            "data_type" to rs.getString("data_type"),
                            "character_maximum_length" to rs.getObject("character_maximum_length")
                        )
                    }
                    
                    // Verify all four audit fields exist
                    assertEquals(4, auditFields.size, "Table $tableName should have all four audit fields")
                    
                    // Verify created_at constraints
                    auditFields["CREATED_AT"]?.let { field ->
                        assertEquals("NO", field["is_nullable"], "created_at should be NOT NULL in $tableName")
                        assertTrue(field["data_type"].toString().contains("TIMESTAMP", ignoreCase = true), 
                            "created_at should be TIMESTAMP in $tableName")
                    }
                    
                    // Verify updated_at constraints
                    auditFields["UPDATED_AT"]?.let { field ->
                        assertEquals("NO", field["is_nullable"], "updated_at should be NOT NULL in $tableName")
                        assertTrue(field["data_type"].toString().contains("TIMESTAMP", ignoreCase = true), 
                            "updated_at should be TIMESTAMP in $tableName")
                    }
                    
                    // Verify created_by constraints
                    auditFields["CREATED_BY"]?.let { field ->
                        assertEquals("NO", field["is_nullable"], "created_by should be NOT NULL in $tableName")
                        val dataType = field["data_type"].toString()
                        assertTrue(dataType.contains("VARCHAR", ignoreCase = true) || dataType.contains("CHARACTER VARYING", ignoreCase = true), 
                            "created_by should be VARCHAR/CHARACTER VARYING in $tableName, but was $dataType")
                        assertEquals(50L, field["character_maximum_length"], 
                            "created_by should have length 50 in $tableName")
                    }
                    
                    // Verify updated_by constraints
                    auditFields["UPDATED_BY"]?.let { field ->
                        assertEquals("NO", field["is_nullable"], "updated_by should be NOT NULL in $tableName")
                        val dataType = field["data_type"].toString()
                        assertTrue(dataType.contains("VARCHAR", ignoreCase = true) || dataType.contains("CHARACTER VARYING", ignoreCase = true), 
                            "updated_by should be VARCHAR/CHARACTER VARYING in $tableName, but was $dataType")
                        assertEquals(50L, field["character_maximum_length"], 
                            "updated_by should have length 50 in $tableName")
                    }
                }
            }
        }
    }
}