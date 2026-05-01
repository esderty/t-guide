package t.lab.guide.dto.admin.excursion.command

import t.lab.guide.dto.admin.excursion.AdminCreatePrebuiltExcursionRequest
import t.lab.guide.dto.excursion.command.toCommand
import t.lab.guide.enums.ExcursionVisibility

fun AdminCreatePrebuiltExcursionRequest.toCommand(): AdminCreatePrebuiltExcursionCommand =
    AdminCreatePrebuiltExcursionCommand(
        title = requireNotNull(this.title) { "title not validated" },
        description = this.description,
        shortDescription = this.shortDescription,
        visibility = ExcursionVisibility.valueOf(requireNotNull(this.visibility) { "visibility not validated" }),
        points = requireNotNull(this.points) { "points not validated" }.map { it.toCommand() },
    )
