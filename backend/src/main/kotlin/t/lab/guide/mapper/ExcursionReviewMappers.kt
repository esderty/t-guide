package t.lab.guide.mapper

import t.lab.guide.domain.ExcursionReview
import t.lab.guide.dto.excursion.ExcursionReviewResponse

fun ExcursionReview.toExcursionReviewResponse(): ExcursionReviewResponse =
    ExcursionReviewResponse(
        id = requireNotNull(this.id) { "ExcursionReview.id is null" },
        excursionId = requireNotNull(this.excursion?.id) { "ExcursionReview.excursionId is null" },
        userId = requireNotNull(this.user?.id) { "ExcursionReview.userId is null" },
        rating = this.rating,
        reviewText = this.reviewText,
        visitDate = this.visitDate,
    )
