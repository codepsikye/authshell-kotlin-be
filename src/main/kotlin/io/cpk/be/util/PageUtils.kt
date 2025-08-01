package io.cpk.be.util

import io.cpk.be.basic.dto.PageResponse
import org.springframework.data.domain.Page

/**
 * Utility class for working with pagination.
 */
object PageUtils {

    /**
     * Converts a Spring Data Page object to a PageResponse.
     *
     * @param page the Spring Data Page object
     * @return a PageResponse with the same data
     */
    fun <T> toPageResponse(page: Page<T>): PageResponse<T> {
        return PageResponse(
            content = page.content,
            page = page.number,
            size = page.size,
            totalElements = page.totalElements,
            totalPages = page.totalPages,
            first = page.isFirst,
            last = page.isLast,
            empty = page.isEmpty
        )
    }
}