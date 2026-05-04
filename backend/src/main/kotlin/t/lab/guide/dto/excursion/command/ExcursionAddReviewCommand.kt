package t.lab.guide.dto.excursion.command

import java.time.LocalDate

data class ExcursionAddReviewCommand(
    val rating: Short,
    val reviewText: String?,
    val visitDate: LocalDate,
)
