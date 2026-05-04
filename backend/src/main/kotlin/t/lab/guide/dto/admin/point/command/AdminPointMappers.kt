package t.lab.guide.dto.admin.point.command

import t.lab.guide.dto.admin.point.AdminCreatePointRequest
import t.lab.guide.dto.admin.point.AdminPointPageQuery
import t.lab.guide.dto.admin.point.AdminUploadPointMediaRequest
import t.lab.guide.dto.common.command.toCommand
import t.lab.guide.enums.AdminPointSortField
import t.lab.guide.enums.MediaType
import t.lab.guide.enums.SortDirection
import t.lab.guide.exception.BadRequestException

fun AdminCreatePointRequest.toCommand(): AdminCreatePointCommand =
    AdminCreatePointCommand(
        title = this.title ?: throw BadRequestException("Поле 'title' обязательно"),
        description = this.description,
        shortDescription = this.shortDescription,
        categoryId = this.categoryId!!,
        address = this.address,
        coordinates = this.coordinates?.toCommand() ?: throw BadRequestException("Поле 'coordinates' обязательно"),
        visitTime = this.visitTime,
        workingHours = this.workingHours,
        isActive = this.isActive,
    )

fun AdminUploadPointMediaRequest.toCommand(): AdminUploadPointMediaCommand =
    AdminUploadPointMediaCommand(
        type = MediaType.valueOf(this.type ?: throw BadRequestException("Поле 'type' обязательно")),
        sortOrder = this.sortOrder,
    )

fun AdminPointPageQuery.toCommand(): AdminPointPageQueryCommand =
    AdminPointPageQueryCommand(
        page = this.page,
        size = this.size,
        sortBy = AdminPointSortField.valueOf(this.sortBy ?: AdminPointSortField.CREATED_AT.name),
        sortDirection = SortDirection.valueOf(this.sortDirection ?: SortDirection.DESC.name),
        search = search,
    )
