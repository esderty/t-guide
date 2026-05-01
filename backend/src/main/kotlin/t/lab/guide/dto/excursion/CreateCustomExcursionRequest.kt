package t.lab.guide.dto.excursion

import io.swagger.v3.oas.annotations.media.ArraySchema
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.Size
import t.lab.guide.enums.ExcursionVisibility
import t.lab.guide.validation.ValidEnum

@Schema(description = "Данные для сохранения пользовательской экскурсии")
data class CreateCustomExcursionRequest(
    @Schema(
        description = "Название экскурсии",
        requiredMode = Schema.RequiredMode.REQUIRED,
        example = "Моя прогулка по центру",
        nullable = false,
    )
    @field:NotBlank(message = "поле обязательно!")
    @field:Size(max = 255, message = "название должно быть не длиннее 255 символов!")
    val title: String? = null,
    @Schema(
        description = "Описание экскурсии",
        requiredMode = Schema.RequiredMode.NOT_REQUIRED,
        example = "Короткий маршрут по центру с посещением главных достопримечательностей.",
    )
    @field:Size(max = 5000, message = "описание должно быть не длиннее 5000 символов")
    val description: String? = null,
    @Schema(
        description = "Краткое описание экскурсии",
        requiredMode = Schema.RequiredMode.NOT_REQUIRED,
        example = "Короткий маршрут по главным достопримечательностям",
    )
    @field:Size(max = 255, message = "краткое описание должно быть не длиннее 255 символов!")
    val shortDescription: String? = null,
    @Schema(
        description = "Видимость экскурсии (PUBLIC - доступна всем, PRIVATE - доступна только автору)",
        requiredMode = Schema.RequiredMode.REQUIRED,
        example = "PUBLIC",
        nullable = false,
    )
    @field:ValidEnum(
        enumClass = ExcursionVisibility::class,
    )
    val visibility: String? = null,
    @ArraySchema(
        schema = Schema(implementation = ExcursionPointOrderItem::class),
        arraySchema =
            Schema(
                description = "Точки маршрута с явным порядком прохождения",
                requiredMode = Schema.RequiredMode.REQUIRED,
                nullable = false,
            ),
    )
    @field:NotEmpty(message = "поле обязательно!")
    @field:Size(min = 1, max = 25, message = "количество точек должно быть от 1 до 25!")
    @field:Valid
    val points: List<ExcursionPointOrderItem>? = null,
)
