package t.lab.guide.dto.excursion;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import t.lab.guide.dto.common.GeoPoint;

import java.util.List;

@Schema(description = "Параметры поиска экскурсий в заданном радиусе от пользователя")
public record ExcursionSearchRequest(

        @Schema(description = "Местоположение пользователя",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @Valid
        @NotNull(message = "поле обязательно!")
        GeoPoint location,

        @Schema(description = "Радиус поиска экскурсий в километрах",
                requiredMode = Schema.RequiredMode.REQUIRED,
                minimum = "1",
                maximum = "5",
                example = "1")
        @NotNull(message = "поле обязательно!")
        @Min(value = 1, message = "радиус должен быть не меньше 1 километра!")
        @Max(value = 5, message = "радиус должен быть не больше 5 километров!")
        Integer radiusKilometers,

        @ArraySchema(
                schema = @Schema(description = "Идентификатор категории экскурсий", example = "1"),
                arraySchema = @Schema(
                        description = "Фильтр по категориям. Если не задан или пуст — возвращаются точки всех категорий",
                        requiredMode = Schema.RequiredMode.NOT_REQUIRED
                )
        )
        List<Long> categoryIds,

        @Schema(description = "Желаемое время экскурсии в минутах. Используется для подбора маршрута по времени",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED,
                minimum = "1",
                example = "60")
        @Min(value = 1, message = "время посещения должно быть положительным!")
        Integer visitTime
) {
}