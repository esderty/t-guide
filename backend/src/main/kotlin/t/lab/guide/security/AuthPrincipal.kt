package t.lab.guide.security

import t.lab.guide.enums.UserRole

data class AuthPrincipal(
    val id: Long,
    val role: UserRole,
)
