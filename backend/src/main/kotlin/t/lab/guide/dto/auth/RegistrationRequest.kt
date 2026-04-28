package t.lab.guide.dto.auth

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import t.lab.guide.enums.UserLanguage

@Schema(description = "Данные для регистрации нового пользователя")
data class RegistrationRequest(
    @Schema(
        description = "Уникальное имя пользователя для входа в систему",
        requiredMode = Schema.RequiredMode.REQUIRED,
        maxLength = 50,
        example = "enzolu",
    )
    @field:NotBlank(message = "поле обязательно!")
    @field:Size(max = 50, message = "макс. длина 50 символов")
    val username: String,
    @Schema(
        description = "Электронная почта пользователя",
        requiredMode = Schema.RequiredMode.REQUIRED,
        maxLength = 254,
        example = "email@domain.zone",
    )
    @field:Email(message = "неверный формат email")
    @field:Size(max = 254, message = "макс. длина 254 символов")
    val email: String,
    @Schema(
        description = "Имя пользователя",
        requiredMode = Schema.RequiredMode.REQUIRED,
        maxLength = 50,
        example = "Игорь",
    )
    @field:NotBlank(message = "поле обязательно!")
    @field:Size(max = 50, message = "макс. длина 50 символов")
    val name: String,
    @Schema(
        description = "Пароль пользователя",
        requiredMode = Schema.RequiredMode.REQUIRED,
        minLength = 8,
        maxLength = 64,
        example = "securePassword123",
    )
    @field:NotBlank(message = "поле обязательно!")
    @field:Size(min = 8, max = 64, message = "длина должна быть от 8 до 64 символов")
    val password: String,
    @Schema(
        description = "Язык интерфейса пользователя",
        requiredMode = Schema.RequiredMode.REQUIRED,
        allowableValues = ["RU", "EN"],
        example = "RU",
    )
    @field:NotNull(message = "поле обязательно!")
    val lang: UserLanguage,
)
