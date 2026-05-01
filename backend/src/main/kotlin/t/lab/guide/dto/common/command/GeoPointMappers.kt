package t.lab.guide.dto.common.command

import t.lab.guide.dto.common.GeoPoint

fun GeoPoint.toCommand(): GeoPointCommand =
    GeoPointCommand(
        latitude = requireNotNull(this.latitude) { "latitude not validated" },
        longitude = requireNotNull(this.longitude) { "longitude not validated" },
    )

fun GeoPointCommand.toDto(): GeoPoint =
    GeoPoint(
        latitude = this.latitude,
        longitude = this.longitude,
    )
