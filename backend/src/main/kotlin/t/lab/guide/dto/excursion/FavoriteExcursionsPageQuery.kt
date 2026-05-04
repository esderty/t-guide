package t.lab.guide.dto.excursion

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min

@Schema(description = "Параметры выборки страницы избранных экскурсий пользователя")
data class FavoriteExcursionsPageQuery(
    @field:Min(0)
    @Schema(description = "Номер страницы (с 0)", example = "0", defaultValue = "0")
    val page: Int = 0,
    @field:Min(1)
    @field:Max(100)
    @Schema(description = "Размер страницы (1..100)", example = "25", defaultValue = "25")
    val size: Int = 25,
)
