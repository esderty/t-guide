package t.lab.guide.dto.admin.excursion.command

import t.lab.guide.dto.excursion.command.ExcursionPointOrderItemCommand
import t.lab.guide.enums.ExcursionVisibility

data class AdminCreatePrebuiltExcursionCommand(
    val title: String,
    val description: String? = null,
    val shortDescription: String? = null,
    val visibility: ExcursionVisibility,
    val points: List<ExcursionPointOrderItemCommand>,
)
