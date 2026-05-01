package t.lab.guide.dto.admin.point.command

import t.lab.guide.dto.common.command.GeoPointCommand

data class AdminCreatePointCommand(
    val title: String,
    val description: String? = null,
    val shortDescription: String? = null,
    val categoryId: Long,
    val address: String? = null,
    val coordinates: GeoPointCommand,
    val visitTime: Int? = null,
    val workingHours: String? = null,
    val isActive: Boolean,
)
