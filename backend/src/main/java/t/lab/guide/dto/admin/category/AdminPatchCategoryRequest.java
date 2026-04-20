package t.lab.guide.dto.admin.category;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record AdminPatchCategoryRequest(
        @Schema(description = "Новое отображаемое имя категории",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED,
                maxLength = 100,
                example = "Ресторан")
        @Size(max = 100)
        String name,

        @Schema(description = "Новый человекочитаемый идентификатор категории для URL",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED,
                maxLength = 100,
                example = "restaurant")
        @Size(max = 100)
        @Pattern(regexp = "^[a-z0-9]+(?:-[a-z0-9]+)*$",
                message = "slug должен состоять из строчных латинских букв, цифр и дефисов")
        String slug
) {
    @AssertTrue(message = "Необходимо передать хотя бы одно поле для обновления")
    @Schema(hidden = true)
    public boolean isAnyFieldPresent() {
        return name != null || slug != null;
    }
}