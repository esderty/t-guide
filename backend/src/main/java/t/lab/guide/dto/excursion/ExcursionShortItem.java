package t.lab.guide.dto.excursion;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import t.lab.guide.dto.common.GeoPoint;
import t.lab.guide.entity.enums.ExcursionRouteType;
import t.lab.guide.entity.enums.ExcursionVisibility;

import java.util.List;

@Schema(description = "Краткая информация об экскурсии")
@Builder
public record ExcursionShortItem(
        @Schema(description = "Идентификатор экскурсии", example = "1")
        Long id,

        @Schema(description = "Тип экскурсии(PREBUILT, CUSTOM)", example = "PREBUILT")
        ExcursionRouteType routeType,

        @Schema(description = "Видимость экскурсии (PUBLIC, PRIVATE)", example = "PUBLIC")
        ExcursionVisibility visibility,

        @Schema(description = "Является ли текущий пользователь владельцем экскурсии", example = "true")
        Boolean isOwner,

        @Schema(description = "Название экскурсии", example = "Исторический центр Москвы")
        String title,

        @Schema(description = "Краткое описание экскурсии",
                example = "Прогулка по главным достопримечательностям центра столицы.")
        String description,

        @Schema(description = "Протяжённость маршрута в метрах", example = "3200")
        Integer distance,

        @Schema(description = "Продолжительность экскурсии в минутах", example = "120")
        Integer duration_min,

        @Schema(description = "Количество точек в маршруте", example = "5")
        Integer pointCounts,

        @Schema(description = "Координаты начальной точки маршрута")
        GeoPoint coordinates,

        @ArraySchema(
                arraySchema = @Schema(description = "Категории точек, входящих в маршрут"),
                schema = @Schema(description = "Идентификатор категории", example = "1")
        )
        List<Long> categoryIds
) {}