package io.cpk.be.basic.controller

import io.cpk.be.basic.dto.*
import io.cpk.be.basic.repository.AccessRightRepository
import io.cpk.be.basic.service.*
import io.cpk.be.util.PageUtils
import io.swagger.v3.oas.annotations.Operation
import org.springframework.data.domain.PageRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/orgs")
class OrgController(
    private val orgService: OrgService,
    private val centerService: CenterService,
    private val appUserService: AppUserService,
    private val roleService: RoleService,
    private val appUserRoleService: AppUserRoleService,
    private val accessRightRepository: AccessRightRepository
) {

    @Operation(operationId = "createOrgWithCenterAndUser")
    @PreAuthorize("hasAuthority('org_create')")
    @PostMapping
    fun createOrgWithCenterAndUser(@RequestBody request: CreateOrgRequest): ResponseEntity<OrgDto> {
        // Create the organization
        val createdOrg = orgService.create(request.orgDto)

        // Create the center in that organization
        val centerDto = request.centerDto.copy(orgId = createdOrg.id!!)
        val createdCenter = centerService.create(centerDto)

        // Create the user in the same organization
        val userDto = request.userDto.copy(orgId = createdOrg.id)
        val createdUser = appUserService.create(userDto)

        // Get all access rights
        val allAccessRights = accessRightRepository.findAll().map { it.name }

        // Create admin role with all access rights if it doesn't exist
        val adminRoleName = "admin"
        val adminRole = roleService.findById(createdOrg.id, adminRoleName) ?: roleService.create(
            RoleDto(
                orgId = createdOrg.id,
                name = adminRoleName,
                accessRight = allAccessRights
            )
        )

        // Create app_user_role association
        createdCenter.id?.let { centerId ->
            appUserRoleService.create(
                AppUserRoleDto(
                    userId = createdUser.id,
                    orgId = createdOrg.id,
                    centerId = centerId,
                    roleName = adminRoleName
                )
            )
        }

        return ResponseEntity(createdOrg, HttpStatus.CREATED)
    }

    @Operation(operationId = "findAllOrgs")
    @PreAuthorize("hasAuthority('org_read')")
    @GetMapping
    fun findAll(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
        @RequestParam(required = false) name: String?
    ): ResponseEntity<PageResponse<OrgDto>> {
        val pageable = PageRequest.of(page, size)
        val pageResult = orgService.findAll(name, pageable)
        return ResponseEntity(PageUtils.toPageResponse(pageResult), HttpStatus.OK)
    }

    @Operation(operationId = "findOrgById")
    @PreAuthorize("hasAuthority('org_read') or #id == authentication.principal.orgId")
    @GetMapping("/{id}")
    fun findById(@PathVariable id: Int): ResponseEntity<OrgDto> {
        val org = orgService.findById(id)
        return if (org != null) {
            ResponseEntity(org, HttpStatus.OK)
        } else {
            ResponseEntity(HttpStatus.NOT_FOUND)
        }
    }

    @Operation(operationId = "updateOrg")
    @PreAuthorize("hasAuthority('org_edit') or ( hasAnyAuthority('org_edit_this') and #id == authentication.principal.orgId)")
    @PutMapping("/{id}")
    fun update(@PathVariable id: Int, @RequestBody orgDto: OrgDto): ResponseEntity<OrgDto> {
        return ResponseEntity(orgService.update(id, orgDto), HttpStatus.OK)
    }

    @Operation(operationId = "deleteOrg")
    @PreAuthorize("hasAuthority('org_remove')")
    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Int): ResponseEntity<Void> {
        orgService.delete(id)
        return ResponseEntity(HttpStatus.NO_CONTENT)
    }
}
