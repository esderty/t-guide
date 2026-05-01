package t.lab.guide.service.real

import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service
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
import t.lab.guide.enums.AdminExcursionSortField
import t.lab.guide.enums.SortDirection
import t.lab.guide.service.ExcursionService

@Service
@Profile("!demo")
class RealExcursionService : ExcursionService {
    override fun searchExcursions(request: ExcursionSearchRequest): ExcursionListResponse {
        TODO("Not yet implemented")
    }

    override fun getExcursionDetail(id: Long): ExcursionDetailResponse {
        TODO("Not yet implemented")
    }

    override fun getUserCustomExcursions(): ExcursionListResponse {
        TODO("Not yet implemented")
    }

    override fun createCustomExcursion(request: CreateCustomExcursionRequest): ExcursionDetailResponse {
        TODO("Not yet implemented")
    }

    override fun updateCustomExcursion(
        id: Long,
        request: UpdateCustomExcursionRequest,
    ): ExcursionShortItem {
        TODO("Not yet implemented")
    }

    override fun setExcursionPoints(
        id: Long,
        request: SetExcursionPointsRequest,
    ): ExcursionDetailResponse {
        TODO("Not yet implemented")
    }

    override fun deleteCustomExcursion(id: Long) {
        TODO("Not yet implemented")
    }

    override fun getUserFavoriteExcursions(): ExcursionListResponse {
        TODO("Not yet implemented")
    }

    override fun favoriteExcursion(excursionId: Long) {
        TODO("Not yet implemented")
    }

    override fun unfavoriteExcursion(excursionId: Long) {
        TODO("Not yet implemented")
    }

    override fun getAdminExcursionsPage(
        page: Int,
        size: Int,
        sortBy: AdminExcursionSortField?,
        sortDirection: SortDirection?,
        search: String?,
    ): AdminExcursionPageResponse {
        TODO("Not yet implemented")
    }

    override fun getAdminExcursionDetail(id: Long): AdminExcursionDetailResponse {
        TODO("Not yet implemented")
    }

    override fun createPrebuiltExcursion(request: AdminCreatePrebuiltExcursionRequest): AdminExcursionDetailResponse {
        TODO("Not yet implemented")
    }

    override fun patchPrebuiltExcursion(
        id: Long,
        request: AdminPatchPrebuiltExcursionRequest,
    ): AdminExcursionDetailResponse {
        TODO("Not yet implemented")
    }

    override fun setAdminExcursionPoints(
        id: Long,
        request: SetExcursionPointsRequest,
    ): AdminExcursionDetailResponse {
        TODO("Not yet implemented")
    }

    override fun deleteExcursion(id: Long) {
        TODO("Not yet implemented")
    }
}
