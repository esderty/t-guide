package t.lab.guide.dto.admin.user

import io.swagger.v3.oas.annotations.media.Schema
import t.lab.guide.enums.UserRole
import java.time.OffsetDateTime

@Schema(description = "Краткая информация о пользователе для списка в админ-панели")
data class AdminUserShortItem(
    @Schema(description = "Уникальный идентификатор пользователя", example = "1")
    val id: Long,
    @Schema(description = "Уникальное имя пользователя для входа в систему", example = "enzolu")
    val username: String,
    @Schema(description = "Электронная почта пользователя", example = "email@domain.zone")
    val email: String,
    @Schema(description = "Роль пользователя в системе", example = "USER")
    val role: UserRole,
    @Schema(description = "Активен ли пользователь", example = "true")
    val isActive: Boolean,
    @Schema(description = "Дата и время создания пользователя", example = "2024-01-01T12:00:00Z")
    val createdAt: OffsetDateTime,
)
