package t.lab.guide.repository.view.point

data class PointShortView(
    val id: Long,
    val title: String,
    val shortDescription: String? = null,
    val categoryId: Long,
    val categoryName: String,
    val latitude: Double,
    val longitude: Double,
    val visitTime: Int? = null,
)
