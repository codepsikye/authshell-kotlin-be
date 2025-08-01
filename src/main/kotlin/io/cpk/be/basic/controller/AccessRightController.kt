package io.cpk.be.basic.controller

import io.cpk.be.basic.dto.AccessRightDto
import io.cpk.be.basic.dto.PageResponse
import io.cpk.be.basic.service.AccessRightService
import io.cpk.be.util.PageUtils
import io.swagger.v3.oas.annotations.Operation
import org.springframework.data.domain.PageRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/access-rights")
class AccessRightController(private val accessRightService: AccessRightService) {

    @Operation(operationId = "createAccessRight")
    @PreAuthorize("hasAuthority('access_right_create')")
    @PostMapping
    fun create(@RequestBody accessRightDto: AccessRightDto): ResponseEntity<AccessRightDto> {
        val createdAccessRight = accessRightService.create(accessRightDto)
        return ResponseEntity(createdAccessRight, HttpStatus.CREATED)
    }

    @Operation(operationId = "findAllAccessRights")
    @PreAuthorize("hasAuthority('access_right_read')")
    @GetMapping
    fun findAll(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
        @RequestParam(required = false) name: String?
    ): ResponseEntity<PageResponse<AccessRightDto>> {
        val pageable = PageRequest.of(page, size)
        val pageResult = accessRightService.findAll(name, pageable)
        return ResponseEntity(PageUtils.toPageResponse(pageResult), HttpStatus.OK)
    }

    @Operation(operationId = "findAccessRightById")
    @PreAuthorize("hasAuthority('access_right_read')")
    @GetMapping("/{id}")
    fun findById(@PathVariable id: String): ResponseEntity<AccessRightDto> {
        val accessRight = accessRightService.findById(id)
        return if (accessRight != null) {
            ResponseEntity(accessRight, HttpStatus.OK)
        } else {
            ResponseEntity(HttpStatus.NOT_FOUND)
        }
    }

    @Operation(operationId = "updateAccessRight")
    @PreAuthorize("hasAuthority('access_right_edit')")
    @PutMapping("/{id}")
    fun update(@PathVariable id: String, @RequestBody accessRightDto: AccessRightDto): ResponseEntity<AccessRightDto> {
        return ResponseEntity(accessRightService.update(id, accessRightDto), HttpStatus.OK)
    }

    @Operation(operationId = "deleteAccessRight")
    @PreAuthorize("hasAuthority('access_right_remove')")
    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: String): ResponseEntity<Void> {
        accessRightService.delete(id)
        return ResponseEntity(HttpStatus.NO_CONTENT)
    }
} 