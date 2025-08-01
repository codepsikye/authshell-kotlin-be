package io.cpk.be.tasks.controller

import io.cpk.be.basic.dto.PageResponse
import io.cpk.be.security.CustomUserDetails
import io.cpk.be.tasks.dto.TaskUpdateDto
import io.cpk.be.tasks.service.TaskService
import io.cpk.be.tasks.service.TaskUpdateService
import io.cpk.be.util.PageUtils
import org.springframework.data.domain.PageRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/task-updates")
class TaskUpdateController(
    private val taskUpdateService: TaskUpdateService,
    private val taskService: TaskService
) {

    @PreAuthorize("hasAuthority('task_update_create')")
    @PostMapping
    fun create(
        @RequestBody taskUpdateDto: TaskUpdateDto,
        @AuthenticationPrincipal userDetails: CustomUserDetails
    ): ResponseEntity<TaskUpdateDto> {
        val centerId = userDetails.centerId
        val taskId = taskUpdateDto.taskId

        return if (centerId != null && taskId != null && taskService.existsByIdAndCenterId(taskId, centerId)) {
            val createdTaskUpdate = taskUpdateService.create(taskUpdateDto)
            ResponseEntity(createdTaskUpdate, HttpStatus.CREATED)
        } else if (centerId == null) {
            ResponseEntity(HttpStatus.FORBIDDEN)
        } else {
            ResponseEntity(HttpStatus.NOT_FOUND)
        }
    }

    @PreAuthorize("hasAuthority('task_update_read')")
    @GetMapping
    fun findAll(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
        @AuthenticationPrincipal userDetails: CustomUserDetails
    ): ResponseEntity<PageResponse<TaskUpdateDto>> {
        val pageable = PageRequest.of(page, size)
        val centerId = userDetails.centerId
        return if (centerId != null) {
            val pageResult = taskUpdateService.findAll(centerId, pageable)
            ResponseEntity(PageUtils.toPageResponse(pageResult), HttpStatus.OK)
        } else {
            ResponseEntity(HttpStatus.FORBIDDEN)
        }
    }

    @PreAuthorize("hasAuthority('task_update_read')")
    @GetMapping("/{id}")
    fun findById(
        @PathVariable id: Long,
        @AuthenticationPrincipal userDetails: CustomUserDetails
    ): ResponseEntity<TaskUpdateDto> {
        val taskUpdate = taskUpdateService.findById(id)
        val centerId = userDetails.centerId

        return if (taskUpdate != null && centerId != null && taskUpdateService.existsByIdAndTaskCenterId(
                id,
                centerId
            )
        ) {
            ResponseEntity(taskUpdate, HttpStatus.OK)
        } else if (centerId == null) {
            ResponseEntity(HttpStatus.FORBIDDEN)
        } else {
            ResponseEntity(HttpStatus.NOT_FOUND)
        }
    }

    @PreAuthorize("hasAuthority('task_update_edit')")
    @PutMapping("/{id}")
    fun update(
        @PathVariable id: Long,
        @RequestBody taskUpdateDto: TaskUpdateDto,
        @AuthenticationPrincipal userDetails: CustomUserDetails
    ): ResponseEntity<TaskUpdateDto> {
        val centerId = userDetails.centerId
        val taskId = taskUpdateDto.taskId

        // Check if the task update exists and is associated with a task in the user's center
        val existingTaskUpdate = taskUpdateService.findById(id)

        return if (centerId != null && taskId != null &&
            existingTaskUpdate != null &&
            taskUpdateService.existsByIdAndTaskCenterId(id, centerId) &&
            taskService.existsByIdAndCenterId(taskId, centerId)
        ) {
            ResponseEntity(taskUpdateService.update(id, taskUpdateDto), HttpStatus.OK)
        } else if (centerId == null) {
            ResponseEntity(HttpStatus.FORBIDDEN)
        } else {
            ResponseEntity(HttpStatus.NOT_FOUND)
        }
    }

    @PreAuthorize("hasAuthority('task_update_remove')")
    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long, @AuthenticationPrincipal userDetails: CustomUserDetails): ResponseEntity<Void> {
        val centerId = userDetails.centerId

        return if (centerId != null && taskUpdateService.existsByIdAndTaskCenterId(id, centerId)) {
            taskUpdateService.delete(id)
            ResponseEntity(HttpStatus.NO_CONTENT)
        } else if (centerId == null) {
            ResponseEntity(HttpStatus.FORBIDDEN)
        } else {
            ResponseEntity(HttpStatus.NOT_FOUND)
        }
    }
} 