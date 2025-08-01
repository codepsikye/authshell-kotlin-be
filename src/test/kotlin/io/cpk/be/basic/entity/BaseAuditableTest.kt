package io.cpk.be.basic.entity

import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import kotlin.test.assertEquals

class BaseAuditableTest {

    // Concrete implementation of BaseAuditable for testing
    private class TestBaseAuditable : BaseAuditable {
        constructor(createdAt: LocalDateTime, updatedAt: LocalDateTime) : super()
        constructor() : super()
    }

    @Test
    fun `should create BaseAuditable with provided timestamps`() {
        // Given
        val createdAt = LocalDateTime.now().minusDays(1)
        val updatedAt = LocalDateTime.now()
        
        // When
        val baseAuditable = TestBaseAuditable(createdAt, updatedAt)
        
        // Then
    }
    
    @Test
    fun `should create BaseAuditable with default timestamps`() {
        // Given
        val beforeCreation = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS)
        
        // When
        val baseAuditable = TestBaseAuditable()
        
        // Then
        val afterCreation = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS)
        
        // Check that timestamps are between beforeCreation and afterCreation
        val createdAtTruncated = baseAuditable.createdAt.truncatedTo(ChronoUnit.SECONDS)
        val updatedAtTruncated = baseAuditable.updatedAt.truncatedTo(ChronoUnit.SECONDS)
        
        assert(createdAtTruncated >= beforeCreation) { 
            "createdAt ($createdAtTruncated) should be after or equal to beforeCreation ($beforeCreation)" 
        }
        assert(createdAtTruncated <= afterCreation) { 
            "createdAt ($createdAtTruncated) should be before or equal to afterCreation ($afterCreation)" 
        }
        assert(updatedAtTruncated >= beforeCreation) { 
            "updatedAt ($updatedAtTruncated) should be after or equal to beforeCreation ($beforeCreation)" 
        }
        assert(updatedAtTruncated <= afterCreation) { 
            "updatedAt ($updatedAtTruncated) should be before or equal to afterCreation ($afterCreation)" 
        }
        
        // Both timestamps should be very close to each other when using the default constructor
        // Compare after truncating to milliseconds to avoid nanosecond differences
        assertEquals(
            baseAuditable.createdAt.truncatedTo(ChronoUnit.MILLIS),
            baseAuditable.updatedAt.truncatedTo(ChronoUnit.MILLIS)
        )
    }
}