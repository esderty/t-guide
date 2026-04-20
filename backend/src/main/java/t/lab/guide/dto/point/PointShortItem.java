package t.lab.guide.dto.point;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import t.lab.guide.dto.common.GeoPoint;

@Schema(description = "Краткая информация о точке интереса")
@Builder
public record PointShortItem(
        @Schema(description = "Идентификатор точки интереса", example = "1")
        Long id,

        @Schema(description = "Название точки интереса", example = "Красная площадь")
        String title,

        @Schema(description = "Идентификатор категории, к которой относится точка", example = "1")
        Long categoryId,

        @Schema(description = "Название категории", example = "Достопримечательности")
        String categoryName,

        @Schema(description = "Координаты точки")
        GeoPoint coordinates,

        @Schema(description = "Среднее время посещения точки в минутах", example = "60")
        Integer visitTime
) {}