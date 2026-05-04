package t.lab.guide.repository.view.point

import java.time.OffsetDateTime

data class AdminPointShortView(
    val id: Long,
    val title: String,
    val categoryId: Long,
    val categoryName: String,
    val visitTime: Int? = null,
    val latitude: Double,
    val longitude: Double,
    val isActive: Boolean,
    val createdAt: OffsetDateTime,
)
