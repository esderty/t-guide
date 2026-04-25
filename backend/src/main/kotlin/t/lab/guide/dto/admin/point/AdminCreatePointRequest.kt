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
    @field:NotBlank
    @field:Size(max = 255)
    val title: String,
    @Schema(
        description = "Подробное описание точки интереса",
        requiredMode = Schema.RequiredMode.REQUIRED,
        maxLength = 5000,
        example = "Красная площадь — главная площадь Москвы.",
    )
    @field:NotBlank
    @field:Size(max = 5000)
    val description: String,
    @Schema(
        description = "Идентификатор категории, к которой относится точка",
        requiredMode = Schema.RequiredMode.REQUIRED,
        example = "1",
    )
    @field:NotNull
    val categoryId: Long,
    @Schema(
        description = "Адрес точки интереса",
        requiredMode = Schema.RequiredMode.REQUIRED,
        maxLength = 500,
        example = "Москва, Красная площадь, 1",
    )
    @field:NotBlank
    @field:Size(max = 500)
    val address: String,
    @Schema(
        description = "Координаты точки",
        requiredMode = Schema.RequiredMode.REQUIRED,
    )
    @field:NotNull
    @field:Valid
    val coordinates: GeoPoint,
    @Schema(
        description = "Среднее время посещения точки интереса в минутах",
        requiredMode = Schema.RequiredMode.REQUIRED,
        example = "60",
    )
    @field:NotNull
    @field:Min(1)
    val visitTime: Int,
    @Schema(
        description = "Часы работы точки",
        requiredMode = Schema.RequiredMode.NOT_REQUIRED,
        maxLength = 255,
        example = "10:00 - 18:00",
    )
    @field:Size(max = 255)
    val workingHours: String? = null,
    @Schema(
        description = "Активна ли точка (показывается пользователям). По умолчанию true",
        requiredMode = Schema.RequiredMode.NOT_REQUIRED,
        example = "true",
    )
    val isActive: Boolean? = null,
)
