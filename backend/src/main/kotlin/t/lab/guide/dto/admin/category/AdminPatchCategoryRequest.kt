package t.lab.guide.dto.admin.category

import com.fasterxml.jackson.annotation.JsonIgnore
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.AssertTrue
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

data class AdminPatchCategoryRequest(
    @Schema(
        description = "Новое отображаемое имя категории",
        requiredMode = Schema.RequiredMode.NOT_REQUIRED,
        maxLength = 100,
        example = "Ресторан",
    )
    @field:Size(max = 100)
    val name: String? = null,
    @Schema(
        description = "Новый человекочитаемый идентификатор категории для URL",
        requiredMode = Schema.RequiredMode.NOT_REQUIRED,
        maxLength = 100,
        example = "restaurant",
    )
    @field:Size(max = 100)
    @field:Pattern(
        regexp = "^[a-z0-9]+(?:-[a-z0-9]+)*$",
        message = "slug должен состоять из строчных латинских букв, цифр и дефисов",
    )
    val slug: String? = null,
) {
    @AssertTrue(message = "Необходимо передать хотя бы одно поле для обновления")
    @JsonIgnore
    @Schema(hidden = true)
    fun isAnyFieldPresent(): Boolean = name != null || slug != null
}
