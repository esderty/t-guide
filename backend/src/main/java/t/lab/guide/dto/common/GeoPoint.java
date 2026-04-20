package t.lab.guide.dto.common;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

@Schema(description = "Географические координаты (широта/долгота)")
public record GeoPoint(
        @Schema(description = "Широта",
                requiredMode = Schema.RequiredMode.REQUIRED,
                minimum = "-90.0",
                maximum = "90.0",
                example = "55.753544")
        @NotNull(message = "поле обязательно!")
        @DecimalMin(value = "-90.0", message = "минимальное значение -90.0")
        @DecimalMax(value = "90.0", message = "максимальное значение 90.0")
        BigDecimal latitude,

        @Schema(description = "Долгота",
                requiredMode = Schema.RequiredMode.REQUIRED,
                minimum = "-180.0",
                maximum = "180.0",
                example = "37.621202")
        @NotNull(message = "поле обязательно!")
        @DecimalMin(value = "-180.0", message = "минимальное значение -180.0")
        @DecimalMax(value = "180.0", message = "максимальное значение 180.0")
        BigDecimal longitude
) {}