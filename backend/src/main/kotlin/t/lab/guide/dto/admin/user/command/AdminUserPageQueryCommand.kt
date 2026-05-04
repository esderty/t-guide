package t.lab.guide.dto.admin.user.command

import t.lab.guide.enums.AdminUserSortField
import t.lab.guide.enums.SortDirection

data class AdminUserPageQueryCommand(
    val page: Int,
    val size: Int,
    val sortBy: AdminUserSortField,
    val sortDirection: SortDirection,
    val search: String? = null,
)
