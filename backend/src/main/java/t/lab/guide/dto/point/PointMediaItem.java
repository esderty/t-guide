package t.lab.guide.dto.point;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import t.lab.guide.entity.enums.MediaType;

@Schema(description = "Медиа-материал, связанный с точкой интереса (фото, видео или аудиогид)")
@Builder
public record PointMediaItem(
        @Schema(description = "Ссылка на медиа-файл",
                example = "https://cdn.t-guide.mock/points/1/photo-/fsdah5432lkh2532.webp")
        String url,

        @Schema(description = "Тип медиа-материала", example = "PHOTO")
        MediaType type,

        @Schema(description = "Порядковый номер для сортировки",
                example = "0")
        Integer sortOrder
) {}