package t.lab.guide.dto.admin.excursion;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Size;
import t.lab.guide.entity.enums.ExcursionVisibility;

@Schema(
        description = "Запрос на частичное обновление готовой (PREBUILT) экскурсии администратором. " +
                "Передаются только изменяемые поля; минимум одно поле обязательно."
)
public record AdminPatchPrebuiltExcursionRequest(
        @Schema(
                description = "Новое название экскурсии",
                example = "Обновлённый маршрут по центру",
                minLength = 1,
                maxLength = 255,
                nullable = true
        )
        @Size(min = 1, max = 255, message = "максимальная длина названия - 255 символов!")
        String title,

        @Schema(
                description = "Новое описание экскурсии",
                example = "Обновлённое описание маршрута",
                maxLength = 2000,
                nullable = true
        )
        @Size(max = 2000, message = "максимальная длина описания - 2000 символов!")
        String description,

        @Schema(description = "Новая видимость экскурсии (PUBLIC, PRIVATE)", example = "PUBLIC", nullable = true)
        ExcursionVisibility visibility
) {
        @AssertTrue(message = "Необходимо передать хотя бы одно поле для обновления")
        @Schema(hidden = true)
        public boolean isAnyFieldPresent() {
                return title != null || description != null || visibility != null;
        }
}