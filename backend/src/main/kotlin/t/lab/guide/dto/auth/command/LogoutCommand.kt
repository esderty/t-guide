package t.lab.guide.dto.auth.command

data class LogoutCommand(
    val refreshToken: String,
)
