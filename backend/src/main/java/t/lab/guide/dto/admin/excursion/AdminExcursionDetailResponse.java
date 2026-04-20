package t.lab.guide.dto.admin.excursion;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import t.lab.guide.dto.admin.point.AdminPointShortItem;
import t.lab.guide.dto.common.GeoPoint;
import t.lab.guide.entity.enums.ExcursionRouteType;
import t.lab.guide.entity.enums.ExcursionVisibility;

import java.time.OffsetDateTime;
import java.util.List;

@Schema(description = "Подробная информация об экскурсии для административного интерфейса")
@Builder
public record AdminExcursionDetailResponse(
        @Schema(description = "Идентификатор экскурсии", example = "1")
        Long id,

        @Schema(description = "Идентификатор владельца экскурсии (null для PREBUILT)",
                example = "42", nullable = true)
        Long ownerId,

        @Schema(description = "Тип маршрута (PREBUILT, CUSTOM)", example = "PREBUILT")
        ExcursionRouteType routeType,

        @Schema(description = "Видимость экскурсии (PUBLIC, PRIVATE)", example = "PUBLIC")
        ExcursionVisibility visibility,

        @Schema(description = "Название экскурсии", example = "Исторический центр Москвы")
        String title,

        @Schema(description = "Подробное описание экскурсии",
                example = "Прогулка по главным достопримечательностям центра столицы: Красная площадь, ГУМ, Кремль.",
                nullable = true)
        String description,

        @Schema(description = "Протяжённость маршрута в метрах", example = "3200")
        Integer distance,

        @Schema(description = "Продолжительность экскурсии в минутах", example = "120")
        Integer durationMin,

        @Schema(description = "Координаты начальной точки маршрута")
        GeoPoint coordinates,

        @ArraySchema(
                arraySchema = @Schema(description = "Список точек интереса, входящих в маршрут экскурсии"),
                schema = @Schema(implementation = AdminPointShortItem.class)
        )
        List<AdminPointShortItem> points,

        @Schema(description = "Идентификатор пользователя, создавшего экскурсию", example = "42")
        Long createdBy,

        @Schema(description = "Дата и время создания экскурсии", example = "2024-01-01T12:00:00Z")
        OffsetDateTime createdAt,

        @Schema(description = "Дата и время последнего обновления экскурсии",
                example = "2024-01-15T12:00:00Z")
        OffsetDateTime updatedAt
) {}