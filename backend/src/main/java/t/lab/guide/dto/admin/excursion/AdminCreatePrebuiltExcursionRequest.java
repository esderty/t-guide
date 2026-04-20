package t.lab.guide.dto.admin.excursion;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import t.lab.guide.dto.excursion.ExcursionPointOrderItem;
import t.lab.guide.entity.enums.ExcursionVisibility;

import java.util.List;

@Schema(description = "Запрос на создание готовой (PREBUILT) экскурсии администратором")
public record AdminCreatePrebuiltExcursionRequest(
        @Schema(description = "Название экскурсии",
                requiredMode = Schema.RequiredMode.REQUIRED,
                maxLength = 255,
                example = "Исторический центр Москвы")
        @NotBlank(message = "поле обязательно!")
        @Size(max = 255, message = "максимальная длина названия - 255 символов!")
        String title,

        @Schema(description = "Подробное описание экскурсии",
                requiredMode = Schema.RequiredMode.REQUIRED,
                maxLength = 2000,
                example = "Прогулка по главным достопримечательностям центра столицы: Красная площадь, ГУМ, Кремль.")
        @NotBlank(message = "поле обязательно!")
        @Size(max = 2000, message = "максимальная длина описания - 2000 символов!")
        String description,

        @Schema(description = "Видимость экскурсии (PUBLIC, PRIVATE). По умолчанию PUBLIC",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED,
                example = "PUBLIC")
        ExcursionVisibility visibility,

        @ArraySchema(
                schema = @Schema(implementation = ExcursionPointOrderItem.class),
                arraySchema = @Schema(
                        description = "Точки маршрута с явным порядком прохождения",
                        requiredMode = Schema.RequiredMode.REQUIRED
                )
        )
        @NotEmpty(message = "поле обязательно!")
        @Valid
        List<ExcursionPointOrderItem> points
) {}