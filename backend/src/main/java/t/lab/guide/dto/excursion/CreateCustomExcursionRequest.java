package t.lab.guide.dto.excursion;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

@Schema(description = "Данные для сохранения пользовательской экскурсии")
public record CreateCustomExcursionRequest(

        @Schema(description = "Название экскурсии",
                requiredMode = Schema.RequiredMode.REQUIRED,
                example = "Моя прогулка по центру")
        @NotBlank(message = "поле обязательно!")
        String title,

        @Schema(description = "Описание экскурсии",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED,
                example = "Короткий маршрут по главным достопримечательностям")
        String description,

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
) {
}