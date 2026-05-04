package t.lab.guide.dto.excursion

import io.swagger.v3.oas.annotations.media.ArraySchema
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Список отзывов на экскурсию")
data class ExcursionReviewListResponse(
    @ArraySchema(
        arraySchema = Schema(description = "Отзывы пользователей"),
        schema = Schema(implementation = ExcursionReviewResponse::class),
    )
    val reviews: List<ExcursionReviewResponse>,
)
