package t.lab.guide.dto.auth

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank

@Schema(description = "Данные для выхода из системы")
data class LogoutRequest(
    @Schema(
        description = "Токен обновления",
        requiredMode = Schema.RequiredMode.REQUIRED,
        example = "dGhpcy1pcz1hLXJlZnJlc2gtdG9rZW4uLi4=...",
    )
    @field:NotBlank(message = "поле обязательно!")
    val refreshToken: String,
)
