package t.lab.guide.dto.admin.point.command

import t.lab.guide.dto.admin.point.AdminCreatePointRequest
import t.lab.guide.dto.admin.point.AdminUploadPointMediaRequest
import t.lab.guide.dto.common.command.toCommand
import t.lab.guide.enums.MediaType

fun AdminCreatePointRequest.toCommand(): AdminCreatePointCommand =
    AdminCreatePointCommand(
        title = requireNotNull(this.title) { "title not validated" },
        description = this.description,
        shortDescription = this.shortDescription,
        categoryId = this.categoryId!!,
        address = this.address,
        coordinates = requireNotNull(this.coordinates) { "coordinates not validated" }.toCommand(),
        visitTime = this.visitTime,
        workingHours = this.workingHours,
        isActive = this.isActive,
    )

fun AdminUploadPointMediaRequest.toCommand(): AdminUploadPointMediaCommand =
    AdminUploadPointMediaCommand(
        type = MediaType.valueOf(requireNotNull(this.type) { "type not validated" }),
        sortOrder = this.sortOrder,
    )
