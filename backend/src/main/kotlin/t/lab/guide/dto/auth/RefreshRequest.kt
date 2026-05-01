package t.lab.guide.dto.auth

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank

@Schema(description = "Данные для обновления токена доступа")
data class RefreshRequest(
    @Schema(
        description = "Токен обновления",
        requiredMode = Schema.RequiredMode.REQUIRED,
        example = "dGhpcy1pcz1hLXJlZnJlc2gtdG9rZW4uLi4=...",
        nullable = false,
    )
    @field:NotBlank(message = "поле обязательно!")
    val refreshToken: String? = null,
)
