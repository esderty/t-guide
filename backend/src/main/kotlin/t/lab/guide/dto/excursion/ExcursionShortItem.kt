package t.lab.guide.dto.excursion

import io.swagger.v3.oas.annotations.media.ArraySchema
import io.swagger.v3.oas.annotations.media.Schema
import t.lab.guide.dto.common.GeoPoint
import t.lab.guide.enums.ExcursionRouteType
import t.lab.guide.enums.ExcursionVisibility

@Schema(description = "Краткая информация об экскурсии")
data class ExcursionShortItem(
    @Schema(description = "Идентификатор экскурсии", example = "1")
    val id: Long,
    @Schema(description = "Тип экскурсии(PREBUILT, CUSTOM)", example = "PREBUILT")
    val routeType: ExcursionRouteType,
    @Schema(description = "Видимость экскурсии (PUBLIC, PRIVATE)", example = "PUBLIC")
    val visibility: ExcursionVisibility,
    @Schema(description = "Является ли текущий пользователь владельцем экскурсии", example = "true")
    val isOwner: Boolean,
    @Schema(description = "Название экскурсии", example = "Исторический центр Москвы")
    val title: String,
    @Schema(
        description = "Описание экскурсии",
        example = "Прогулка по центру Москвы с посещением главных достопримечательностей.",
        nullable = true,
    )
    val description: String? = null,
    @Schema(
        description = "Краткое описание экскурсии",
        example = "Прогулка по главным достопримечательностям центра столицы.",
        nullable = true,
    )
    val shortDescription: String? = null,
    @Schema(description = "Протяжённость маршрута в метрах", example = "3200")
    val distance: Int,
    @Schema(description = "Продолжительность экскурсии в минутах", example = "120")
    val durationMin: Int,
    @Schema(description = "Количество точек в маршруте", example = "5")
    val pointsCount: Int,
    @Schema(description = "Координаты начальной точки маршрута")
    val coordinates: GeoPoint,
    @ArraySchema(
        arraySchema = Schema(description = "Категории точек, входящих в маршрут"),
        schema = Schema(description = "Идентификатор категории", example = "1"),
    )
    val categoryIds: List<Long>,
)
