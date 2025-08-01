package io.cpk.be.security

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

/** Custom implementation of UserDetails that includes the organization ID. */
class CustomUserDetails(
    private val username: String, // This should be the username, not id
    private val password: String,
    private val authorities: Collection<GrantedAuthority>,
    val orgId: Int,
    val centerId: Int? = null,
    val id: String? = null // Optionally keep id for reference
) : UserDetails {

    override fun getAuthorities(): Collection<GrantedAuthority> = authorities

    override fun getPassword(): String = password

    override fun getUsername(): String = username

    override fun isAccountNonExpired(): Boolean = true

    override fun isAccountNonLocked(): Boolean = true

    override fun isCredentialsNonExpired(): Boolean = true

    override fun isEnabled(): Boolean = true
}
