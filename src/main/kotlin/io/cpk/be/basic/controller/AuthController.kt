package io.cpk.be.basic.controller

import io.cpk.be.basic.dto.*
import io.cpk.be.basic.repository.AppUserRepository
import io.cpk.be.basic.repository.AppUserRoleRepository
import io.cpk.be.security.CustomUserDetails
import io.cpk.be.security.JwtTokenProvider
import io.swagger.v3.oas.annotations.Operation
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val authenticationManager: AuthenticationManager,
    private val tokenProvider: JwtTokenProvider,
    private val appUserRepository: AppUserRepository,
    private val appUserRoleRepository: AppUserRoleRepository
) {
    val logger = org.slf4j.LoggerFactory.getLogger(AuthController::class.java)

    @Operation(operationId = "loginAuth")
    @PostMapping("/login")
    fun authenticateUser(
        @Valid @RequestBody loginRequest: LoginRequest
    ): ResponseEntity<JwtResponse> {
        try {
            // Authenticate user with username and password
            val authentication =
                authenticationManager.authenticate(
                    UsernamePasswordAuthenticationToken(
                        loginRequest.username,
                        loginRequest.password
                    )
                )

            // Get user details from authentication
            val userDetails = authentication.principal as CustomUserDetails
            val appUser =
                appUserRepository.findByUsername(loginRequest.username)
                    .orElseThrow { RuntimeException("User not found") }

            // Generate tokens with orgId and centerId
            val accessToken =
                tokenProvider.generateTokenFromUsername(
                    loginRequest.username,
                    appUser.orgId,
                    userDetails.centerId
                )
            val refreshToken =
                tokenProvider.generateRefreshToken(
                    loginRequest.username,
                    appUser.orgId,
                    userDetails.centerId
                )

            // Get token expiration
            val expirationDate = tokenProvider.getExpirationDateFromToken(accessToken)
            val expiresIn =
                expirationDate?.time?.minus(System.currentTimeMillis()) ?: 0L

            // Get access rights if centerId is set
            val accessRights = if (userDetails.centerId != null) {
                val accessRightLists =
                    appUserRoleRepository.findAccessRightsByUserIdAndCenterId(appUser.id, userDetails.centerId)
                accessRightLists.flatten().distinct()
            } else {
                emptyList()
            }

            val userInfo =
                UserInfo(
                    id = appUser.id,
                    username = appUser.username,
                    fullname = appUser.fullname,
                    email = appUser.email,
                    orgId = appUser.orgId,
                    orgAdmin = appUser.orgAdmin,
                    centerId = userDetails.centerId,
                    accessRight = accessRights
                )

            val jwtResponse =
                JwtResponse(
                    accessToken = accessToken,
                    refreshToken = refreshToken,
                    expiresIn = expiresIn,
                    user = userInfo
                )

            return ResponseEntity.ok(jwtResponse)
        } catch (ex: Exception) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        }
    }

    @Operation(operationId = "refreshTokenAuth")
    @PostMapping("/refresh")
    fun refreshToken(
        @Valid @RequestBody refreshRequest: RefreshTokenRequest
    ): ResponseEntity<JwtResponse> {
        try {
            val refreshToken = refreshRequest.refreshToken

            if (!tokenProvider.validateToken(refreshToken) ||
                !tokenProvider.isRefreshToken(refreshToken)
            ) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
            }

            val username = tokenProvider.getUsernameFromToken(refreshToken)
            val appUser =
                appUserRepository.findByUsername(username).orElseThrow {
                    RuntimeException("User not found")
                }

            // Get orgId and centerId from refresh token
            val orgId = tokenProvider.getOrgIdFromToken(refreshToken)
            var centerId = tokenProvider.getCenterIdFromToken(refreshToken)

            // If centerId from token is null, use the one from the request
            if (centerId == null) {
                centerId = refreshRequest.centerId
            }

            // Generate new tokens with orgId and centerId
            val newAccessToken =
                tokenProvider.generateTokenFromUsername(username, orgId, centerId)
            val newRefreshToken =
                tokenProvider.generateRefreshToken(username, orgId, centerId)

            val expirationDate =
                tokenProvider.getExpirationDateFromToken(newAccessToken)
            val expiresIn =
                expirationDate?.time?.minus(System.currentTimeMillis()) ?: 0L

            // Get access rights if centerId is set
            val accessRights = if (centerId != null) {
                val accessRightLists = appUserRoleRepository.findAccessRightsByUserIdAndCenterId(appUser.id, centerId)
                accessRightLists.flatten().distinct()
            } else {
                emptyList()
            }

            val userInfo =
                UserInfo(
                    id = appUser.id,
                    username = appUser.username,
                    fullname = appUser.fullname,
                    email = appUser.email,
                    orgId = appUser.orgId,
                    orgAdmin = appUser.orgAdmin,
                    centerId = centerId,
                    accessRight = accessRights
                )

            val jwtResponse =
                JwtResponse(
                    accessToken = newAccessToken,
                    refreshToken = newRefreshToken,
                    expiresIn = expiresIn,
                    user = userInfo
                )

            return ResponseEntity.ok(jwtResponse)
        } catch (ex: Exception) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        }
    }

    @Operation(operationId = "logoutAuth")
    @PostMapping("/logout")
    fun logout(): ResponseEntity<Map<String, String>> {
        // In a more sophisticated implementation, you would:
        // 1. Add the token to a blacklist
        // 2. Clear any server-side session data
        // For now, we'll just return a success message
        // The client should remove the token from storage

        SecurityContextHolder.clearContext()

        return ResponseEntity.ok(mapOf("message" to "Logout successful"))
    }

    @Operation(operationId = "getCurrentUserAuth")
    @GetMapping("/me")
    fun getCurrentUser(authentication: Authentication): ResponseEntity<UserInfo> {
        try {
            val username = authentication.name
            val appUser =
                appUserRepository.findByUsername(username).orElseThrow {
                    RuntimeException("User not found")
                }

            // Get user details from authentication
            val userDetails = authentication.principal as? CustomUserDetails

            // Get centerId from user details
            val centerId = userDetails?.centerId
            logger.info("centerId: $centerId")
            // Get access rights
            val accessRights = if (centerId != null) {
                // Get access rights for the user and center ID
                val accessRightLists = appUserRoleRepository.findAccessRightsByUserIdAndCenterId(appUser.id, centerId)
                // Flatten the list of lists into a single list of unique access rights
                accessRightLists.flatten().distinct()
            } else {
                emptyList()
            }

            val userInfo =
                UserInfo(
                    id = appUser.id,
                    username = appUser.username,
                    fullname = appUser.fullname,
                    email = appUser.email,
                    orgId = appUser.orgId,
                    orgAdmin = appUser.orgAdmin,
                    centerId = centerId,
                    accessRight = accessRights
                )

            return ResponseEntity.ok(userInfo)
        } catch (ex: Exception) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        }
    }

    @Operation(operationId = "requestPasswordResetAuth")
    @PostMapping("/reset-password-request")
    fun requestPasswordReset(
        @Valid @RequestBody request: PasswordResetRequest
    ): ResponseEntity<Map<String, String>> {
        // TODO: Implement password reset request
        // This would typically:
        // 1. Find user by email
        // 2. Generate a reset token
        // 3. Send email with reset link
        // 4. Store the reset token temporarily

        return ResponseEntity.ok(mapOf("message" to "Password reset request sent to email"))
    }

    @Operation(operationId = "resetPasswordAuth")
    @PostMapping("/reset-password")
    fun resetPassword(
        @Valid @RequestBody request: PasswordResetConfirm
    ): ResponseEntity<Map<String, String>> {
        // TODO: Implement password reset
        // This would typically:
        // 1. Validate the reset token
        // 2. Update the user's password
        // 3. Invalidate the reset token

        return ResponseEntity.ok(mapOf("message" to "Password reset successful"))
    }

    /**
     * Endpoint to set centerId for a user's token
     * This is used when a token is generated without centerId or with centerId = null
     */
    @Operation(operationId = "setCenterAuth")
    @PostMapping("/set-center")
    fun setCenter(
        @Valid @RequestBody request: SetCenterRequest,
        authentication: Authentication
    ): ResponseEntity<JwtResponse> {
        try {
            val username = authentication.name
            val userDetails = authentication.principal as CustomUserDetails

            // Verify that the centerId is valid for this user
            val centerIds = appUserRoleRepository.findCenterIdsByUserId(userDetails.id ?: username)
            if (centerIds.isEmpty() || !centerIds.contains(request.centerId)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(null)
            }

            // Generate new tokens with the specified centerId
            val accessToken = tokenProvider.generateTokenFromUsername(
                username,
                userDetails.orgId,
                request.centerId
            )
            val refreshToken = tokenProvider.generateRefreshToken(
                username,
                userDetails.orgId,
                request.centerId
            )

            // Get token expiration
            val expirationDate = tokenProvider.getExpirationDateFromToken(accessToken)
            val expiresIn = expirationDate?.time?.minus(System.currentTimeMillis()) ?: 0L

            val appUser = appUserRepository.findByUsername(username)
                .orElseThrow { RuntimeException("User not found") }

            // Get access rights for the user and center ID
            val accessRightLists =
                appUserRoleRepository.findAccessRightsByUserIdAndCenterId(appUser.id, request.centerId)
            // Flatten the list of lists into a single list of unique access rights
            val accessRights = accessRightLists.flatten().distinct()

            val userInfo = UserInfo(
                id = appUser.id,
                username = appUser.username,
                fullname = appUser.fullname,
                email = appUser.email,
                orgId = appUser.orgId,
                orgAdmin = appUser.orgAdmin,
                centerId = request.centerId,
                accessRight = accessRights
            )

            val jwtResponse = JwtResponse(
                accessToken = accessToken,
                refreshToken = refreshToken,
                expiresIn = expiresIn,
                user = userInfo
            )

            return ResponseEntity.ok(jwtResponse)
        } catch (ex: Exception) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        }
    }
}
