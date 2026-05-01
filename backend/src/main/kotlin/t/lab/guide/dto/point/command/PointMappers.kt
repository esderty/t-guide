package t.lab.guide.dto.point.command

import t.lab.guide.dto.common.command.toCommand
import t.lab.guide.dto.point.PointSearchRequest

fun PointSearchRequest.toCommand(): PointSearchCommand {
    return PointSearchCommand(
        location = requireNotNull(this.location) { "location not validated" }.toCommand(),
        radiusKilometers = requireNotNull(this.radiusKilometers) { "radiusKilometers not validated" },
        categorySlugs = this.categorySlugs,
        visitTime = this.visitTime,
    )
}
