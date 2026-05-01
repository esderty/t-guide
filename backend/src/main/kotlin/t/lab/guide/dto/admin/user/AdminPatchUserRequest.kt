package t.lab.guide.dto.admin.user

import com.fasterxml.jackson.annotation.JsonIgnore
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.AssertTrue
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.Size
import t.lab.guide.enums.UserLanguage
import t.lab.guide.enums.UserRole
import t.lab.guide.validation.ValidEnum

data class AdminPatchUserRequest(
    @Schema(
        description = "Новый логин пользователя (уникальное имя для входа)",
        requiredMode = Schema.RequiredMode.NOT_REQUIRED,
        maxLength = 50,
        example = "new_username",
        nullable = true,
    )
    @field:Size(max = 50, message = "максимальная длина логина - 50 символов!")
    val username: String? = null,
    @Schema(
        description = "Новый адрес электронной почты",
        requiredMode = Schema.RequiredMode.NOT_REQUIRED,
        maxLength = 254,
        example = "mail@domain.zone",
        nullable = true,
    )
    @field:Email(message = "некорректный формат email")
    @field:Size(max = 254, message = "максимальная длина email - 254 символов!")
    val email: String? = null,
    @Schema(
        description = "Новый язык интерфейса пользователя",
        requiredMode = Schema.RequiredMode.NOT_REQUIRED,
        example = "RU",
        nullable = true,
    )
    @field:ValidEnum(
        enumClass = UserLanguage::class,
        message = "Не поддерживаемый язык. Допустимые значения: RU, EN",
    )
    val lang: String? = null,
    @Schema(
        description = "Новая роль пользователя в системе",
        requiredMode = Schema.RequiredMode.NOT_REQUIRED,
        example = "USER",
        nullable = true,
    )
    val role: UserRole? = null,
    @Schema(
        description = "Активен ли пользователь",
        requiredMode = Schema.RequiredMode.NOT_REQUIRED,
        example = "true",
        nullable = true,
    )
    val isActive: Boolean? = null,
) {
    @AssertTrue(message = "Необходимо передать хотя бы одно поле для обновления")
    @JsonIgnore
    @Schema(hidden = true)
    fun isAnyFieldPresent(): Boolean = username != null || email != null || lang != null || role != null || isActive != null
}
