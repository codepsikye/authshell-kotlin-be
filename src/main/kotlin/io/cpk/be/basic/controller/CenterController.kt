package io.cpk.be.basic.controller

import io.cpk.be.basic.dto.CenterDto
import io.cpk.be.basic.dto.PageResponse
import io.cpk.be.basic.service.CenterService
import io.cpk.be.security.CustomUserDetails
import io.cpk.be.util.PageUtils
import org.springframework.data.domain.PageRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/centers")
class CenterController(private val centerService: CenterService) {

    @PreAuthorize("hasAuthority('center_create')")
    @PostMapping
    fun create(@RequestBody centerDto: CenterDto): ResponseEntity<CenterDto> {
        val createdCenter = centerService.create(centerDto)
        return ResponseEntity(createdCenter, HttpStatus.CREATED)
    }

    @PreAuthorize("hasAuthority('center_read')")
    @GetMapping
    fun findAll(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
        @RequestParam(required = false) name: String?,
        @AuthenticationPrincipal userDetails: CustomUserDetails
    ): ResponseEntity<PageResponse<CenterDto>> {
        val pageable = PageRequest.of(page, size)
        val pageResult = centerService.findAll(userDetails.orgId, name, pageable)
        return ResponseEntity(PageUtils.toPageResponse(pageResult), HttpStatus.OK)
    }

    @PreAuthorize("hasAuthority('center_read')")
    @GetMapping("/{id}")
    fun findById(
        @PathVariable id: Int,
        @AuthenticationPrincipal userDetails: CustomUserDetails
    ): ResponseEntity<CenterDto> {
        val center = centerService.findById(id)
        return if (center != null && center.orgId == userDetails.orgId) {
            ResponseEntity(center, HttpStatus.OK)
        } else {
            ResponseEntity(HttpStatus.NOT_FOUND)
        }
    }

    @PreAuthorize("hasAuthority('center_edit') and #centerDto.orgId == authentication.principal.orgId")
    @PutMapping("/{id}")
    fun update(@PathVariable id: Int, @RequestBody centerDto: CenterDto): ResponseEntity<CenterDto> {
        return ResponseEntity(centerService.update(id, centerDto), HttpStatus.OK)
    }

    @PreAuthorize("hasAuthority('center_remove')")
    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Int, @AuthenticationPrincipal userDetails: CustomUserDetails): ResponseEntity<Void> {
        val center = centerService.findById(id)
        if (center != null && center.orgId == userDetails.orgId) {
            centerService.delete(id)
            return ResponseEntity(HttpStatus.NO_CONTENT)
        }
        return ResponseEntity(HttpStatus.NOT_FOUND)
    }

    @GetMapping("/my-centers")
    fun getMyCenters(authentication: Authentication): ResponseEntity<List<CenterDto>> {
        val userDetails = authentication.principal as CustomUserDetails
        val userId = userDetails.id ?: authentication.name
        val centers = centerService.findByUserId(userId)
        return ResponseEntity(centers, HttpStatus.OK)
    }
}