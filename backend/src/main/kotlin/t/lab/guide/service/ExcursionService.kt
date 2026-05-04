package t.lab.guide.service

import t.lab.guide.dto.admin.excursion.AdminExcursionDetailResponse
import t.lab.guide.dto.admin.excursion.AdminExcursionPageResponse
import t.lab.guide.dto.admin.excursion.AdminPatchPrebuiltExcursionRequest
import t.lab.guide.dto.admin.excursion.command.AdminCreatePrebuiltExcursionCommand
import t.lab.guide.dto.admin.excursion.command.AdminExcursionPageQueryCommand
import t.lab.guide.dto.excursion.CustomExcursionsPageQuery
import t.lab.guide.dto.excursion.ExcursionDetailResponse
import t.lab.guide.dto.excursion.ExcursionListResponse
import t.lab.guide.dto.excursion.ExcursionReviewListResponse
import t.lab.guide.dto.excursion.ExcursionReviewResponse
import t.lab.guide.dto.excursion.ExcursionShortItem
import t.lab.guide.dto.excursion.FavoriteExcursionsPageQuery
import t.lab.guide.dto.excursion.PatchExcursionReview
import t.lab.guide.dto.excursion.UpdateCustomExcursionRequest
import t.lab.guide.dto.excursion.UserCustomExcursionPageResponse
import t.lab.guide.dto.excursion.UserFavoriteExcursionPageResponse
import t.lab.guide.dto.excursion.command.CreateCustomExcursionCommand
import t.lab.guide.dto.excursion.command.ExcursionAddReviewCommand
import t.lab.guide.dto.excursion.command.ExcursionSearchCommand
import t.lab.guide.dto.excursion.command.SetExcursionPointsCommand

interface ExcursionService {
    fun searchExcursions(request: ExcursionSearchCommand): ExcursionListResponse

    fun getExcursionDetail(id: Long): ExcursionDetailResponse

    fun getUserCustomExcursions(query: CustomExcursionsPageQuery): UserCustomExcursionPageResponse

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

    fun getUserFavoriteExcursions(query: FavoriteExcursionsPageQuery): UserFavoriteExcursionPageResponse

    fun favoriteExcursion(excursionId: Long)

    fun unfavoriteExcursion(excursionId: Long)

    fun getAdminExcursionsPage(
        query: AdminExcursionPageQueryCommand,
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

    fun addReview(
        excursionId: Long,
        command: ExcursionAddReviewCommand,
    ): ExcursionReviewResponse

    fun patchOwnReview(
        excursionId: Long,
        request: PatchExcursionReview,
    ): ExcursionReviewResponse

    fun deleteOwnReview(excursionId: Long)

    fun getReviews(excursionId: Long): ExcursionReviewListResponse
}
