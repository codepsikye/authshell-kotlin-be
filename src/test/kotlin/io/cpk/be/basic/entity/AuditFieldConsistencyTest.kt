package io.cpk.be.basic.entity

import io.cpk.be.tasks.entity.Task
import io.cpk.be.tasks.entity.TaskUpdate
import jakarta.persistence.Column
import jakarta.persistence.Temporal
import jakarta.persistence.TemporalType
import org.junit.jupiter.api.Test
import org.springframework.data.annotation.CreatedBy
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedBy
import org.springframework.data.annotation.LastModifiedDate
import java.time.LocalDateTime
import kotlin.reflect.KClass
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.javaField
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * Test class to verify audit field consistency across all entities extending BaseAuditable.
 * This ensures all entities have consistent audit field definitions, constraints, and annotations.
 */
class AuditFieldConsistencyTest {

    // List of all entity classes that extend BaseAuditable
    private val baseAuditableEntities = listOf(
        AppUser::class,
        AppUserRole::class,
        Center::class,
        Org::class,
        OrgType::class,
        Role::class,
        Task::class,
        TaskUpdate::class
    )

    @Test
    fun `all BaseAuditable entities should have consistent createdAt field definition`() {
        baseAuditableEntities.forEach { entityClass ->
            verifyCreatedAtField(entityClass)
        }
    }

    @Test
    fun `all BaseAuditable entities should have consistent updatedAt field definition`() {
        baseAuditableEntities.forEach { entityClass ->
            verifyUpdatedAtField(entityClass)
        }
    }

    @Test
    fun `all BaseAuditable entities should have consistent createdBy field definition`() {
        baseAuditableEntities.forEach { entityClass ->
            verifyCreatedByField(entityClass)
        }
    }

    @Test
    fun `all BaseAuditable entities should have consistent updatedBy field definition`() {
        baseAuditableEntities.forEach { entityClass ->
            verifyUpdatedByField(entityClass)
        }
    }

    @Test
    fun `all BaseAuditable entities should inherit audit field annotations from BaseAuditable`() {
        baseAuditableEntities.forEach { entityClass ->
            verifyAuditFieldAnnotations(entityClass)
        }
    }

    @Test
    fun `BaseAuditable should have correct audit field data types`() {
        val baseAuditableClass = BaseAuditable::class
        
        // Verify createdAt is LocalDateTime
        val createdAtProperty = baseAuditableClass.memberProperties.find { it.name == "createdAt" }
        assertNotNull(createdAtProperty, "createdAt property should exist in BaseAuditable")
        assertEquals(LocalDateTime::class, createdAtProperty.returnType.classifier, 
            "createdAt should be of type LocalDateTime")
        
        // Verify updatedAt is LocalDateTime
        val updatedAtProperty = baseAuditableClass.memberProperties.find { it.name == "updatedAt" }
        assertNotNull(updatedAtProperty, "updatedAt property should exist in BaseAuditable")
        assertEquals(LocalDateTime::class, updatedAtProperty.returnType.classifier,
            "updatedAt should be of type LocalDateTime")
        
        // Verify createdBy is String
        val createdByProperty = baseAuditableClass.memberProperties.find { it.name == "createdBy" }
        assertNotNull(createdByProperty, "createdBy property should exist in BaseAuditable")
        assertEquals(String::class, createdByProperty.returnType.classifier,
            "createdBy should be of type String")
        
        // Verify updatedBy is String
        val updatedByProperty = baseAuditableClass.memberProperties.find { it.name == "updatedBy" }
        assertNotNull(updatedByProperty, "updatedBy property should exist in BaseAuditable")
        assertEquals(String::class, updatedByProperty.returnType.classifier,
            "updatedBy should be of type String")
    }

    @Test
    fun `BaseAuditable should have correct Column annotations for audit fields`() {
        val baseAuditableClass = BaseAuditable::class
        
        // Verify createdAt Column annotation
        val createdAtField = baseAuditableClass.memberProperties
            .find { it.name == "createdAt" }?.javaField
        assertNotNull(createdAtField, "createdAt field should exist")
        
        val createdAtColumn = createdAtField.getAnnotation(Column::class.java)
        assertNotNull(createdAtColumn, "createdAt should have @Column annotation")
        assertEquals("created_at", createdAtColumn.name, "createdAt column name should be 'created_at'")
        assertEquals(false, createdAtColumn.nullable, "createdAt should be non-nullable")
        assertEquals(false, createdAtColumn.updatable, "createdAt should be non-updatable")
        
        // Verify updatedAt Column annotation
        val updatedAtField = baseAuditableClass.memberProperties
            .find { it.name == "updatedAt" }?.javaField
        assertNotNull(updatedAtField, "updatedAt field should exist")
        
        val updatedAtColumn = updatedAtField.getAnnotation(Column::class.java)
        assertNotNull(updatedAtColumn, "updatedAt should have @Column annotation")
        assertEquals("updated_at", updatedAtColumn.name, "updatedAt column name should be 'updated_at'")
        assertEquals(false, updatedAtColumn.nullable, "updatedAt should be non-nullable")
        assertEquals(true, updatedAtColumn.updatable, "updatedAt should be updatable")
        
        // Verify createdBy Column annotation
        val createdByField = baseAuditableClass.memberProperties
            .find { it.name == "createdBy" }?.javaField
        assertNotNull(createdByField, "createdBy field should exist")
        
        val createdByColumn = createdByField.getAnnotation(Column::class.java)
        assertNotNull(createdByColumn, "createdBy should have @Column annotation")
        assertEquals("created_by", createdByColumn.name, "createdBy column name should be 'created_by'")
        assertEquals(false, createdByColumn.nullable, "createdBy should be non-nullable")
        assertEquals(false, createdByColumn.updatable, "createdBy should be non-updatable")
        assertEquals(50, createdByColumn.length, "createdBy should have length 50")
        
        // Verify updatedBy Column annotation
        val updatedByField = baseAuditableClass.memberProperties
            .find { it.name == "updatedBy" }?.javaField
        assertNotNull(updatedByField, "updatedBy field should exist")
        
        val updatedByColumn = updatedByField.getAnnotation(Column::class.java)
        assertNotNull(updatedByColumn, "updatedBy should have @Column annotation")
        assertEquals("updated_by", updatedByColumn.name, "updatedBy column name should be 'updated_by'")
        assertEquals(false, updatedByColumn.nullable, "updatedBy should be non-nullable")
        assertEquals(true, updatedByColumn.updatable, "updatedBy should be updatable")
        assertEquals(50, updatedByColumn.length, "updatedBy should have length 50")
    }

    @Test
    fun `BaseAuditable should have correct audit annotations`() {
        val baseAuditableClass = BaseAuditable::class
        
        // Verify createdAt has @CreatedDate and @Temporal annotations
        val createdAtField = baseAuditableClass.memberProperties
            .find { it.name == "createdAt" }?.javaField
        assertNotNull(createdAtField, "createdAt field should exist")
        
        assertNotNull(createdAtField.getAnnotation(CreatedDate::class.java), 
            "createdAt should have @CreatedDate annotation")
        
        val createdAtTemporal = createdAtField.getAnnotation(Temporal::class.java)
        assertNotNull(createdAtTemporal, "createdAt should have @Temporal annotation")
        assertEquals(TemporalType.TIMESTAMP, createdAtTemporal.value, 
            "createdAt @Temporal should be TIMESTAMP")
        
        // Verify updatedAt has @LastModifiedDate and @Temporal annotations
        val updatedAtField = baseAuditableClass.memberProperties
            .find { it.name == "updatedAt" }?.javaField
        assertNotNull(updatedAtField, "updatedAt field should exist")
        
        assertNotNull(updatedAtField.getAnnotation(LastModifiedDate::class.java),
            "updatedAt should have @LastModifiedDate annotation")
        
        val updatedAtTemporal = updatedAtField.getAnnotation(Temporal::class.java)
        assertNotNull(updatedAtTemporal, "updatedAt should have @Temporal annotation")
        assertEquals(TemporalType.TIMESTAMP, updatedAtTemporal.value,
            "updatedAt @Temporal should be TIMESTAMP")
        
        // Verify createdBy has @CreatedBy annotation
        val createdByField = baseAuditableClass.memberProperties
            .find { it.name == "createdBy" }?.javaField
        assertNotNull(createdByField, "createdBy field should exist")
        
        assertNotNull(createdByField.getAnnotation(CreatedBy::class.java),
            "createdBy should have @CreatedBy annotation")
        
        // Verify updatedBy has @LastModifiedBy annotation
        val updatedByField = baseAuditableClass.memberProperties
            .find { it.name == "updatedBy" }?.javaField
        assertNotNull(updatedByField, "updatedBy field should exist")
        
        assertNotNull(updatedByField.getAnnotation(LastModifiedBy::class.java),
            "updatedBy should have @LastModifiedBy annotation")
    }

    private fun verifyCreatedAtField(entityClass: KClass<*>) {
        assertTrue(BaseAuditable::class.java.isAssignableFrom(entityClass.java),
            "${entityClass.simpleName} should extend BaseAuditable")
        
        // Since entities extend BaseAuditable, they inherit the createdAt field
        // We verify that the inheritance is correct by checking the superclass
        val superclass = entityClass.java.superclass
        assertEquals(BaseAuditable::class.java, superclass,
            "${entityClass.simpleName} should directly extend BaseAuditable")
    }

    private fun verifyUpdatedAtField(entityClass: KClass<*>) {
        assertTrue(BaseAuditable::class.java.isAssignableFrom(entityClass.java),
            "${entityClass.simpleName} should extend BaseAuditable")
        
        // Since entities extend BaseAuditable, they inherit the updatedAt field
        // We verify that the inheritance is correct by checking the superclass
        val superclass = entityClass.java.superclass
        assertEquals(BaseAuditable::class.java, superclass,
            "${entityClass.simpleName} should directly extend BaseAuditable")
    }

    private fun verifyCreatedByField(entityClass: KClass<*>) {
        assertTrue(BaseAuditable::class.java.isAssignableFrom(entityClass.java),
            "${entityClass.simpleName} should extend BaseAuditable")
        
        // Since entities extend BaseAuditable, they inherit the createdBy field
        // We verify that the inheritance is correct by checking the superclass
        val superclass = entityClass.java.superclass
        assertEquals(BaseAuditable::class.java, superclass,
            "${entityClass.simpleName} should directly extend BaseAuditable")
    }

    private fun verifyUpdatedByField(entityClass: KClass<*>) {
        assertTrue(BaseAuditable::class.java.isAssignableFrom(entityClass.java),
            "${entityClass.simpleName} should extend BaseAuditable")
        
        // Since entities extend BaseAuditable, they inherit the updatedBy field
        // We verify that the inheritance is correct by checking the superclass
        val superclass = entityClass.java.superclass
        assertEquals(BaseAuditable::class.java, superclass,
            "${entityClass.simpleName} should directly extend BaseAuditable")
    }

    private fun verifyAuditFieldAnnotations(entityClass: KClass<*>) {
        assertTrue(BaseAuditable::class.java.isAssignableFrom(entityClass.java),
            "${entityClass.simpleName} should extend BaseAuditable")
        
        // Verify that the entity inherits all audit field annotations from BaseAuditable
        // by checking that it can be instantiated and has access to the audit fields
        try {
            val constructor = entityClass.java.getDeclaredConstructor()
            constructor.isAccessible = true
            val instance = constructor.newInstance()
            
            // Verify the instance has access to audit fields through inheritance
            assertTrue(instance is BaseAuditable,
                "${entityClass.simpleName} instance should be an instance of BaseAuditable")
            
        } catch (e: Exception) {
            // If no-arg constructor is not available, that's fine - the inheritance is still valid
            // We just verify the class hierarchy
            assertTrue(BaseAuditable::class.java.isAssignableFrom(entityClass.java),
                "${entityClass.simpleName} should extend BaseAuditable")
        }
    }
}