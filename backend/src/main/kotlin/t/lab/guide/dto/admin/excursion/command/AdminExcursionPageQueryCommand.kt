package t.lab.guide.dto.admin.excursion.command

import t.lab.guide.enums.AdminExcursionSortField
import t.lab.guide.enums.SortDirection

data class AdminExcursionPageQueryCommand(
    val page: Int,
    val size: Int,
    val sortBy: AdminExcursionSortField,
    val sortDirection: SortDirection,
    val search: String? = null,
)
