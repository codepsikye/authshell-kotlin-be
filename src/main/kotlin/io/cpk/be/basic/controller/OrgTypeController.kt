package io.cpk.be.basic.controller

import io.cpk.be.basic.dto.OrgTypeDto
import io.cpk.be.basic.dto.PageResponse
import io.cpk.be.basic.service.OrgTypeService
import io.cpk.be.util.PageUtils
import io.swagger.v3.oas.annotations.Operation
import jakarta.annotation.security.RolesAllowed
import org.springframework.data.domain.PageRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/org-types")
class OrgTypeController(private val orgTypeService: OrgTypeService) {

    @Operation(operationId = "createOrgType")
    @PreAuthorize("hasAuthority('org_type_create')")
    @PostMapping
    fun create(@RequestBody orgTypeDto: OrgTypeDto): ResponseEntity<OrgTypeDto> {
        val createdOrgType = orgTypeService.create(orgTypeDto)
        return ResponseEntity(createdOrgType, HttpStatus.CREATED)
    }

    @Operation(operationId = "findAllOrgTypes")
    @PreAuthorize("hasAuthority('org_type_read')")
    @GetMapping
    fun findAll(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
        @RequestParam(required = false) name: String?
    ): ResponseEntity<PageResponse<OrgTypeDto>> {
        val pageable = PageRequest.of(page, size)
        val pageResult = orgTypeService.findAll(name, pageable)
        return ResponseEntity(PageUtils.toPageResponse(pageResult), HttpStatus.OK)
    }

    @Operation(operationId = "findOrgTypeById")
    @PreAuthorize("hasAuthority('org_type_read')")
    @GetMapping("/{id}")
    fun findById(@PathVariable id: String): ResponseEntity<OrgTypeDto> {
        val orgType = orgTypeService.findById(id)
        return if (orgType != null) {
            ResponseEntity(orgType, HttpStatus.OK)
        } else {
            ResponseEntity(HttpStatus.NOT_FOUND)
        }
    }

    @Operation(operationId = "updateOrgType")
    @RolesAllowed("org_type_edit")
    @PutMapping("/{id}")
    fun update(@PathVariable id: String, @RequestBody orgTypeDto: OrgTypeDto): ResponseEntity<OrgTypeDto> {
        return ResponseEntity(orgTypeService.update(id, orgTypeDto), HttpStatus.OK)
    }

    @Operation(operationId = "deleteOrgType")
    @RolesAllowed("org_type_remove")
    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: String): ResponseEntity<Void> {
        orgTypeService.delete(id)
        return ResponseEntity(HttpStatus.NO_CONTENT)
    }
} 