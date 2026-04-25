package t.lab.guide.dto.excursion

import com.fasterxml.jackson.annotation.JsonIgnore
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.AssertTrue
import jakarta.validation.constraints.Size
import t.lab.guide.entity.enums.ExcursionVisibility

@Schema(
    description =
        "Запрос на частичное обновление пользовательской экскурсии. " +
            "Передаются только изменяемые поля; минимум одно поле обязательно.",
)
data class UpdateCustomExcursionRequest(
    @Schema(
        description = "Новое название экскурсии",
        example = "Обновлённый маршрут по центру",
        minLength = 1,
        maxLength = 255,
        nullable = true,
    )
    @field:Size(min = 1, max = 255, message = "максимальная длина названия - 255 символов!")
    val title: String? = null,
    @Schema(
        description = "Новое описание экскурсии",
        example = "Обновлённое описание маршрута",
        maxLength = 2000,
        nullable = true,
    )
    @field:Size(max = 2000, message = "максимальная длина описания - 2000 символов!")
    val description: String? = null,
    @Schema(description = "Новая видимость экскурсии (PUBLIC, PRIVATE)", example = "PUBLIC", nullable = true)
    val visibility: ExcursionVisibility? = null,
) {
    @AssertTrue(message = "Хотя бы одно поле должно быть заполнено для обновления")
    @JsonIgnore
    @Schema(hidden = true)
    fun isAnyFieldPresent(): Boolean = title != null || description != null || visibility != null
}
