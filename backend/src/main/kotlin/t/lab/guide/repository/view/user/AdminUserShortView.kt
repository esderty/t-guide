package t.lab.guide.repository.view.user

import t.lab.guide.enums.UserRole
import java.time.OffsetDateTime

data class AdminUserShortView(
    val id: Long,
    val username: String,
    val email: String,
    val role: UserRole,
    val isActive: Boolean,
    val createdAt: OffsetDateTime,
)
