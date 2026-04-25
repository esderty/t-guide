package t.lab.guide.dto.excursion

import io.swagger.v3.oas.annotations.media.Schema
import t.lab.guide.dto.common.GeoPoint
import t.lab.guide.dto.point.PointListResponse
import t.lab.guide.entity.enums.ExcursionRouteType
import t.lab.guide.entity.enums.ExcursionVisibility

@Schema(description = "Подробная информация об экскурсии с полным списком точек маршрута")
data class ExcursionDetailResponse(
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
        description = "Подробное описание экскурсии",
        example = "Прогулка по главным достопримечательностям центра столицы: Красная площадь, ГУМ, Кремль.",
    )
    val description: String,
    @Schema(description = "Протяжённость маршрута в метрах", example = "3200")
    val distance: Int,
    @Schema(description = "Продолжительность экскурсии в минутах", example = "120")
    val duration: Int,
    @Schema(description = "Координаты начальной точки маршрута")
    val coordinates: GeoPoint,
    @Schema(description = "Список точек интереса, входящих в маршрут экскурсии")
    val points: PointListResponse,
)
