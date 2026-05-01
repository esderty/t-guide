package t.lab.guide.dto.excursion

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotNull

@Schema(description = "Точка маршрута пользовательской экскурсии с явным порядком прохождения")
data class ExcursionPointOrderItem(
    @Schema(description = "Идентификатор точки интереса", example = "1")
    @field:NotNull(message = "поле обязательно!")
    val pointId: Long,
    @Schema(
        description = "Порядковый номер точки в маршруте (начиная с 0, без пропусков и дубликатов)",
        example = "1",
    )
    @field:NotNull(message = "поле обязательно!")
    @field:Min(1)
    val order: Int,
)
