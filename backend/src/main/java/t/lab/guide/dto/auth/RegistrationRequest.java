package t.lab.guide.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import t.lab.guide.entity.enums.UserLanguage;

@Schema(description = "Данные для регистрации нового пользователя")
public record RegistrationRequest(

        @Schema(description = "Уникальное имя пользователя для входа в систему",
                requiredMode = Schema.RequiredMode.REQUIRED,
                maxLength = 50,
                example = "enzolu")
        @NotBlank(message = "поле обязательно!")
        @Size(max = 50, message = "макс. длина 50 символов")
        String username,

        @Schema(description = "Электронная почта пользователя",
                requiredMode = Schema.RequiredMode.REQUIRED,
                maxLength = 50,
                example = "email@domain.zone")
        @Email(message = "неверный формат email")
        @Size(max = 50, message = "макс. длина 50 символов")
        String email,

        @Schema(description = "Имя пользователя",
                requiredMode = Schema.RequiredMode.REQUIRED,
                maxLength = 50,
                example = "Игорь")
        @NotBlank(message = "поле обязательно!")
        @Size(max = 50, message = "макс. длина 50 символов")
        String name,

        @Schema(description = "Пароль пользователя",
                requiredMode = Schema.RequiredMode.REQUIRED,
                minLength = 8,
                maxLength = 64,
                example = "securePassword123")
        @NotBlank(message = "поле обязательно!")
        @Size(min = 8, max = 64, message = "длина должна быть от 8 до 64 символов")
        String password,

        @Schema(description = "Язык интерфейса пользователя",
                requiredMode = Schema.RequiredMode.REQUIRED,
                allowableValues = {"RU", "EN"},
                example = "RU")
        @NotNull(message = "поле обязательно!")
        UserLanguage language
) {
}