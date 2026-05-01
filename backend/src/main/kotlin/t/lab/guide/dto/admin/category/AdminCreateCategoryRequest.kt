package t.lab.guide.dto.admin.category

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

data class AdminCreateCategoryRequest(
    @Schema(
        description = "Отображаемое имя категории",
        requiredMode = Schema.RequiredMode.REQUIRED,
        maxLength = 50,
        example = "Ресторан",
        nullable = false,
    )
    @field:NotBlank(message = "Название категории обязательно")
    @field:Size(max = 50, message = "Название категории не может превышать 50 символов")
    val name: String? = null,
    @Schema(
        description = "Уникальный человекочитаемый идентификатор категории для URL",
        requiredMode = Schema.RequiredMode.REQUIRED,
        maxLength = 50,
        example = "restaurant",
        nullable = false,
    )
    @field:NotBlank(message = "Slug категории обязателен")
    @field:Size(max = 50, message = "Slug категории не может превышать 50 символов")
    @field:Pattern(
        regexp = "^[a-z0-9]+(?:-[a-z0-9]+)*$",
        message = "slug должен состоять из строчных латинских букв, цифр и дефисов",
    )
    val slug: String? = null,
)
