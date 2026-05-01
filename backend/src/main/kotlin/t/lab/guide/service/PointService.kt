package t.lab.guide.service

import org.springframework.web.multipart.MultipartFile
import t.lab.guide.dto.admin.point.AdminPatchPointRequest
import t.lab.guide.dto.admin.point.AdminPointDetailResponse
import t.lab.guide.dto.admin.point.AdminPointMediaItem
import t.lab.guide.dto.admin.point.AdminPointPageResponse
import t.lab.guide.dto.admin.point.command.AdminCreatePointCommand
import t.lab.guide.dto.admin.point.command.AdminUploadPointMediaCommand
import t.lab.guide.dto.point.PointDetailResponse
import t.lab.guide.dto.point.PointListResponse
import t.lab.guide.dto.point.command.PointSearchCommand
import t.lab.guide.enums.AdminPointSortField
import t.lab.guide.enums.SortDirection

interface PointService {
    fun searchPoints(request: PointSearchCommand): PointListResponse

    fun getPointDetail(id: Long): PointDetailResponse

    fun getPointsPage(
        page: Int,
        size: Int,
        sortBy: AdminPointSortField?,
        sortDirection: SortDirection?,
        search: String?,
    ): AdminPointPageResponse

    fun getAdminPointDetail(id: Long): AdminPointDetailResponse

    fun createPoint(request: AdminCreatePointCommand): AdminPointDetailResponse

    fun patchPoint(
        id: Long,
        request: AdminPatchPointRequest,
    ): AdminPointDetailResponse

    fun deletePoint(id: Long)

    fun uploadPointMedia(
        pointId: Long,
        file: MultipartFile,
        request: AdminUploadPointMediaCommand,
    ): AdminPointMediaItem

    fun deletePointMedia(
        pointId: Long,
        mediaId: Long,
    )
// TODO когда будут entity(model), добавить сервисные методы
}
