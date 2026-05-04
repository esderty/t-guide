package t.lab.guide.dto.point.command

import t.lab.guide.dto.common.command.toCommand
import t.lab.guide.dto.point.PointSearchRequest
import t.lab.guide.exception.BadRequestException

fun PointSearchRequest.toCommand(): PointSearchCommand {
    return PointSearchCommand(
        location = this.location?.toCommand() ?: throw BadRequestException("Поле 'location' обязательно"),
        radiusKilometers = this.radiusKilometers ?: throw BadRequestException("Поле 'radiusKilometers' обязательно"),
        categoryIds = this.categoryIds,
        visitTime = this.visitTime,
    )
}
