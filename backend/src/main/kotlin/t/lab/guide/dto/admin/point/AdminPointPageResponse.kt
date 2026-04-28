package t.lab.guide.dto.admin.point

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Страница точек интереса для админ-панели с метаданными пагинации")
data class AdminPointPageResponse(
    @Schema(description = "Список точек интереса на текущей странице")
    val points: List<AdminPointShortItem>,
    @Schema(description = "Номер текущей страницы (с 0)", example = "0")
    val page: Int,
    @Schema(description = "Размер страницы (количество элементов)", example = "20")
    val size: Int,
    @Schema(description = "Общее количество точек", example = "247")
    val totalElements: Long,
    @Schema(description = "Общее количество страниц", example = "13")
    val totalPages: Int,
)
