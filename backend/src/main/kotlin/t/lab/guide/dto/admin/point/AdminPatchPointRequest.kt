package t.lab.guide.dto.admin.point

import com.fasterxml.jackson.annotation.JsonIgnore
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.Valid
import jakarta.validation.constraints.AssertTrue
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.Size
import t.lab.guide.dto.common.GeoPoint

@Schema(
    description =
        "Запрос на частичное обновление точки интереса администратором. " +
            "Передаются только изменяемые поля; минимум одно поле обязательно.",
)
data class AdminPatchPointRequest(
    @Schema(
        description = "Новое название точки интереса",
        requiredMode = Schema.RequiredMode.NOT_REQUIRED,
        maxLength = 255,
        example = "Красная площадь",
    )
    @field:Size(max = 255, message = "максимальная длина названия - 255 символов!")
    val title: String? = null,
    @Schema(
        description = "Новое подробное описание точки интереса",
        requiredMode = Schema.RequiredMode.NOT_REQUIRED,
        maxLength = 5000,
        example = "Красная площадь — главная площадь Москвы.",
    )
    @field:Size(max = 5000, message = "максимальная длина описания - 5000 символов!")
    val description: String? = null,
    @Schema(
        description = "Новое краткое описание точки интереса",
        requiredMode = Schema.RequiredMode.NOT_REQUIRED,
        maxLength = 255,
        example = "Главная площадь Москвы",
    )
    @field:Size(max = 255, message = "максимальная длина краткого описания - 255 символов!")
    val shortDescription: String? = null,
    @Schema(
        description = "Новый идентификатор категории",
        requiredMode = Schema.RequiredMode.NOT_REQUIRED,
        example = "2",
    )
    val categoryId: Long? = null,
    @Schema(
        description = "Новый адрес точки интереса",
        requiredMode = Schema.RequiredMode.NOT_REQUIRED,
        maxLength = 255,
        example = "Москва, Красная площадь, 1",
    )
    @field:Size(max = 255, message = "максимальная длина адреса - 255 символов!")
    val address: String? = null,
    @Schema(
        description = "Новые координаты точки",
        requiredMode = Schema.RequiredMode.NOT_REQUIRED,
    )
    @field:Valid
    val coordinates: GeoPoint? = null,
    @Schema(
        description = "Новое среднее время посещения точки в минутах",
        requiredMode = Schema.RequiredMode.NOT_REQUIRED,
        example = "90",
    )
    @field:Min(1, message = "время посещения должно быть положительным числом!")
    val visitTime: Int? = null,
    @Schema(
        description = "Новые часы работы точки",
        requiredMode = Schema.RequiredMode.NOT_REQUIRED,
        maxLength = 255,
        example = "10:00 - 22:00",
    )
    @field:Size(max = 255, message = "максимальная длина часов работы - 255 символов!")
    val workingHours: String? = null,
    @Schema(
        description = "Активна ли точка (показывается пользователям)",
        requiredMode = Schema.RequiredMode.NOT_REQUIRED,
        example = "true",
    )
    val isActive: Boolean? = null,
) {
    @AssertTrue(message = "Необходимо передать хотя бы одно поле для обновления")
    @JsonIgnore
    @Schema(hidden = true)
    fun isAnyFieldPresent(): Boolean =
        title != null ||
            description != null ||
            categoryId != null ||
            address != null ||
            coordinates != null ||
            visitTime != null ||
            workingHours != null ||
            isActive != null
}
