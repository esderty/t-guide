package t.lab.guide.dto.admin.point

import io.swagger.v3.oas.annotations.media.Schema
import t.lab.guide.enums.MediaType
import java.time.OffsetDateTime

@Schema(description = "Медиа-материал точки интереса в административном представлении (с идентификатором для управления)")
data class AdminPointMediaItem(
    @Schema(description = "Уникальный идентификатор медиа-материала", example = "10")
    val id: Long,
    @Schema(description = "Идентификатор точки, к которому относится media")
    val pointId: Long,
    @Schema(
        description = "Ссылка на медиа-файл",
        example = "https://cdn.t-guide.mock/points/1/photo-1.jpg",
    )
    val url: String,
    @Schema(description = "Тип медиа-материала", example = "PHOTO")
    val type: MediaType,
    @Schema(description = "Порядковый номер для сортировки", example = "0")
    val sortOrder: Int,
    @Schema(description = "Дата и время загрузки медиа", example = "2024-01-01T12:00:00Z")
    val createdAt: OffsetDateTime,
)
