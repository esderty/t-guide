package t.lab.guide.dto.admin.excursion.command

import t.lab.guide.dto.admin.excursion.AdminCreatePrebuiltExcursionRequest
import t.lab.guide.dto.admin.excursion.AdminExcursionPageQuery
import t.lab.guide.dto.excursion.command.toCommand
import t.lab.guide.enums.AdminExcursionSortField
import t.lab.guide.enums.AdminPointSortField
import t.lab.guide.enums.ExcursionVisibility
import t.lab.guide.enums.SortDirection
import t.lab.guide.exception.BadRequestException

fun AdminCreatePrebuiltExcursionRequest.toCommand(): AdminCreatePrebuiltExcursionCommand =
    AdminCreatePrebuiltExcursionCommand(
        title = this.title ?: throw BadRequestException("Поле 'title' обязательно"),
        description = this.description,
        shortDescription = this.shortDescription,
        visibility = ExcursionVisibility.valueOf(this.visibility),
        points = this.points?.map { it.toCommand() } ?: throw BadRequestException("Поле 'points' обязательно"),
    )

fun AdminExcursionPageQuery.toCommand(): AdminExcursionPageQueryCommand =
    AdminExcursionPageQueryCommand(
        page = this.page,
        size = this.size,
        sortBy = AdminExcursionSortField.valueOf(this.sortBy ?: AdminPointSortField.CREATED_AT.name),
        sortDirection = SortDirection.valueOf(this.sortDirection ?: SortDirection.DESC.name),
        search = search,
    )
