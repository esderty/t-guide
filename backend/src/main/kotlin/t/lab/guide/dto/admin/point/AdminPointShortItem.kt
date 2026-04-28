package t.lab.guide.dto.admin.point

import io.swagger.v3.oas.annotations.media.Schema
import java.time.OffsetDateTime

@Schema(description = "Краткая информация о точке интереса для административного интерфейса")
data class AdminPointShortItem(
    @Schema(description = "Идентификатор точки интереса", example = "1")
    val id: Long,
    @Schema(description = "Название точки интереса", example = "Красная площадь")
    val title: String,
    @Schema(description = "Идентификатор категории, к которой относится точка", example = "1")
    val categoryId: Long,
    @Schema(description = "Название категории", example = "Достопримечательности")
    val categoryName: String,
    @Schema(description = "Среднее время посещения точки в минутах", example = "60", nullable = true)
    val visitTime: Int? = null,
    @Schema(description = "Активна ли точка(показывается пользователям)")
    val isActive: Boolean,
    @Schema(description = "Дата и время создания точки интереса", example = "2024-01-01T12:00:00Z")
    val createdAt: OffsetDateTime,
)
