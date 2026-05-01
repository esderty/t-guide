package t.lab.guide.dto.point.category

import io.swagger.v3.oas.annotations.media.ArraySchema
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Ответ с списком категорий")
data class CategoryListResponse(
    @ArraySchema(
        arraySchema = Schema(description = "Список категорий"),
        schema = Schema(implementation = CategoryItem::class),
    )
    val categories: List<CategoryItem>,
)
