package t.lab.guide.security

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import t.lab.guide.enums.UserRole

data class AppUserDetails(
    val id: Long,
    private val username: String,
    val role: UserRole,
    val passwordHash: String,
    val isActive: Boolean,
    private val authorities: Collection<GrantedAuthority>,
) : UserDetails {
    override fun getAuthorities() = authorities

    override fun getPassword() = passwordHash

    override fun getUsername() = username

    override fun isAccountNonExpired() = true

    override fun isAccountNonLocked() = true

    override fun isCredentialsNonExpired() = true

    override fun isEnabled() = isActive
}
