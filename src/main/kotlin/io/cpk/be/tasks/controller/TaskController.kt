package io.cpk.be.tasks.controller

import io.cpk.be.basic.dto.PageResponse
import io.cpk.be.security.CustomUserDetails
import io.cpk.be.tasks.dto.TaskDto
import io.cpk.be.tasks.service.TaskService
import io.cpk.be.util.PageUtils
import org.springframework.data.domain.PageRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/tasks")
class TaskController(private val taskService: TaskService) {

    @PreAuthorize("hasAuthority('task_create')")
    @PostMapping
    fun create(
        @RequestBody taskDto: TaskDto,
        @AuthenticationPrincipal userDetails: CustomUserDetails
    ): ResponseEntity<TaskDto> {
        val centerId = userDetails.centerId
        return if (centerId != null && taskDto.centerId == centerId) {
            val createdTask = taskService.create(taskDto)
            ResponseEntity(createdTask, HttpStatus.CREATED)
        } else {
            ResponseEntity(HttpStatus.FORBIDDEN)
        }
    }

    @PreAuthorize("hasAuthority('task_read')")
    @GetMapping
    fun findAll(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
        @RequestParam(required = false) status: String?,
        @RequestParam(required = false) subject: String?,
        @AuthenticationPrincipal userDetails: CustomUserDetails
    ): ResponseEntity<PageResponse<TaskDto>> {
        val pageable = PageRequest.of(page, size)
        val centerId = userDetails.centerId
        return if (centerId != null) {
            val pageResult = taskService.findAll(centerId, status, subject, pageable)
            ResponseEntity(PageUtils.toPageResponse(pageResult), HttpStatus.OK)
        } else {
            ResponseEntity(HttpStatus.FORBIDDEN)
        }
    }

    @PreAuthorize("hasAuthority('task_read')")
    @GetMapping("/{id}")
    fun findById(
        @PathVariable id: Long,
        @AuthenticationPrincipal userDetails: CustomUserDetails
    ): ResponseEntity<TaskDto> {
        val task = taskService.findById(id)
        val centerId = userDetails.centerId
        return if (task != null && centerId != null && task.centerId == centerId) {
            ResponseEntity(task, HttpStatus.OK)
        } else if (centerId == null) {
            ResponseEntity(HttpStatus.FORBIDDEN)
        } else {
            ResponseEntity(HttpStatus.NOT_FOUND)
        }
    }

    @PreAuthorize("hasAuthority('task_edit')")
    @PutMapping("/{id}")
    fun update(
        @PathVariable id: Long,
        @RequestBody taskDto: TaskDto,
        @AuthenticationPrincipal userDetails: CustomUserDetails
    ): ResponseEntity<TaskDto> {
        val task = taskService.findById(id)
        val centerId = userDetails.centerId
        return if (task != null && centerId != null && task.centerId == centerId && taskDto.centerId == centerId) {
            ResponseEntity(taskService.update(id, taskDto), HttpStatus.OK)
        } else if (centerId == null) {
            ResponseEntity(HttpStatus.FORBIDDEN)
        } else {
            ResponseEntity(HttpStatus.NOT_FOUND)
        }
    }

    @PreAuthorize("hasAuthority('task_remove')")
    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long, @AuthenticationPrincipal userDetails: CustomUserDetails): ResponseEntity<Void> {
        val task = taskService.findById(id)
        val centerId = userDetails.centerId
        if (task != null && centerId != null && task.centerId == centerId) {
            taskService.delete(id)
            return ResponseEntity(HttpStatus.NO_CONTENT)
        } else if (centerId == null) {
            return ResponseEntity(HttpStatus.FORBIDDEN)
        }
        return ResponseEntity(HttpStatus.NOT_FOUND)
    }
} 