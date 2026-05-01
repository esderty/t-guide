package t.lab.guide.dto.auth.command

data class ChangePasswordCommand(
    val oldPassword: String,
    val newPassword: String,
)
