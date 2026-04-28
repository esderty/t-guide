package t.lab.guide.dto.point

import io.swagger.v3.oas.annotations.media.Schema
import t.lab.guide.dto.common.GeoPoint

@Schema(description = "Краткая информация о точке интереса")
data class PointShortItem(
    @Schema(description = "Идентификатор точки интереса", example = "1")
    val id: Long,
    @Schema(description = "Название точки интереса", example = "Красная площадь")
    val title: String,
    @Schema(description = "Краткое описание точки интереса", example = "Главная площадь Москвы", nullable = true)
    val shortDescription: String? = null,
    @Schema(description = "Идентификатор категории, к которой относится точка", example = "1")
    val categoryId: Long,
    @Schema(description = "Название категории", example = "Достопримечательности")
    val categoryName: String,
    @Schema(description = "Координаты точки")
    val coordinates: GeoPoint,
    @Schema(description = "Среднее время посещения точки в минутах", example = "60", nullable = true)
    val visitTime: Int? = null,
)
