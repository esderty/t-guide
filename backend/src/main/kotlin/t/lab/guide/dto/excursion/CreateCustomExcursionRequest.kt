package t.lab.guide.dto.excursion

import io.swagger.v3.oas.annotations.media.ArraySchema
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty

@Schema(description = "Данные для сохранения пользовательской экскурсии")
data class CreateCustomExcursionRequest(
    @Schema(
        description = "Название экскурсии",
        requiredMode = Schema.RequiredMode.REQUIRED,
        example = "Моя прогулка по центру",
    )
    @field:NotBlank(message = "поле обязательно!")
    val title: String,
    @Schema(
        description = "Описание экскурсии",
        requiredMode = Schema.RequiredMode.NOT_REQUIRED,
        example = "Короткий маршрут по главным достопримечательностям",
    )
    val description: String,
    @ArraySchema(
        schema = Schema(implementation = ExcursionPointOrderItem::class),
        arraySchema =
            Schema(
                description = "Точки маршрута с явным порядком прохождения",
                requiredMode = Schema.RequiredMode.REQUIRED,
            ),
    )
    @field:NotEmpty(message = "поле обязательно!")
    @field:Valid
    val points: List<ExcursionPointOrderItem>,
)
