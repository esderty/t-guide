package t.lab.guide.dto.admin.user

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Страница пользователей для админ-панели с метаданными пагинации")
data class AdminUserPageResponse(
    @Schema(description = "Список пользователей на текущей странице")
    val users: List<AdminUserShortItem>,
    @Schema(description = "Номер текущей страницы (с 0)", example = "0")
    val page: Int,
    @Schema(description = "Размер страницы (количество элементов)", example = "20")
    val size: Int,
    @Schema(description = "Общее количество пользователей", example = "531")
    val totalElements: Long,
    @Schema(description = "Общее количество страниц", example = "27")
    val totalPages: Int,
)
