package t.lab.guide.service

import t.lab.guide.dto.admin.excursion.AdminCreatePrebuiltExcursionRequest
import t.lab.guide.dto.admin.excursion.AdminExcursionDetailResponse
import t.lab.guide.dto.admin.excursion.AdminExcursionPageResponse
import t.lab.guide.dto.admin.excursion.AdminPatchPrebuiltExcursionRequest
import t.lab.guide.dto.excursion.CreateCustomExcursionRequest
import t.lab.guide.dto.excursion.ExcursionDetailResponse
import t.lab.guide.dto.excursion.ExcursionListResponse
import t.lab.guide.dto.excursion.ExcursionSearchRequest
import t.lab.guide.dto.excursion.ExcursionShortItem
import t.lab.guide.dto.excursion.SetExcursionPointsRequest
import t.lab.guide.dto.excursion.UpdateCustomExcursionRequest

interface ExcursionService {
    fun searchExcursions(request: ExcursionSearchRequest): ExcursionListResponse

    fun getExcursionDetail(id: Long): ExcursionDetailResponse

    fun createCustomExcursion(request: CreateCustomExcursionRequest): ExcursionDetailResponse

    fun updateCustomExcursion(
        id: Long,
        request: UpdateCustomExcursionRequest,
    ): ExcursionShortItem

    fun setExcursionPoints(
        id: Long,
        request: SetExcursionPointsRequest,
    ): ExcursionDetailResponse

    fun deleteCustomExcursion(id: Long)

    fun favoriteExcursion(excursionId: Long)

    fun unfavoriteExcursion(excursionId: Long)

    fun getAdminExcursionsPage(
        page: Int,
        size: Int,
        sortBy: String?,
        sortDirection: String?,
        search: String?,
    ): AdminExcursionPageResponse

    fun getAdminExcursionDetail(id: Long): AdminExcursionDetailResponse

    fun createPrebuiltExcursion(request: AdminCreatePrebuiltExcursionRequest): AdminExcursionDetailResponse

    fun patchPrebuiltExcursion(
        id: Long,
        request: AdminPatchPrebuiltExcursionRequest,
    ): AdminExcursionDetailResponse

    fun setAdminExcursionPoints(
        id: Long,
        request: SetExcursionPointsRequest,
    ): AdminExcursionDetailResponse

    fun deleteExcursion(id: Long)
}
