package t.lab.guide.dto.admin.point

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.Valid
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import t.lab.guide.dto.common.GeoPoint

@Schema(description = "Запрос на создание точки интереса администратором")
data class AdminCreatePointRequest(
    @Schema(
        description = "Название точки интереса",
        requiredMode = Schema.RequiredMode.REQUIRED,
        maxLength = 255,
        example = "Красная площадь",
    )
    @field:NotBlank(message = "поле обязательно")
    @field:Size(max = 255, message = "максимальная длина названия - 255 символов!")
    val title: String,
    @Schema(
        description = "Подробное описание точки интереса",
        requiredMode = Schema.RequiredMode.NOT_REQUIRED,
        maxLength = 5000,
        example = "Красная площадь — главная площадь Москвы.",
    )
    @field:Size(max = 5000, message = "максимальная длина описания - 5000 символов!")
    val description: String? = null,
    @Schema(
        description = "Краткое описание точки интереса",
        requiredMode = Schema.RequiredMode.NOT_REQUIRED,
        maxLength = 255,
        example = "Главная площадь Москвы",
    )
    @field:Size(max = 255, message = "максимальная длина краткого описания - 255 символов!")
    val shortDescription: String? = null,
    @Schema(
        description = "Идентификатор категории, к которой относится точка",
        requiredMode = Schema.RequiredMode.REQUIRED,
        example = "1",
    )
    @field:NotNull(message = "поле обязательно")
    val categoryId: Long,
    @Schema(
        description = "Адрес точки интереса",
        requiredMode = Schema.RequiredMode.NOT_REQUIRED,
        maxLength = 255,
        example = "Москва, Красная площадь, 1",
    )
    @field:Size(max = 255, message = "максимальная длина адреса - 255 символов!")
    val address: String? = null,
    @Schema(
        description = "Координаты точки",
        requiredMode = Schema.RequiredMode.REQUIRED,
    )
    @field:NotNull(message = "поле обязательно")
    @field:Valid
    val coordinates: GeoPoint,
    @Schema(
        description = "Среднее время посещения точки интереса в минутах",
        requiredMode = Schema.RequiredMode.NOT_REQUIRED,
        example = "60",
    )
    @field:Min(1, message = "время посещения должно быть положительным числом")
    val visitTime: Int? = null,
    @Schema(
        description = "Часы работы точки",
        requiredMode = Schema.RequiredMode.NOT_REQUIRED,
        maxLength = 255,
        example = "10:00 - 18:00",
    )
    @field:Size(max = 255, message = "максимальная длина часов работы - 255 символов!")
    val workingHours: String? = null,
    @Schema(
        description = "Активна ли точка (показывается пользователям). По умолчанию true",
        requiredMode = Schema.RequiredMode.NOT_REQUIRED,
        example = "true",
    )
    val isActive: Boolean = true,
)
