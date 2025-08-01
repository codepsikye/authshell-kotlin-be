package io.cpk.be.util

import org.junit.jupiter.api.Test
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Unit tests for PageUtils
 */
class PageUtilsTest {

    @Test
    fun `should convert page to page response`() {
        // Given
        val content = listOf("item1", "item2", "item3")
        val pageable = PageRequest.of(0, 10)
        val page = PageImpl(content, pageable, content.size.toLong())

        // When
        val pageResponse = PageUtils.toPageResponse(page)

        // Then
        assertEquals(content, pageResponse.content)
        assertEquals(0, pageResponse.page)
        assertEquals(10, pageResponse.size)
        assertEquals(3, pageResponse.totalElements)
        assertEquals(1, pageResponse.totalPages)
        assertTrue(pageResponse.first)
        assertTrue(pageResponse.last)
        assertFalse(pageResponse.empty)
    }

    @Test
    fun `should convert empty page to page response`() {
        // Given
        val content = emptyList<String>()
        val pageable = PageRequest.of(0, 10)
        val page = PageImpl(content, pageable, content.size.toLong())

        // When
        val pageResponse = PageUtils.toPageResponse(page)

        // Then
        assertEquals(content, pageResponse.content)
        assertEquals(0, pageResponse.page)
        assertEquals(10, pageResponse.size)
        assertEquals(0, pageResponse.totalElements)
        assertEquals(0, pageResponse.totalPages)
        assertTrue(pageResponse.first)
        assertTrue(pageResponse.last)
        assertTrue(pageResponse.empty)
    }

    @Test
    fun `should convert page with multiple pages to page response`() {
        // Given
        val content = listOf("item1", "item2", "item3")
        val pageable = PageRequest.of(0, 2) // 2 items per page
        val page = PageImpl(content.take(2), pageable, content.size.toLong())

        // When
        val pageResponse = PageUtils.toPageResponse(page)

        // Then
        assertEquals(content.take(2), pageResponse.content)
        assertEquals(0, pageResponse.page)
        assertEquals(2, pageResponse.size)
        assertEquals(3, pageResponse.totalElements)
        assertEquals(2, pageResponse.totalPages)
        assertTrue(pageResponse.first)
        assertFalse(pageResponse.last)
        assertFalse(pageResponse.empty)
    }

    @Test
    fun `should convert last page to page response`() {
        // Given
        val content = listOf("item1", "item2", "item3")
        val pageable = PageRequest.of(1, 2) // 2 items per page, second page
        val page = PageImpl(content.drop(2), pageable, content.size.toLong())

        // When
        val pageResponse = PageUtils.toPageResponse(page)

        // Then
        assertEquals(content.drop(2), pageResponse.content)
        assertEquals(1, pageResponse.page)
        assertEquals(2, pageResponse.size)
        assertEquals(3, pageResponse.totalElements)
        assertEquals(2, pageResponse.totalPages)
        assertFalse(pageResponse.first)
        assertTrue(pageResponse.last)
        assertFalse(pageResponse.empty)
    }
}