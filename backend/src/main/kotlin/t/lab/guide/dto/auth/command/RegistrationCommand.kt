package t.lab.guide.dto.auth.command

import t.lab.guide.enums.UserLanguage

data class RegistrationCommand(
    val username: String,
    val email: String,
    val name: String,
    val password: String,
    val lang: UserLanguage,
)
