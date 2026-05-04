package t.lab.guide.enums

enum class AdminExcursionSortField(
    val value: String,
) {
    DISTANCE("distance"),
    ROUTE_TYPE("route_type"),
    OWNER("owner"),
    CREATED_AT("created_at"),
}
