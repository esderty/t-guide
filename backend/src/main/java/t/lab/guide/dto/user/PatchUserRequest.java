package t.lab.guide.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import t.lab.guide.entity.enums.UserLanguage;

@Schema(description = "Данные для частичного обновления информации о пользователе. Все поля необязательные, при их наличии будут обновлены соответствующие данные пользователя.")
public record PatchUserRequest(

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

        @Schema(description = "Новое отображаемое имя пользователя",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED,
                maxLength = 50,
                example = "Игорь")
        @Size(max = 50)
        String name,

        @Schema(description = "Новый язык интерфейса пользователя",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED,
                example = "RU")
        UserLanguage lang
) {
        @AssertTrue(message = "Необходимо передать хотя бы одно поле для обновления")
        @Schema(hidden = true)
        public boolean isAnyFieldPresent() {
                return username != null || email != null || name != null || lang != null;
        }
}