package t.lab.guide.dto.excursion.command

import t.lab.guide.dto.common.command.GeoPointCommand

data class ExcursionSearchCommand(
    val location: GeoPointCommand,
    val radiusKilometers: Int,
    val categoryIds: List<Long> = emptyList(),
    val visitTime: Int? = null,
)
