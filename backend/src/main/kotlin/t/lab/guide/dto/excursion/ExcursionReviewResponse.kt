package t.lab.guide.dto.excursion

import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDate

@Schema(description = "Отзыв пользователя на экскурсию")
data class ExcursionReviewResponse(
    @Schema(description = "Идентификатор отзыва", example = "1")
    val id: Long,
    @Schema(description = "Идентификатор экскурсии, к которой относится отзыв", example = "10")
    val excursionId: Long,
    @Schema(description = "Идентификатор пользователя, оставившего отзыв", example = "42")
    val userId: Long,
    @Schema(
        description = "Оценка экскурсии по шкале от 1 до 5",
        example = "5",
        minimum = "1",
        maximum = "5",
    )
    val rating: Short,
    @Schema(
        description = "Текст отзыва",
        example = "Отличная экскурсия, узнали много нового!",
        nullable = true,
    )
    val reviewText: String?,
    @Schema(
        description = "Дата посещения экскурсии",
        example = "2024-05-01",
        format = "date",
    )
    val visitDate: LocalDate,
)
