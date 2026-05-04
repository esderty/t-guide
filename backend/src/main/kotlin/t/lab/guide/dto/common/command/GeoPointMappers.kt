package t.lab.guide.dto.common.command

import t.lab.guide.dto.common.GeoPoint
import t.lab.guide.exception.BadRequestException

fun GeoPoint.toCommand(): GeoPointCommand =
    GeoPointCommand(
        latitude = this.latitude ?: throw BadRequestException("Поле 'latitude' обязательно"),
        longitude = this.longitude ?: throw BadRequestException("Поле 'longitude' обязательно"),
    )

fun GeoPointCommand.toDto(): GeoPoint =
    GeoPoint(
        latitude = this.latitude,
        longitude = this.longitude,
    )
