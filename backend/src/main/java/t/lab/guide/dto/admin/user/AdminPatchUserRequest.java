package t.lab.guide.dto.admin.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import t.lab.guide.entity.enums.UserLanguage;
import t.lab.guide.entity.enums.UserRole;

public record AdminPatchUserRequest (

        @Schema(description = "Новый логин пользователя (уникальное имя для входа)",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED,
                maxLength = 50,
                example = "new_username")
        @Size(max = 50)
        String username,

        @Schema(description = "Новый адрес электронной почты",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED,
                maxLength = 50,
                example = "mail@domain.zone")
        @Email
        @Size(max = 50)
        String email,

        @Schema(description = "Новый язык интерфейса пользователя",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED,
                example = "RU")
        UserLanguage lang,

        @Schema(description = "Новая роль пользователя в системе",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED,
                example = "USER")
        UserRole role,

        @Schema(description = "Активен ли пользователь",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED,
                example = "true")
        Boolean isActive
) {
    @AssertTrue(message = "Необходимо передать хотя бы одно поле для обновления")
    @Schema(hidden = true)
    public boolean isAnyFieldPresent() {
        return username != null || email != null || lang != null || role != null || isActive != null;
    }
}
