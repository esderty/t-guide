package t.lab.guide.repository.view

import t.lab.guide.enums.UserRole

data class LoginUserView(
    val id: Long,
    val username: String,
    val passwordHash: String,
    val role: UserRole,
    val isActive: Boolean,
)
