package t.lab.guide.dto.admin.excursion

import io.swagger.v3.oas.annotations.media.Schema
import t.lab.guide.dto.excursion.ExcursionShortItem

@Schema(description = "Страница экскурсий для админ-панели с метаданными пагинации")
data class AdminExcursionPageResponse(
    @Schema(description = "Список экскурсий на текущей странице")
    val excursions: List<ExcursionShortItem>,
    @Schema(description = "Номер текущей страницы (с 0)", example = "0")
    val page: Int,
    @Schema(description = "Размер страницы (количество элементов)", example = "20")
    val size: Int,
    @Schema(description = "Общее количество экскурсий", example = "84")
    val totalElements: Long,
    @Schema(description = "Общее количество страниц", example = "5")
    val totalPages: Int,
)
