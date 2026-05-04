package t.lab.guide.repository.view.user

import t.lab.guide.enums.UserLanguage
import t.lab.guide.enums.UserRole

data class ProfileUserView(
    val id: Long,
    val username: String,
    val email: String,
    val name: String,
    val lang: UserLanguage,
    val role: UserRole,
)
