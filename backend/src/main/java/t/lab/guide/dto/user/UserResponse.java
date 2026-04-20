package t.lab.guide.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import t.lab.guide.entity.enums.UserLanguage;
import t.lab.guide.entity.enums.UserRole;

@Schema(description = "Ответ с данными пользователя")
@Builder
public record UserResponse(
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
        UserRole role
) {}