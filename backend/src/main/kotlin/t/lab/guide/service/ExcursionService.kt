package t.lab.guide.service

import t.lab.guide.dto.admin.excursion.AdminExcursionDetailResponse
import t.lab.guide.dto.admin.excursion.AdminExcursionPageResponse
import t.lab.guide.dto.admin.excursion.AdminPatchPrebuiltExcursionRequest
import t.lab.guide.dto.admin.excursion.command.AdminCreatePrebuiltExcursionCommand
import t.lab.guide.dto.excursion.ExcursionDetailResponse
import t.lab.guide.dto.excursion.ExcursionListResponse
import t.lab.guide.dto.excursion.ExcursionShortItem
import t.lab.guide.dto.excursion.UpdateCustomExcursionRequest
import t.lab.guide.dto.excursion.command.CreateCustomExcursionCommand
import t.lab.guide.dto.excursion.command.ExcursionSearchCommand
import t.lab.guide.dto.excursion.command.SetExcursionPointsCommand
import t.lab.guide.enums.AdminExcursionSortField
import t.lab.guide.enums.SortDirection

interface ExcursionService {
    fun searchExcursions(request: ExcursionSearchCommand): ExcursionListResponse

    fun getExcursionDetail(id: Long): ExcursionDetailResponse

    fun getUserCustomExcursions(): ExcursionListResponse

    fun createCustomExcursion(request: CreateCustomExcursionCommand): ExcursionDetailResponse

    fun updateCustomExcursion(
        id: Long,
        request: UpdateCustomExcursionRequest,
    ): ExcursionShortItem

    fun setExcursionPoints(
        id: Long,
        request: SetExcursionPointsCommand,
    ): ExcursionDetailResponse

    fun deleteCustomExcursion(id: Long)

    fun getUserFavoriteExcursions(): ExcursionListResponse

    fun favoriteExcursion(excursionId: Long)

    fun unfavoriteExcursion(excursionId: Long)

    fun getAdminExcursionsPage(
        page: Int,
        size: Int,
        sortBy: AdminExcursionSortField?,
        sortDirection: SortDirection?,
        search: String?,
    ): AdminExcursionPageResponse

    fun getAdminExcursionDetail(id: Long): AdminExcursionDetailResponse

    fun createPrebuiltExcursion(request: AdminCreatePrebuiltExcursionCommand): AdminExcursionDetailResponse

    fun patchPrebuiltExcursion(
        id: Long,
        request: AdminPatchPrebuiltExcursionRequest,
    ): AdminExcursionDetailResponse

    fun setAdminExcursionPoints(
        id: Long,
        request: SetExcursionPointsCommand,
    ): AdminExcursionDetailResponse

    fun deleteExcursion(id: Long)
}
