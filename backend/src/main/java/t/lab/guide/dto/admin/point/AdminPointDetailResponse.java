package t.lab.guide.dto.admin.point;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import t.lab.guide.dto.common.GeoPoint;

import java.time.OffsetDateTime;
import java.util.List;

@Schema(description = "Подробная информация о точке интереса для административного интерфейса")
@Builder
public record AdminPointDetailResponse(
        @Schema(description = "Уникальный идентификатор точки интереса", example = "1")
        Long id,

        @Schema(description = "Название точки интереса", example = "Красная площадь")
        String title,

        @Schema(description = "Подробное описание точки интереса",
                example = "Красная площадь — главная площадь Москвы.")
        String description,

        @Schema(description = "Идентификатор категории, к которой относится точка", example = "1")
        Long categoryId,

        @Schema(description = "Название категории", example = "Достопримечательности")
        String categoryName,

        @Schema(description = "Адрес точки интереса", example = "Москва, Красная площадь, 1")
        String address,

        @Schema(description = "Координаты точки")
        GeoPoint coordinates,

        @Schema(description = "Среднее время посещения точки интереса в минутах", example = "60")
        Integer visitTime,

        @Schema(description = "Часы работы точки", example = "10:00 - 18:00")
        String workingHours,

        @Schema(description = "Активна ли точка (показывается пользователям)", example = "true")
        Boolean isActive,

        @Schema(description = "Список медиа-материалов, связанных с точкой интереса")
        List<AdminPointMediaItem> media,

        @Schema(description = "Дата и время создания точки интереса", example = "2024-01-01T12:00:00Z")
        OffsetDateTime createdAt,

        @Schema(description = "Дата и время последнего обновления точки интереса", example = "2024-01-15T12:00:00Z")
        OffsetDateTime updatedAt
) {}