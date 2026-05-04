package t.lab.guide.dto.admin.user

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.Size
import t.lab.guide.enums.AdminUserSortField
import t.lab.guide.enums.SortDirection
import t.lab.guide.validation.ValidEnum

@Schema(description = "Параметры выборки страницы пользователей для админ-панели")
data class AdminUserPageQuery(
    @field:Min(0)
    @Schema(description = "Номер страницы (с 0)", example = "0", defaultValue = "0")
    val page: Int = 0,
    @field:Min(1)
    @field:Max(100)
    @Schema(description = "Размер страницы (1..100)", example = "25", defaultValue = "25")
    val size: Int = 25,
    @Schema(description = "Поле сортировки", example = "CREATED_AT")
    @ValidEnum(AdminUserSortField::class)
    val sortBy: String? = null,
    @Schema(description = "Направление сортировки", example = "DESC")
    @ValidEnum(SortDirection::class)
    val sortDirection: String? = null,
    @field:Size(max = 100)
    @Schema(description = "Строка поиска по username/email/name", example = "ivan")
    val search: String? = null,
)
