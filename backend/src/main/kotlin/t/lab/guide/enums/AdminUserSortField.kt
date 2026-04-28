package t.lab.guide.enums

enum class AdminUserSortField(
    val value: String,
) {
    ID("id"),
    USERNAME("username"),
    EMAIL("email"),
    CREATED_AT("created_at"),
}
