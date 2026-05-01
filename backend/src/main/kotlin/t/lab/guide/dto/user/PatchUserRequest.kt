package t.lab.guide.dto.user

import com.fasterxml.jackson.annotation.JsonIgnore
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.AssertTrue
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.Size
import t.lab.guide.enums.UserLanguage
import t.lab.guide.validation.ValidEnum

@Schema(
    description = "Данные для частичного обновления информации о пользователе.",
)
data class PatchUserRequest(
    @Schema(
        description = "Новый логин пользователя (уникальное имя для входа)",
        requiredMode = Schema.RequiredMode.NOT_REQUIRED,
        maxLength = 50,
        example = "new_username",
    )
    @field:Size(max = 50)
    val username: String? = null,
    @Schema(
        description = "Новый адрес электронной почты",
        requiredMode = Schema.RequiredMode.NOT_REQUIRED,
        maxLength = 254,
        example = "mail@domain.zone",
    )
    @field:Email(message = "Некорректный формат email")
    @field:Size(max = 254, message = "Email не должен превышать 254 символов")
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
    @field:ValidEnum(
        enumClass = UserLanguage::class,
    )
    val lang: String? = null,
) {
    @AssertTrue(message = "Хотя бы одно поле должно быть заполнено для обновления")
    @JsonIgnore
    @Schema(hidden = true)
    fun isAnyFieldPresent(): Boolean = username != null || email != null || name != null || lang != null
}
