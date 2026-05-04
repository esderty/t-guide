package t.lab.guide.dto.excursion

import io.swagger.v3.oas.annotations.media.ArraySchema
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Страница кастомных экскурсий пользователя с метаданными пагинации")
data class UserCustomExcursionPageResponse(
    @ArraySchema(
        arraySchema = Schema(description = "Список экскурсий"),
        schema = Schema(implementation = ExcursionShortItem::class),
    )
    val excursions: List<ExcursionShortItem>,
    @Schema(description = "Номер текущей страницы (с 0)", example = "0")
    val page: Int,
    @Schema(description = "Размер страницы (количество элементов)", example = "20")
    val size: Int,
    @Schema(description = "Общее количество точек", example = "247")
    val totalElements: Long,
    @Schema(description = "Общее количество страниц", example = "13")
    val totalPages: Int,
)
