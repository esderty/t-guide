package t.lab.guide.dto.admin.excursion

import io.swagger.v3.oas.annotations.media.ArraySchema
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import t.lab.guide.dto.excursion.ExcursionPointOrderItem
import t.lab.guide.enums.ExcursionVisibility
import t.lab.guide.validation.ValidEnum

@Schema(description = "Запрос на создание готовой (PREBUILT) экскурсии администратором")
data class AdminCreatePrebuiltExcursionRequest(
    @Schema(
        description = "Название экскурсии",
        requiredMode = Schema.RequiredMode.REQUIRED,
        maxLength = 255,
        example = "Исторический центр Москвы",
        nullable = false,
    )
    @field:NotBlank(message = "поле обязательно!")
    @field:Size(max = 255, message = "максимальная длина названия - 255 символов!")
    val title: String? = null,
    @Schema(
        description = "Подробное описание экскурсии",
        requiredMode = Schema.RequiredMode.NOT_REQUIRED,
        maxLength = 5000,
        example = "Прогулка по главным достопримечательностям центра столицы: Красная площадь, ГУМ, Кремль.",
        nullable = true,
    )
    @field:Size(max = 5000, message = "максимальная длина описания 5000 символов")
    val description: String? = null,
    @Schema(
        description = "Краткое описание экскурсии для отображения в списках",
        requiredMode = Schema.RequiredMode.NOT_REQUIRED,
        maxLength = 255,
        example = "Прогулка по центру Москвы с посещением главных достопримечательностей.",
        nullable = true,
    )
    @field:Size(max = 255, message = "максимальная длина краткого описания 255 символов")
    val shortDescription: String? = null,
    @Schema(
        description = "Видимость экскурсии (PUBLIC, PRIVATE). По умолчанию PUBLIC",
        requiredMode = Schema.RequiredMode.NOT_REQUIRED,
        example = "PUBLIC",
    )
    @field:ValidEnum(
        enumClass = ExcursionVisibility::class,
    )
    val visibility: String = "PUBLIC",
    @ArraySchema(
        schema = Schema(implementation = ExcursionPointOrderItem::class),
        arraySchema =
            Schema(
                description = "Точки маршрута с явным порядком прохождения",
                requiredMode = Schema.RequiredMode.REQUIRED,
                nullable = false,
            ),
    )
    @field:NotNull(message = "поле обязательно!")
    @field:Size(min = 1, max = 25, message = "количество точек должно быть от 1 до 25!")
    @field:Valid
    val points: List<ExcursionPointOrderItem>?,
)
