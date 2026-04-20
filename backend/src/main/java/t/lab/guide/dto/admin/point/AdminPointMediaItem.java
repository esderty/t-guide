package t.lab.guide.dto.admin.point;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import t.lab.guide.entity.enums.MediaType;

import java.time.OffsetDateTime;

@Schema(description = "Медиа-материал точки интереса в административном представлении (с идентификатором для управления)")
@Builder
public record AdminPointMediaItem(
        @Schema(description = "Уникальный идентификатор медиа-материала", example = "10")
        Long id,
        
        @Schema(description = "Идентификатор точки, к которому отновится media")
        Long pointId,

        @Schema(description = "Ссылка на медиа-файл",
                example = "https://cdn.t-guide.mock/points/1/photo-1.jpg")
        String url,

        @Schema(description = "Тип медиа-материала", example = "PHOTO")
        MediaType type,

        @Schema(description = "Порядковый номер для сортировки", example = "0")
        Integer sortOrder,

        @Schema(description = "Дата и время загрузки медиа", example = "2024-01-01T12:00:00Z")
        OffsetDateTime createdAt
) {}