package io.cpk.be.basic.controller

import io.cpk.be.basic.dto.AppUserDto
import io.cpk.be.basic.dto.PageResponse
import io.cpk.be.basic.service.AppUserService
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
@RequestMapping("/api/app-users")
class AppUserController(private val appUserService: AppUserService) {

    @Operation(operationId = "createAppUser")
    @PreAuthorize("hasAuthority('user_create')")
    @PostMapping
    fun create(@RequestBody appUserDto: AppUserDto): ResponseEntity<AppUserDto> {
        val createdAppUser = appUserService.create(appUserDto)
        return ResponseEntity(createdAppUser, HttpStatus.CREATED)
    }

    @Operation(operationId = "findAllAppUsers")
    @PreAuthorize("hasAuthority('user_read')")
    @GetMapping
    fun findAll(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
        @RequestParam(required = false) fullname: String?,
        @AuthenticationPrincipal userDetails: CustomUserDetails,
    ): ResponseEntity<PageResponse<AppUserDto>> {
        val pageable = PageRequest.of(page, size)
        val pageResult = appUserService.findAll(userDetails.orgId, fullname, pageable)
        return ResponseEntity(PageUtils.toPageResponse(pageResult), HttpStatus.OK)
    }

    @Operation(operationId = "findAppUserById")
    @PreAuthorize("hasAuthority('user_read') or #id == authentication.principal.id")
    @GetMapping("/{id}")
    fun findById(
        @PathVariable id: Int,
        @AuthenticationPrincipal userDetails: CustomUserDetails
    ): ResponseEntity<AppUserDto> {
        val appUser = appUserService.findById(id)
        if (appUser != null && appUser.orgId == userDetails.orgId) {
            return ResponseEntity(appUser, HttpStatus.OK)
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build<AppUserDto>()
    }

    @Operation(operationId = "updateAppUser")
    @PreAuthorize("hasAuthority('user_edit') and #appUserDto.orgId == authentication.principal.orgId")
    @PutMapping("/{id}")
    fun update(@PathVariable id: Int, @RequestBody appUserDto: AppUserDto): ResponseEntity<AppUserDto> {
        return ResponseEntity(appUserService.update(id, appUserDto), HttpStatus.OK)
    }

    @Operation(operationId = "deleteAppUser")
    @PreAuthorize("hasAuthority('user_remove') and #id == authentication.principal.id")
    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Int): ResponseEntity<Void> {
        appUserService.delete(id)
        return ResponseEntity(HttpStatus.NO_CONTENT)
    }
} 