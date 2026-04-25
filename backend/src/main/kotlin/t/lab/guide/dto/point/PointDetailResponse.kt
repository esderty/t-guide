package t.lab.guide.dto.point

import io.swagger.v3.oas.annotations.media.Schema
import t.lab.guide.dto.common.GeoPoint

@Schema(description = "Подробная информация о точке интереса")
data class PointDetailResponse(
    @Schema(description = "Идентификатор точки интереса", example = "1")
    val id: Long,
    @Schema(description = "Название точки интереса", example = "Красная площадь")
    val title: String,
    @Schema(description = "Подробное описание точки интереса", example = "Красная площадь — главная площадь Москвы.")
    val description: String,
    @Schema(description = "Идентификатор категории, к которой относится точка", example = "1")
    val categoryId: Long,
    @Schema(description = "Название категории", example = "Достопримечательности")
    val categoryName: String,
    @Schema(description = "Адрес точки интереса", example = "Москва, Красная площадь, 1")
    val address: String,
    @Schema(description = "Координаты точки")
    val coordinates: GeoPoint,
    @Schema(description = "Среднее время посещения точки интереса в минутах", example = "30")
    val visitTime: Int,
    @Schema(description = "Часы работы точки", example = "10:00 - 18:00")
    val workingHours: String,
    @Schema(description = "Список медиа-материалов, связанных с точкой интереса")
    val media: List<PointMediaItem>,
)
