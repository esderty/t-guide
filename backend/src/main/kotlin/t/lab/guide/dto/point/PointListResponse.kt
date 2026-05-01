package t.lab.guide.dto.point

import io.swagger.v3.oas.annotations.media.ArraySchema
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Ответ с краткой информацией о найденных точках интереса")
data class PointListResponse(
    @ArraySchema(
        arraySchema = Schema(description = "Список точек интереса"),
        schema = Schema(implementation = PointShortItem::class),
    )
    val points: List<PointShortItem>,
)
