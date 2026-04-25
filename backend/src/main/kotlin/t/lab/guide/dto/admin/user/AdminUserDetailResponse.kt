package t.lab.guide.dto.admin.user

import io.swagger.v3.oas.annotations.media.Schema
import t.lab.guide.enums.UserLanguage
import t.lab.guide.enums.UserRole
import java.time.OffsetDateTime

@Schema(description = "Ответ с данными пользователя для администратора")
data class AdminUserDetailResponse(
    @Schema(description = "Уникальный идентификатор пользователя", example = "1")
    val id: Long,
    @Schema(description = "Уникальное имя пользователя для входа в систему", example = "enzolu")
    val username: String,
    @Schema(description = "Электронная почта пользователя", example = "email@domain.zone")
    val email: String,
    @Schema(description = "Отображаемое имя пользователя", example = "Игорь")
    val name: String,
    @Schema(description = "Язык интерфейса пользователя", example = "RU")
    val lang: UserLanguage,
    @Schema(description = "Роль пользователя в системе", example = "USER")
    val role: UserRole,
    @Schema(description = "Активен ли пользователь", example = "true")
    val isActive: Boolean,
    @Schema(description = "Дата и время создания пользователя", example = "2024-01-01T12:00:00Z")
    val createdAt: OffsetDateTime,
    @Schema(description = "Дата и время последнего обновления данных пользователя", example = "2024-01-15T12:00:00Z")
    val updatedAt: OffsetDateTime,
)
