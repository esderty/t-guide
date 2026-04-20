package t.lab.guide.dto.admin.point;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.OffsetDateTime;

@Schema(description = "Краткая информация о точке интереса для административного интерфейса")
public record AdminPointShortItem(
        @Schema(description = "Идентификатор точки интереса", example = "1")
        Long id,

        @Schema(description = "Название точки интереса", example = "Красная площадь")
        String title,

        @Schema(description = "Идентификатор категории, к которой относится точка", example = "1")
        Long categoryId,

        @Schema(description = "Название категории", example = "Достопримечательности")
        String categoryName,

        @Schema(description = "Среднее время посещения точки в минутах", example = "60")
        Integer visitTime,

        @Schema(description = "Активна ли точка(показывается пользователям)")
        Boolean isActive,

        @Schema(description = "Дата и время создания точки интереса", example = "2024-01-01T12:00:00Z")
        OffsetDateTime createdAt
) {}