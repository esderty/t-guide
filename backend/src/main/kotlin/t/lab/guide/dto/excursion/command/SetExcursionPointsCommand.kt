package t.lab.guide.dto.excursion.command

data class SetExcursionPointsCommand(
    val points: List<ExcursionPointOrderItemCommand>,
)
