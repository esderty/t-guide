package t.lab.guide.dto.excursion

import io.swagger.v3.oas.annotations.media.ArraySchema
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.Valid
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotNull
import t.lab.guide.dto.common.GeoPoint

@Schema(description = "Параметры поиска экскурсий в заданном радиусе от пользователя")
data class ExcursionSearchRequest(
    @Schema(
        description = "Местоположение пользователя",
        requiredMode = Schema.RequiredMode.REQUIRED,
        nullable = false,
    )
    @field:Valid
    @field:NotNull(message = "поле обязательно!")
    val location: GeoPoint? = null,
    @Schema(
        description = "Радиус поиска экскурсий в километрах",
        requiredMode = Schema.RequiredMode.REQUIRED,
        minimum = "1",
        maximum = "15",
        example = "1",
        nullable = false,
    )
    @field:NotNull(message = "поле обязательно!")
    @field:Min(value = 1, message = "радиус должен быть не меньше 1 километра!")
    @field:Max(value = 15, message = "радиус должен быть не больше 15 километров!")
    val radiusKilometers: Int? = null,
    @ArraySchema(
        schema = Schema(description = "Идентификатор категории экскурсий", example = "1"),
        arraySchema =
            Schema(
                description = "Фильтр по категориям. Если не задан или пуст — возвращаются точки всех категорий",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED,
            ),
    )
    val categoryIds: List<Long> = emptyList(),
    @Schema(
        description = "Желаемое время экскурсии в минутах. Используется для подбора маршрута по времени",
        requiredMode = Schema.RequiredMode.NOT_REQUIRED,
        minimum = "1",
        example = "60",
    )
    @field:Min(value = 1, message = "время посещения должно быть положительным!")
    val visitTime: Int? = null,
)
