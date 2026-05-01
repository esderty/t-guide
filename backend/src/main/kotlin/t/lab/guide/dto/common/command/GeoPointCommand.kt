package t.lab.guide.dto.common.command

import java.math.BigDecimal

data class GeoPointCommand(
    val latitude: BigDecimal,
    val longitude: BigDecimal,
)
