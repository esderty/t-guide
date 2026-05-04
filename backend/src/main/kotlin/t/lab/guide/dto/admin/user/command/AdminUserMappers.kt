package t.lab.guide.dto.admin.user.command

import t.lab.guide.dto.admin.user.AdminUserPageQuery
import t.lab.guide.enums.AdminUserSortField
import t.lab.guide.enums.SortDirection

fun AdminUserPageQuery.toCommand(): AdminUserPageQueryCommand =
    AdminUserPageQueryCommand(
        page = this.page,
        size = this.size,
        sortBy = AdminUserSortField.valueOf(this.sortBy ?: AdminUserSortField.CREATED_AT.name),
        sortDirection = SortDirection.valueOf(this.sortDirection ?: SortDirection.DESC.name),
        search,
    )
