package t.lab.guide.dto.excursion

import io.swagger.v3.oas.annotations.media.ArraySchema
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.Valid
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.Size

@Schema(description = "Запрос на изменение точек маршрута экскурсии с явным порядком прохождения")
data class SetExcursionPointsRequest(
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
