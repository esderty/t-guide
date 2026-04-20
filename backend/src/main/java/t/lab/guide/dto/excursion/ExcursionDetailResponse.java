package t.lab.guide.dto.excursion;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import t.lab.guide.dto.common.GeoPoint;
import t.lab.guide.dto.point.PointListResponse;
import t.lab.guide.entity.enums.ExcursionRouteType;
import t.lab.guide.entity.enums.ExcursionVisibility;

@Schema(description = "Подробная информация об экскурсии с полным списком точек маршрута")
@Builder
public record ExcursionDetailResponse(
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

        @Schema(description = "Подробное описание экскурсии",
                example = "Прогулка по главным достопримечательностям центра столицы: Красная площадь, ГУМ, Кремль.")
        String description,

        @Schema(description = "Протяжённость маршрута в метрах", example = "3200")
        Integer distance,

        @Schema(description = "Продолжительность экскурсии в минутах", example = "120")
        Integer duration,

        @Schema(description = "Координаты начальной точки маршрута")
        GeoPoint coordinates,

        @Schema(description = "Список точек интереса, входящих в маршрут экскурсии")
        PointListResponse points
) {}