package t.lab.guide.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Данные для входа в систему по логину и паролю")
public record AuthRequest(

        @Schema(description = "Логин пользователя (уникальное имя для входа в систему)",
                requiredMode = Schema.RequiredMode.REQUIRED,
                maxLength = 50,
                example = "enzolu")
        @NotBlank(message = "поле обязательно!")
        @Size(max = 50, message = "максимальная длина 50 символов!")
        String username,

        @Schema(description = "Пароль для входа в систему",
                requiredMode = Schema.RequiredMode.REQUIRED,
                minLength = 8,
                maxLength = 64,
                example = "securePassword123")
        @NotBlank(message = "поле обязательно!")
        @Size(min = 8, max = 64, message = "длина должна быть от 8 до 64 символов")
        String password
) {
}