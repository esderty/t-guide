package t.lab.guide.dto.point;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import t.lab.guide.dto.common.GeoPoint;

import java.util.List;

@Schema(description = "Подробная информация о точке интереса")
@Builder
public record PointDetailResponse(
        @Schema(description = "Идентификатор точки интереса", example = "1")
        Long id,

        @Schema(description = "Название точки интереса", example = "Красная площадь")
        String title,

        @Schema(description = "Подробное описание точки интереса", example = "Красная площадь — главная площадь Москвы.")
        String description,

        @Schema(description = "Идентификатор категории, к которой относится точка", example = "1")
        Long categoryId,

        @Schema(description = "Название категории", example = "Достопримечательности")
        String categoryName,

        @Schema(description = "Адрес точки интереса", example = "Москва, Красная площадь, 1")
        String address,

        @Schema(description = "Координаты точки")
        GeoPoint coordinates,

        @Schema(description = "Среднее время посещения точки интереса в минутах", example = "30")
        Integer visitTime,

        @Schema(description = "Часы работы точки", example = "10:00 - 18:00")
        String workingHours,

        @Schema(description = "Список медиа-материалов, связанных с точкой интереса")
        List<PointMediaItem> media
) {}