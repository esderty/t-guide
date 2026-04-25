package t.lab.guide.dto.excursion

import io.swagger.v3.oas.annotations.media.ArraySchema
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Ответ с краткой информацией о найденных экскурсиях")
data class ExcursionListResponse(
    @ArraySchema(
        arraySchema = Schema(description = "Список экскурсий"),
        schema = Schema(implementation = ExcursionShortItem::class),
    )
    val excursions: List<ExcursionShortItem>,
)
