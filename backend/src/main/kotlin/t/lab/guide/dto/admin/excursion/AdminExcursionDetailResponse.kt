package t.lab.guide.dto.admin.excursion

import io.swagger.v3.oas.annotations.media.ArraySchema
import io.swagger.v3.oas.annotations.media.Schema
import t.lab.guide.dto.admin.point.AdminPointShortItem
import t.lab.guide.dto.common.GeoPoint
import t.lab.guide.enums.ExcursionRouteType
import t.lab.guide.enums.ExcursionVisibility
import java.time.OffsetDateTime

@Schema(description = "Подробная информация об экскурсии для административного интерфейса")
data class AdminExcursionDetailResponse(
    @Schema(description = "Идентификатор экскурсии", example = "1")
    val id: Long,
    @Schema(
        description = "Идентификатор владельца экскурсии (null для PREBUILT)",
        example = "42",
        nullable = true,
    )
    val ownerId: Long?,
    @Schema(description = "Тип маршрута (PREBUILT, CUSTOM)", example = "PREBUILT")
    val routeType: ExcursionRouteType,
    @Schema(description = "Видимость экскурсии (PUBLIC, PRIVATE)", example = "PUBLIC")
    val visibility: ExcursionVisibility,
    @Schema(description = "Название экскурсии", example = "Исторический центр Москвы")
    val title: String,
    @Schema(
        description = "Подробное описание экскурсии",
        example = "Прогулка по главным достопримечательностям центра столицы: Красная площадь, ГУМ, Кремль.",
        nullable = true,
    )
    val description: String?,
    @Schema(description = "Протяжённость маршрута в метрах", example = "3200")
    val distance: Int,
    @Schema(description = "Продолжительность экскурсии в минутах", example = "120")
    val durationMin: Int,
    @Schema(description = "Координаты начальной точки маршрута")
    val coordinates: GeoPoint,
    @ArraySchema(
        arraySchema = Schema(description = "Список точек интереса, входящих в маршрут экскурсии"),
        schema = Schema(implementation = AdminPointShortItem::class),
    )
    val points: List<AdminPointShortItem>,
    @Schema(description = "Идентификатор пользователя, создавшего экскурсию", example = "42")
    val createdBy: Long,
    @Schema(description = "Дата и время создания экскурсии", example = "2024-01-01T12:00:00Z")
    val createdAt: OffsetDateTime,
    @Schema(description = "Дата и время последнего обновления экскурсии", example = "2024-01-15T12:00:00Z")
    val updatedAt: OffsetDateTime,
)
