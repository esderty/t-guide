package t.lab.guide.service.real

import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service
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
import t.lab.guide.service.PointService

@Service
@Profile("!demo")
class RealPointService : PointService {
    override fun searchPoints(request: PointSearchCommand): PointListResponse {
        TODO("Not yet implemented")
    }

    override fun getPointDetail(id: Long): PointDetailResponse {
        TODO("Not yet implemented")
    }

    override fun getPointsPage(
        page: Int,
        size: Int,
        sortBy: AdminPointSortField?,
        sortDirection: SortDirection?,
        search: String?,
    ): AdminPointPageResponse {
        TODO("Not yet implemented")
    }

    override fun getAdminPointDetail(id: Long): AdminPointDetailResponse {
        TODO("Not yet implemented")
    }

    override fun createPoint(request: AdminCreatePointCommand): AdminPointDetailResponse {
        TODO("Not yet implemented")
    }

    override fun patchPoint(
        id: Long,
        request: AdminPatchPointRequest,
    ): AdminPointDetailResponse {
        TODO("Not yet implemented")
    }

    override fun deletePoint(id: Long) {
        TODO("Not yet implemented")
    }

    override fun uploadPointMedia(
        pointId: Long,
        file: MultipartFile,
        request: AdminUploadPointMediaCommand,
    ): AdminPointMediaItem {
        TODO("Not yet implemented")
    }

    override fun deletePointMedia(
        pointId: Long,
        mediaId: Long,
    ) {
        TODO("Not yet implemented")
    }
}
