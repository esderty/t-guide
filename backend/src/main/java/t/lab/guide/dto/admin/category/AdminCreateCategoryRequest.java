package t.lab.guide.dto.admin.category;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record AdminCreateCategoryRequest(
        @Schema(description = "Отображаемое имя категории",
                requiredMode = Schema.RequiredMode.REQUIRED,
                maxLength = 100,
                example = "Ресторан")
        @NotBlank
        @Size(max = 100)
        String name,

        @Schema(description = "Уникальный человекочитаемый идентификатор категории для URL",
                requiredMode = Schema.RequiredMode.REQUIRED,
                maxLength = 100,
                example = "restaurant")
        @NotBlank
        @Size(max = 100)
        @Pattern(regexp = "^[a-z0-9]+(?:-[a-z0-9]+)*$",
                message = "slug должен состоять из строчных латинских букв, цифр и дефисов")
        String slug
) {}