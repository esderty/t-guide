package t.lab.guide.dto.point

import io.swagger.v3.oas.annotations.media.Schema
import t.lab.guide.enums.MediaType

@Schema(description = "Медиа-материал, связанный с точкой интереса (фото, видео или аудиогид)")
data class PointMediaItem(
    @Schema(
        description = "Ссылка на медиа-файл",
        example = "https://cdn.t-guide.mock/points/1/photo-/fsdah5432lkh2532.webp",
    )
    val url: String,
    @Schema(description = "Тип медиа-материала", example = "PHOTO")
    val type: MediaType,
    @Schema(
        description = "Порядковый номер для сортировки",
        example = "0",
    )
    val sortOrder: Int,
)
