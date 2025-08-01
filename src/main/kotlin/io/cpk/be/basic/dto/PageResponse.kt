package io.cpk.be.basic.dto

import org.springframework.data.domain.Page

/**
 * A custom response class for paginated data that provides a stable JSON structure.
 * This class is used to replace direct serialization of Spring Data's PageImpl class.
 *
 * @param T the type of elements in the page
 * @property content the list of items in the current page
 * @property page the current page number (0-based)
 * @property size the size of the page
 * @property totalElements the total number of elements across all pages
 * @property totalPages the total number of pages
 * @property first whether this is the first page
 * @property last whether this is the last page
 * @property empty whether the page is empty
 */
data class PageResponse<T>(
    val content: List<T>,
    val page: Int,
    val size: Int,
    val totalElements: Long,
    val totalPages: Int,
    val first: Boolean,
    val last: Boolean,
    val empty: Boolean
) {
    companion object {
        /**
         * Creates a PageResponse from a Spring Data Page object.
         *
         * @param page the Spring Data Page object
         * @return a PageResponse with the same data
         */
        fun <T> from(page: Page<T>): PageResponse<T> {
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
}