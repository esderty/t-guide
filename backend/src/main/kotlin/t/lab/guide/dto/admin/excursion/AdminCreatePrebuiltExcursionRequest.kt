package t.lab.guide.dto.admin.excursion

import io.swagger.v3.oas.annotations.media.ArraySchema
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.Size
import t.lab.guide.dto.excursion.ExcursionPointOrderItem
import t.lab.guide.entity.enums.ExcursionVisibility

@Schema(description = "Запрос на создание готовой (PREBUILT) экскурсии администратором")
data class AdminCreatePrebuiltExcursionRequest(
    @Schema(
        description = "Название экскурсии",
        requiredMode = Schema.RequiredMode.REQUIRED,
        maxLength = 255,
        example = "Исторический центр Москвы",
    )
    @field:NotBlank(message = "поле обязательно!")
    @field:Size(max = 255, message = "максимальная длина названия - 255 символов!")
    val title: String,
    @Schema(
        description = "Подробное описание экскурсии",
        requiredMode = Schema.RequiredMode.REQUIRED,
        maxLength = 2000,
        example = "Прогулка по главным достопримечательностям центра столицы: Красная площадь, ГУМ, Кремль.",
    )
    @field:NotBlank(message = "поле обязательно!")
    @field:Size(max = 2000, message = "максимальная длина описания - 2000 символов!")
    val description: String,
    @Schema(
        description = "Видимость экскурсии (PUBLIC, PRIVATE). По умолчанию PUBLIC",
        requiredMode = Schema.RequiredMode.NOT_REQUIRED,
        example = "PUBLIC",
    )
    val visibility: ExcursionVisibility? = null,
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
