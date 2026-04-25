package t.lab.guide.dto.point.category

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Краткая информация о категории, используется в списках категорий")
data class CategoryItem(
    @Schema(description = "Уникальный идентификатор категории", example = "1")
    val id: Long,
    @Schema(description = "Отображаемое имя категории", example = "Ресторан")
    val name: String,
    @Schema(
        description = "Уникальный человекочитаемый идентификатор категории, используемый в URL",
        example = "restaurant",
    )
    val slug: String,
)
