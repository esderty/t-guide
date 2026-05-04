package t.lab.guide.dto.admin.point.command

import t.lab.guide.enums.AdminPointSortField
import t.lab.guide.enums.SortDirection

data class AdminPointPageQueryCommand(
    val page: Int,
    val size: Int,
    val sortBy: AdminPointSortField,
    val sortDirection: SortDirection,
    val search: String? = null,
)
