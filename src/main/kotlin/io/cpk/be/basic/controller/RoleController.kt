package io.cpk.be.basic.controller

import io.cpk.be.basic.dto.PageResponse
import io.cpk.be.basic.dto.RoleDto
import io.cpk.be.basic.service.RoleService
import io.cpk.be.security.CustomUserDetails
import io.cpk.be.util.PageUtils
import io.swagger.v3.oas.annotations.Operation
import org.springframework.data.domain.PageRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/roles")
class RoleController(private val roleService: RoleService) {

    @Operation(operationId = "createRole")
    @PreAuthorize("hasAuthority('role_create') and #roleDto.orgId == authentication.principal.orgId")
    @PostMapping
    fun create(@RequestBody roleDto: RoleDto): ResponseEntity<RoleDto> {
        val createdRole = roleService.create(roleDto)
        return ResponseEntity(createdRole, HttpStatus.CREATED)
    }

    @Operation(operationId = "findAllRoles")
    @PreAuthorize("hasAuthority('role_read')")
    @GetMapping
    fun findAll(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
        @RequestParam(required = false) name: String?,
        @AuthenticationPrincipal userDetails: CustomUserDetails
    ): ResponseEntity<PageResponse<RoleDto>> {
        val pageable = PageRequest.of(page, size)
        val pageResult = roleService.findAll(userDetails.orgId, name, pageable)
        return ResponseEntity(PageUtils.toPageResponse(pageResult), HttpStatus.OK)
    }

    @Operation(operationId = "findRoleById")
    @PreAuthorize("hasAuthority('role_read') and #orgId == authentication.principal.orgId")
    @GetMapping("/{orgId}/{name}")
    fun findById(
        @PathVariable orgId: Int,
        @PathVariable name: String,
        @AuthenticationPrincipal userDetails: CustomUserDetails
    ): ResponseEntity<RoleDto> {
        val role = roleService.findById(orgId, name)
        return if (role != null && role.orgId == userDetails.orgId) {
            ResponseEntity(role, HttpStatus.OK)
        } else {
            ResponseEntity(HttpStatus.NOT_FOUND)
        }
    }

    @Operation(operationId = "updateRole")
    @PreAuthorize("hasAuthority('role_edit') and #orgId == authentication.principal.orgId and #roleDto.orgId == authentication.principal.orgId")
    @PutMapping("/{orgId}/{name}")
    fun update(
        @PathVariable orgId: Int,
        @PathVariable name: String,
        @RequestBody roleDto: RoleDto
    ): ResponseEntity<RoleDto> {
        return ResponseEntity(roleService.update(orgId, name, roleDto), HttpStatus.OK)
    }

    @Operation(operationId = "deleteRole")
    @PreAuthorize("hasAuthority('role_remove') and #orgId == authentication.principal.orgId")
    @DeleteMapping("/{orgId}/{name}")
    fun delete(
        @PathVariable orgId: Int,
        @PathVariable name: String,
        @AuthenticationPrincipal userDetails: CustomUserDetails
    ): ResponseEntity<Void> {
        val role = roleService.findById(orgId, name)
        if (role != null && role.orgId == userDetails.orgId) {
            roleService.delete(orgId, name)
            return ResponseEntity(HttpStatus.NO_CONTENT)
        }
        return ResponseEntity(HttpStatus.NOT_FOUND)
    }
} 