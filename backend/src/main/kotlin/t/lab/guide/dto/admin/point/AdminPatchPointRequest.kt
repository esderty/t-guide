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
    @field:Size(max = 255)
    val title: String? = null,
    @Schema(
        description = "Новое подробное описание точки интереса",
        requiredMode = Schema.RequiredMode.NOT_REQUIRED,
        maxLength = 5000,
        example = "Красная площадь — главная площадь Москвы.",
    )
    @field:Size(max = 5000)
    val description: String? = null,
    @Schema(
        description = "Новый идентификатор категории",
        requiredMode = Schema.RequiredMode.NOT_REQUIRED,
        example = "2",
    )
    val categoryId: Long? = null,
    @Schema(
        description = "Новый адрес точки интереса",
        requiredMode = Schema.RequiredMode.NOT_REQUIRED,
        maxLength = 500,
        example = "Москва, Красная площадь, 1",
    )
    @field:Size(max = 500)
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
    @field:Min(1)
    val visitTime: Int? = null,
    @Schema(
        description = "Новые часы работы точки",
        requiredMode = Schema.RequiredMode.NOT_REQUIRED,
        maxLength = 255,
        example = "10:00 - 22:00",
    )
    @field:Size(max = 255)
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
