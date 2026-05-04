package t.lab.guide.repository.view.point

data class PointDetailView(
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
)
