package io.cpk.be.security

import io.cpk.be.basic.repository.AppUserRepository
import io.cpk.be.basic.repository.AppUserRoleRepository
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CustomUserDetailsService(
    private val appUserRepository: AppUserRepository,
    private val appUserRoleRepository: AppUserRoleRepository
) : UserDetailsService {

    @Transactional(readOnly = true)
    open fun loadUserByUsername(username: String, centerId: Int?): UserDetails {
        val appUser = appUserRepository.findByUsername(username).orElseThrow {
            UsernameNotFoundException("User not found with username: $username")
        }

        if (centerId != null) {
            // Verify that the centerId is valid for this user
            val centerIds = appUserRoleRepository.findCenterIdsByUserId(appUser.id)
            if (!centerIds.contains(centerId)) {
                throw UsernameNotFoundException("User not found with username: $username and centerId: $centerId")
            }

            // Get access rights for the user and center ID
            val accessRightLists = appUserRoleRepository.findAccessRightsByUserIdAndCenterId(appUser.id, centerId)

            // Flatten the list of lists into a single list of unique access rights
            val accessRights = accessRightLists.flatten().distinct()

            // Convert access rights to authorities
            val authorities = accessRights.map { SimpleGrantedAuthority(it) }

            return CustomUserDetails(
                username = appUser.username,
                password = appUser.password.orEmpty(),
                authorities = authorities,
                orgId = appUser.orgId,
                centerId = centerId,
                id = appUser.id
            )
        } else {
            // If centerId is not set, return user with empty authorities
            return CustomUserDetails(
                username = appUser.username,
                password = appUser.password.orEmpty(),
                authorities = emptyList(),
                orgId = appUser.orgId,
                centerId = null,
                id = appUser.id
            )
        }
    }

    @Transactional(readOnly = true)
    override fun loadUserByUsername(username: String): UserDetails {
        val appUser =
            appUserRepository.findByUsername(username).orElseThrow {
                UsernameNotFoundException("User not found with username: $username")
            }

        // For backward compatibility, create basic authorities
        val authorities = mutableListOf<SimpleGrantedAuthority>()

        // Check if user has a unique centerId
        val centerId = if (appUserRoleRepository.hasUniqueCenterId(appUser.id)) {
            appUserRoleRepository.getUniqueCenterId(appUser.id)
        } else {
            null
        }

        // If centerId is set, load user with access rights, otherwise with basic authorities
        return if (centerId != null) {
            loadUserByUsername(username, centerId)
        } else {
            CustomUserDetails(
                username = appUser.username,
                password = appUser.password ?: "",
                authorities = authorities,
                orgId = appUser.orgId,
                centerId = null,
                id = appUser.id
            )
        }
    }

}
