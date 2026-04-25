package t.lab.guide.dto.admin.category

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

data class AdminCreateCategoryRequest(
    @Schema(
        description = "Отображаемое имя категории",
        requiredMode = Schema.RequiredMode.REQUIRED,
        maxLength = 100,
        example = "Ресторан",
    )
    @field:NotBlank
    @field:Size(max = 100)
    val name: String,
    @Schema(
        description = "Уникальный человекочитаемый идентификатор категории для URL",
        requiredMode = Schema.RequiredMode.REQUIRED,
        maxLength = 100,
        example = "restaurant",
    )
    @field:NotBlank
    @field:Size(max = 100)
    @field:Pattern(
        regexp = "^[a-z0-9]+(?:-[a-z0-9]+)*$",
        message = "slug должен состоять из строчных латинских букв, цифр и дефисов",
    )
    val slug: String,
)
