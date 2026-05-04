package t.lab.guide.repository.view.excursion

import t.lab.guide.enums.ExcursionRouteType
import t.lab.guide.enums.ExcursionVisibility

data class ExcursionShortView(
    val id: Long,
    val title: String,
    val description: String? = null,
    val shortDescription: String? = null,
    val latitude: Double,
    val longitude: Double,
    val distance: Int,
    val durationMin: Int,
    val pointsCount: Int,
    val routeType: ExcursionRouteType,
    val visibility: ExcursionVisibility,
    val ownerId: Long?,
    val categoryIds: List<Long>,
    val rating: Double?,
    val reviewsCount: Int,
)
