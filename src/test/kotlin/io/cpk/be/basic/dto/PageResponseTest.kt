package io.cpk.be.basic.dto

import org.junit.jupiter.api.Test
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class PageResponseTest {

    @Test
    fun `should create PageResponse with all parameters`() {
        // Given
        val content = listOf("item1", "item2", "item3")
        val page = 0
        val size = 10
        val totalElements = 3L
        val totalPages = 1
        val first = true
        val last = true
        val empty = false
        
        // When
        val pageResponse = PageResponse(
            content = content,
            page = page,
            size = size,
            totalElements = totalElements,
            totalPages = totalPages,
            first = first,
            last = last,
            empty = empty
        )
        
        // Then
        assertEquals(content, pageResponse.content)
        assertEquals(page, pageResponse.page)
        assertEquals(size, pageResponse.size)
        assertEquals(totalElements, pageResponse.totalElements)
        assertEquals(totalPages, pageResponse.totalPages)
        assertEquals(first, pageResponse.first)
        assertEquals(last, pageResponse.last)
        assertEquals(empty, pageResponse.empty)
    }
    
    @Test
    fun `should correctly implement equals and hashCode`() {
        // Given
        val pageResponse1 = PageResponse(
            content = listOf("item1", "item2"),
            page = 0,
            size = 10,
            totalElements = 2L,
            totalPages = 1,
            first = true,
            last = true,
            empty = false
        )
        
        val pageResponse2 = PageResponse(
            content = listOf("item1", "item2"),
            page = 0,
            size = 10,
            totalElements = 2L,
            totalPages = 1,
            first = true,
            last = true,
            empty = false
        )
        
        val pageResponse3 = PageResponse(
            content = listOf("item3", "item4"),
            page = 0,
            size = 10,
            totalElements = 2L,
            totalPages = 1,
            first = true,
            last = true,
            empty = false
        )
        
        // Then
        assertEquals(pageResponse1, pageResponse2)
        assertEquals(pageResponse1.hashCode(), pageResponse2.hashCode())
        assertNotEquals(pageResponse1, pageResponse3)
        assertNotEquals(pageResponse1.hashCode(), pageResponse3.hashCode())
    }
    
    @Test
    fun `should correctly implement copy`() {
        // Given
        val pageResponse = PageResponse(
            content = listOf("item1", "item2"),
            page = 0,
            size = 10,
            totalElements = 2L,
            totalPages = 1,
            first = true,
            last = true,
            empty = false
        )
        
        // When
        val copied = pageResponse.copy(
            content = listOf("item3", "item4"),
            page = 1
        )
        
        // Then
        assertEquals(listOf("item3", "item4"), copied.content)
        assertEquals(1, copied.page)
        assertEquals(10, copied.size)
        assertEquals(2L, copied.totalElements)
        assertEquals(1, copied.totalPages)
        assertEquals(true, copied.first)
        assertEquals(true, copied.last)
        assertEquals(false, copied.empty)
    }
    
    @Test
    fun `should create PageResponse from Spring Page - first page`() {
        // Given
        val content = listOf("item1", "item2")
        val pageable = PageRequest.of(0, 2)
        val total = 5L
        val page = PageImpl(content, pageable, total)
        
        // When
        val pageResponse = PageResponse.from(page)
        
        // Then
        assertEquals(content, pageResponse.content)
        assertEquals(0, pageResponse.page)
        assertEquals(2, pageResponse.size)
        assertEquals(5L, pageResponse.totalElements)
        assertEquals(3, pageResponse.totalPages)
        assertTrue(pageResponse.first)
        assertFalse(pageResponse.last)
        assertFalse(pageResponse.empty)
    }
    
    @Test
    fun `should create PageResponse from Spring Page - middle page`() {
        // Given
        val content = listOf("item3", "item4")
        val pageable = PageRequest.of(1, 2)
        val total = 5L
        val page = PageImpl(content, pageable, total)
        
        // When
        val pageResponse = PageResponse.from(page)
        
        // Then
        assertEquals(content, pageResponse.content)
        assertEquals(1, pageResponse.page)
        assertEquals(2, pageResponse.size)
        assertEquals(5L, pageResponse.totalElements)
        assertEquals(3, pageResponse.totalPages)
        assertFalse(pageResponse.first)
        assertFalse(pageResponse.last)
        assertFalse(pageResponse.empty)
    }
    
    @Test
    fun `should create PageResponse from Spring Page - last page`() {
        // Given
        val content = listOf("item5")
        val pageable = PageRequest.of(2, 2)
        val total = 5L
        val page = PageImpl(content, pageable, total)
        
        // When
        val pageResponse = PageResponse.from(page)
        
        // Then
        assertEquals(content, pageResponse.content)
        assertEquals(2, pageResponse.page)
        assertEquals(2, pageResponse.size)
        assertEquals(5L, pageResponse.totalElements)
        assertEquals(3, pageResponse.totalPages)
        assertFalse(pageResponse.first)
        assertTrue(pageResponse.last)
        assertFalse(pageResponse.empty)
    }
    
    @Test
    fun `should create PageResponse from empty Spring Page`() {
        // Given
        val content = emptyList<String>()
        val pageable = PageRequest.of(0, 10)
        val total = 0L
        val page = PageImpl(content, pageable, total)
        
        // When
        val pageResponse = PageResponse.from(page)
        
        // Then
        assertEquals(content, pageResponse.content)
        assertEquals(0, pageResponse.page)
        assertEquals(10, pageResponse.size)
        assertEquals(0L, pageResponse.totalElements)
        assertEquals(0, pageResponse.totalPages)
        assertTrue(pageResponse.first)
        assertTrue(pageResponse.last)
        assertTrue(pageResponse.empty)
    }
}