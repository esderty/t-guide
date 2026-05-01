package t.lab.guide.dto.excursion.command

import t.lab.guide.dto.common.command.toCommand
import t.lab.guide.dto.excursion.CreateCustomExcursionRequest
import t.lab.guide.dto.excursion.ExcursionPointOrderItem
import t.lab.guide.dto.excursion.ExcursionSearchRequest
import t.lab.guide.dto.excursion.SetExcursionPointsRequest
import t.lab.guide.enums.ExcursionVisibility

fun CreateCustomExcursionRequest.toCommand(): CreateCustomExcursionCommand =
    CreateCustomExcursionCommand(
        title = requireNotNull(this.title) { "title not validated" },
        description = this.description,
        shortDescription = this.description,
        visibility = ExcursionVisibility.valueOf(requireNotNull(this.visibility) { "visibility not validated" }),
        points = requireNotNull(this.points) { "points not validated" },
    )

fun ExcursionSearchRequest.toCommand(): ExcursionSearchCommand =
    ExcursionSearchCommand(
        location = requireNotNull(this.location) { "location not validated" }.toCommand(),
        radiusKilometers = requireNotNull(this.radiusKilometers) { "radiusKilometers not validated" },
        categoryIds = this.categoryIds,
        visitTime = this.visitTime,
    )

fun ExcursionPointOrderItem.toCommand(): ExcursionPointOrderItemCommand =
    ExcursionPointOrderItemCommand(
        pointId = requireNotNull(this.pointId) { "pointId not validated" },
        order = requireNotNull(this.order) { "order not validated" },
    )

fun SetExcursionPointsRequest.toCommand(): SetExcursionPointsCommand =
    SetExcursionPointsCommand(
        points = requireNotNull(this.points) { "points not validated" }.map { it.toCommand() },
    )
