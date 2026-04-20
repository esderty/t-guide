package t.lab.guide.dto.admin.point;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import t.lab.guide.dto.common.GeoPoint;

public record AdminPatchPointRequest(
        @Schema(description = "Новое название точки интереса",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED,
                maxLength = 255,
                example = "Красная площадь")
        @Size(max = 255)
        String title,

        @Schema(description = "Новое подробное описание точки интереса",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED,
                maxLength = 5000,
                example = "Красная площадь — главная площадь Москвы.")
        @Size(max = 5000)
        String description,

        @Schema(description = "Новый идентификатор категории",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED,
                example = "2")
        Long categoryId,

        @Schema(description = "Новый адрес точки интереса",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED,
                maxLength = 500,
                example = "Москва, Красная площадь, 1")
        @Size(max = 500)
        String address,

        @Schema(description = "Новые координаты точки",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        @Valid
        GeoPoint coordinates,

        @Schema(description = "Новое среднее время посещения точки в минутах",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED,
                example = "90")
        @Min(1)
        Integer visitTime,

        @Schema(description = "Новые часы работы точки",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED,
                maxLength = 255,
                example = "10:00 - 22:00")
        @Size(max = 255)
        String workingHours,

        @Schema(description = "Активна ли точка (показывается пользователям)",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED,
                example = "true")
        Boolean isActive
) {
    @AssertTrue(message = "Необходимо передать хотя бы одно поле для обновления")
    @Schema(hidden = true)
    public boolean isAnyFieldPresent() {
        return title != null
                || description != null
                || categoryId != null
                || address != null
                || coordinates != null
                || visitTime != null
                || workingHours != null
                || isActive != null;
    }
}