package t.lab.guide.dto.admin.point

import io.swagger.v3.oas.annotations.media.ArraySchema
import io.swagger.v3.oas.annotations.media.Schema
import t.lab.guide.dto.common.GeoPoint
import java.time.OffsetDateTime

@Schema(description = "Подробная информация о точке интереса для административного интерфейса")
data class AdminPointDetailResponse(
    @Schema(description = "Уникальный идентификатор точки интереса", example = "1")
    val id: Long,
    @Schema(description = "Название точки интереса", example = "Красная площадь")
    val title: String,
    @Schema(
        description = "Подробное описание точки интереса",
        example = "Красная площадь — главная площадь Москвы.",
    )
    val description: String,
    @Schema(description = "Идентификатор категории, к которой относится точка", example = "1")
    val categoryId: Long,
    @Schema(description = "Название категории", example = "Достопримечательности")
    val categoryName: String,
    @Schema(description = "Адрес точки интереса", example = "Москва, Красная площадь, 1")
    val address: String,
    @Schema(description = "Координаты точки")
    val coordinates: GeoPoint,
    @Schema(description = "Среднее время посещения точки интереса в минутах", example = "60")
    val visitTime: Int,
    @Schema(description = "Часы работы точки", example = "10:00 - 18:00", nullable = true)
    val workingHours: String?,
    @Schema(description = "Активна ли точка (показывается пользователям)", example = "true")
    val isActive: Boolean,
    @ArraySchema(
        arraySchema = Schema(description = "Список медиа-материалов, связанных с точкой интереса"),
        schema = Schema(implementation = AdminPointMediaItem::class),
    )
    val media: List<AdminPointMediaItem>,
    @Schema(description = "Дата и время создания точки интереса", example = "2024-01-01T12:00:00Z")
    val createdAt: OffsetDateTime,
    @Schema(description = "Дата и время последнего обновления точки интереса", example = "2024-01-15T12:00:00Z")
    val updatedAt: OffsetDateTime,
)
