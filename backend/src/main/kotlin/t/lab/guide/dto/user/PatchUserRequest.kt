package t.lab.guide.dto.user

import com.fasterxml.jackson.annotation.JsonIgnore
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.AssertTrue
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.Size
import t.lab.guide.entity.enums.UserLanguage

@Schema(
    description = "Данные для частичного обновления информации о пользователе. Все поля необязательные, при их наличии будут обновлены соответствующие данные пользователя.",
)
data class PatchUserRequest(
    @Schema(
        description = "Новый логин пользователя (уникальное имя для входа)",
        requiredMode = Schema.RequiredMode.NOT_REQUIRED,
        maxLength = 50,
        example = "new_username",
    )
    @field:Size(max = 50)
    val userName: String? = null,
    @Schema(
        description = "Новый адрес электронной почты",
        requiredMode = Schema.RequiredMode.NOT_REQUIRED,
        maxLength = 50,
        example = "mail@domain.zone",
    )
    @field:Email(message = "Некорректный формат email")
    @field:Size(max = 50, message = "Email не должен превышать 50 символов")
    val email: String? = null,
    @Schema(
        description = "Новое отображаемое имя пользователя",
        requiredMode = Schema.RequiredMode.NOT_REQUIRED,
        maxLength = 50,
        example = "Игорь",
    )
    @field:Size(max = 50)
    val name: String? = null,
    @Schema(
        description = "Новый язык интерфейса пользователя",
        requiredMode = Schema.RequiredMode.NOT_REQUIRED,
        example = "RU",
    )
    val language: UserLanguage? = null,
) {
    @AssertTrue(message = "Хотя бы одно поле должно быть заполнено для обновления")
    @JsonIgnore
    @Schema(hidden = true)
    fun isAnyFieldPresent(): Boolean = userName != null || email != null || name != null || language != null
}
