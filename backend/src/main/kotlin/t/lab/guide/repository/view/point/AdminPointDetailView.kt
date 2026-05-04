package t.lab.guide.repository.view.point

import java.time.OffsetDateTime

data class AdminPointDetailView(
    val id: Long,
    val title: String,
    val description: String? = null,
    val shortDescription: String? = null,
    val categoryId: Long,
    val categoryName: String,
    val address: String? = null,
    val latitude: Double,
    val longitude: Double,
    val visitTime: Int? = null,
    val workingHours: String? = null,
    val isActive: Boolean,
    val createdAt: OffsetDateTime,
    val updatedAt: OffsetDateTime,
)
