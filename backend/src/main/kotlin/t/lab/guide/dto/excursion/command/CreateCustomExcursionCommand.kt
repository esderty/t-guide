package t.lab.guide.dto.excursion.command

import t.lab.guide.enums.ExcursionVisibility

data class CreateCustomExcursionCommand(
    val title: String,
    val description: String? = null,
    val shortDescription: String? = null,
    val visibility: ExcursionVisibility,
    val points: List<ExcursionPointOrderItemCommand>,
)
