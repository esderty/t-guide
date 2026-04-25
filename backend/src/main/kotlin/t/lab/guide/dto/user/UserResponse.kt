package t.lab.guide.dto.user

import io.swagger.v3.oas.annotations.media.Schema
import t.lab.guide.entity.enums.UserLanguage
import t.lab.guide.entity.enums.UserRole

@Schema(description = "Ответ с данными пользователя")
data class UserResponse(
    @Schema(description = "Уникальный идентификатор пользователя", example = "1")
    val id: Long,
    @Schema(description = "Уникальное имя пользователя для входа в систему", example = "enzolu")
    val username: String,
    @Schema(description = "Электронная почта пользователя", example = "email@domain.zone")
    val email: String,
    @Schema(description = "Отображаемое имя пользователя", example = "Игорь")
    val name: String,
    @Schema(description = "Язык интерфейса пользователя", example = "RU")
    val language: UserLanguage,
    @Schema(description = "Роль пользователя в системе", example = "USER")
    val role: UserRole,
)
