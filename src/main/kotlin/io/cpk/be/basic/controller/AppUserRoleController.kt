package io.cpk.be.basic.controller

import io.cpk.be.basic.dto.AppUserRoleDto
import io.cpk.be.basic.dto.PageResponse
import io.cpk.be.basic.service.AppUserRoleService
import io.cpk.be.util.PageUtils
import io.swagger.v3.oas.annotations.Operation
import org.springframework.data.domain.PageRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/app-user-roles")
class AppUserRoleController(private val appUserRoleService: AppUserRoleService) {

    @Operation(operationId = "createAppUserRole")
    @PreAuthorize("hasAuthority('user_role_create')")
    @PostMapping
    fun create(@RequestBody appUserRoleDto: AppUserRoleDto): ResponseEntity<AppUserRoleDto> {
        val createdAppUserRole = appUserRoleService.create(appUserRoleDto)
        return ResponseEntity(createdAppUserRole, HttpStatus.CREATED)
    }

    @Operation(operationId = "findAllAppUserRoles")
    @PreAuthorize("hasAuthority('user_role_read')")
    @GetMapping
    fun findAll(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int
    ): ResponseEntity<PageResponse<AppUserRoleDto>> {
        val pageable = PageRequest.of(page, size)
        val pageResult = appUserRoleService.findAll(pageable)
        return ResponseEntity(PageUtils.toPageResponse(pageResult), HttpStatus.OK)
    }

    @Operation(operationId = "findAppUserRoleById")
    @PreAuthorize("hasAuthority('user_role_read')")
    @GetMapping("/{userId}/{orgId}/{centerId}/{roleName}")
    fun findById(
        @PathVariable userId: Int,
        @PathVariable orgId: Int,
        @PathVariable centerId: Int,
        @PathVariable roleName: String
    ): ResponseEntity<AppUserRoleDto> {
        val appUserRole = appUserRoleService.findById(userId, orgId, centerId, roleName)
        return if (appUserRole != null) {
            ResponseEntity(appUserRole, HttpStatus.OK)
        } else {
            ResponseEntity(HttpStatus.NOT_FOUND)
        }
    }

    @Operation(operationId = "updateAppUserRole")
    @PreAuthorize("hasAuthority('user_role_edit')")
    @PutMapping("/{userId}/{orgId}/{centerId}/{roleName}")
    fun update(
        @PathVariable userId: Int,
        @PathVariable orgId: Int,
        @PathVariable centerId: Int,
        @PathVariable roleName: String,
        @RequestBody appUserRoleDto: AppUserRoleDto
    ): ResponseEntity<AppUserRoleDto> {
        return ResponseEntity(
            appUserRoleService.update(userId, orgId, centerId, roleName, appUserRoleDto),
            HttpStatus.OK
        )
    }

    @Operation(operationId = "deleteAppUserRole")
    @PreAuthorize("hasAuthority('user_role_remove')")
    @DeleteMapping("/{userId}/{orgId}/{centerId}/{roleName}")
    fun delete(
        @PathVariable userId: Int,
        @PathVariable orgId: Int,
        @PathVariable centerId: Int,
        @PathVariable roleName: String
    ): ResponseEntity<Void> {
        appUserRoleService.delete(userId, orgId, centerId, roleName)
        return ResponseEntity(HttpStatus.NO_CONTENT)
    }
} 