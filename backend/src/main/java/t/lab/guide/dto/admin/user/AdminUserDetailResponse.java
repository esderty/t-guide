package t.lab.guide.dto.admin.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import t.lab.guide.entity.enums.UserLanguage;
import t.lab.guide.entity.enums.UserRole;

import java.time.OffsetDateTime;

@Schema(description = "Ответ с данными пользователя для администратора")
@Builder
public record AdminUserDetailResponse(
        @Schema(description = "Уникальный идентификатор пользователя", example = "1")
        Long id,

        @Schema(description = "Уникальное имя пользователя для входа в систему", example = "enzolu")
        String username,

        @Schema(description = "Электронная почта пользователя", example = "email@domain.zone")
        String email,

        @Schema(description = "Отображаемое имя пользователя", example = "Игорь")
        String name,

        @Schema(description = "Язык интерфейса пользователя", example = "RU")
        UserLanguage lang,

        @Schema(description = "Роль пользователя в системе", example = "USER")
        UserRole role,

        @Schema(description = "Активен ли пользователь", example = "true")
        Boolean isActive,

        @Schema(description = "Дата и время создания пользователя", example = "2024-01-01T12:00:00Z")
        OffsetDateTime createdAt,

        @Schema(description = "Дата и время последнего обновления данных пользователя", example = "2024-01-15T12:00:00Z")
        OffsetDateTime updatedAt
) {}