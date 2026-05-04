package t.lab.guide.dto.point.command

import t.lab.guide.dto.common.command.GeoPointCommand

data class PointSearchCommand(
    val location: GeoPointCommand,
    val radiusKilometers: Int,
    val categoryIds: List<Long> = emptyList(),
    val visitTime: Int? = null,
)
