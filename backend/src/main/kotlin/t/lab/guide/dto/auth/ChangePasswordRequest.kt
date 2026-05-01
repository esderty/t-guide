package t.lab.guide.dto.auth

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

@Schema(description = "Данные для изменения пароля пользователя")
data class ChangePasswordRequest(
    @Schema(
        description = "Текущий пароль пользователя",
        requiredMode = Schema.RequiredMode.REQUIRED,
        minLength = 8,
        maxLength = 64,
        example = "currentPassword123",
        nullable = false,
    )
    @field:NotBlank(message = "поле обязательно!")
    @field:Size(min = 8, max = 64, message = "длина должна быть от 8 до 64 символов")
    val oldPassword: String? = null,
    @Schema(
        description = "Новый пароль пользователя",
        requiredMode = Schema.RequiredMode.REQUIRED,
        minLength = 8,
        maxLength = 64,
        example = "newSecurePassword123",
        nullable = false,
    )
    @field:NotBlank(message = "поле обязательно!")
    @field:Size(min = 8, max = 64, message = "длина должна быть от 8 до 64 символов")
    val newPassword: String? = null,
)
