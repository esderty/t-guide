package t.lab.guide.dto.admin.point;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import t.lab.guide.dto.common.GeoPoint;

public record AdminCreatePointRequest(
        @Schema(description = "Название точки интереса",
                requiredMode = Schema.RequiredMode.REQUIRED,
                maxLength = 255,
                example = "Красная площадь")
        @NotBlank
        @Size(max = 255)
        String title,

        @Schema(description = "Подробное описание точки интереса",
                requiredMode = Schema.RequiredMode.REQUIRED,
                maxLength = 5000,
                example = "Красная площадь — главная площадь Москвы.")
        @NotBlank
        @Size(max = 5000)
        String description,

        @Schema(description = "Идентификатор категории, к которой относится точка",
                requiredMode = Schema.RequiredMode.REQUIRED,
                example = "1")
        @NotNull
        Long categoryId,

        @Schema(description = "Адрес точки интереса",
                requiredMode = Schema.RequiredMode.REQUIRED,
                maxLength = 500,
                example = "Москва, Красная площадь, 1")
        @NotBlank
        @Size(max = 500)
        String address,

        @Schema(description = "Координаты точки",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull
        @Valid
        GeoPoint coordinates,

        @Schema(description = "Среднее время посещения точки интереса в минутах",
                requiredMode = Schema.RequiredMode.REQUIRED,
                example = "60")
        @NotNull
        @Min(1)
        Integer visitTime,

        @Schema(description = "Часы работы точки",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED,
                maxLength = 255,
                example = "10:00 - 18:00")
        @Size(max = 255)
        String workingHours,

        @Schema(description = "Активна ли точка (показывается пользователям). По умолчанию true",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED,
                example = "true")
        Boolean isActive
) {}