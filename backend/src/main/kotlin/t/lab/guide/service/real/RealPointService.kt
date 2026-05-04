package t.lab.guide.service.real

import org.springframework.context.annotation.Profile
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.data.jdbc.core.mapping.AggregateReference
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import t.lab.guide.domain.Point
import t.lab.guide.domain.PointMedia
import t.lab.guide.dto.admin.point.AdminPatchPointRequest
import t.lab.guide.dto.admin.point.AdminPointDetailResponse
import t.lab.guide.dto.admin.point.AdminPointMediaItem
import t.lab.guide.dto.admin.point.AdminPointPageResponse
import t.lab.guide.dto.admin.point.command.AdminCreatePointCommand
import t.lab.guide.dto.admin.point.command.AdminPointPageQueryCommand
import t.lab.guide.dto.admin.point.command.AdminUploadPointMediaCommand
import t.lab.guide.dto.point.PointDetailResponse
import t.lab.guide.dto.point.PointListResponse
import t.lab.guide.dto.point.command.PointSearchCommand
import t.lab.guide.enums.SortDirection
import t.lab.guide.exception.BadRequestException
import t.lab.guide.exception.NotFoundException
import t.lab.guide.mapper.toAdminPointDetailResponse
import t.lab.guide.mapper.toAdminPointDetailResponseWithoutMedia
import t.lab.guide.mapper.toAdminPointMediaItem
import t.lab.guide.mapper.toAdminPointShortItem
import t.lab.guide.mapper.toPoint
import t.lab.guide.mapper.toPointDetailResponse
import t.lab.guide.mapper.toPointMediaItem
import t.lab.guide.mapper.toPointShortItem
import t.lab.guide.repository.PointMediaRepository
import t.lab.guide.repository.PointRepository
import t.lab.guide.service.PointService
import t.lab.guide.service.S3Service

@Service
@Profile("!demo")
class RealPointService(
    private val pointRepository: PointRepository,
    private val pointMediaRepository: PointMediaRepository,
    private val pointCategoryService: RealPointCategoryService,
    private val s3Service: S3Service,
) : PointService {
    override fun searchPoints(request: PointSearchCommand): PointListResponse {
        if (request.categoryIds.isNotEmpty()) {
            pointCategoryService.checkRequestCategory(request.categoryIds.toSet())
        }

        val points =
            pointRepository.findPointByLocation(
                request.location.latitude.toDouble(),
                request.location.longitude.toDouble(),
                request.radiusKilometers * 1000,
                if (request.categoryIds.isEmpty()) null else request.categoryIds.toTypedArray(),
                request.visitTime,
            )

        return PointListResponse(points.map { it.toPointShortItem() })
    }

    override fun getPointDetail(id: Long): PointDetailResponse {
        val point =
            pointRepository.findDetailById(id)
                ?: throw NotFoundException("Точка с id=$id не найдена")

        val pointMedia = pointMediaRepository.findMediaByPointId(point.id)
        val s3Domain = s3Service.getS3Domain()
        val mediaDto = pointMedia.map { it.toPointMediaItem(s3Domain) }

        return point.toPointDetailResponse(mediaDto)
    }

    override fun getPointsPage(
        query: AdminPointPageQueryCommand,
    ): AdminPointPageResponse {
        val safeSearch = query.search?.trim()?.takeIf { it.isNotEmpty() }

        val direction =
            when (query.sortDirection) {
                SortDirection.ASC -> Sort.Direction.ASC
                SortDirection.DESC -> Sort.Direction.DESC
            }
        val pageable = PageRequest.of(query.page, query.size, Sort.by(direction, query.sortBy.value))

        val points = pointRepository.findPointsPage(safeSearch, pageable)
        val total = pointRepository.countPointsInPage(safeSearch)
        val totalPages = ((total + query.size - 1) / query.size).toInt()

        return AdminPointPageResponse(
            points = points.map { it.toAdminPointShortItem() },
            page = query.page,
            size = query.size,
            totalPages = totalPages,
            totalElements = total,
        )
    }

    override fun getAdminPointDetail(id: Long): AdminPointDetailResponse {
        val point =
            pointRepository.findAdminDetailById(id)
                ?: throw NotFoundException("Точка с id=$id не найдена")

        val pointMedia = pointMediaRepository.findMediaByPointId(point.id)
        val s3Domain = s3Service.getS3Domain()
        val mediaDto = pointMedia.map { it.toAdminPointMediaItem(s3Domain) }

        return point.toAdminPointDetailResponse(mediaDto)
    }

    override fun createPoint(request: AdminCreatePointCommand): AdminPointDetailResponse {
        val category = pointCategoryService.getCategoryById(request.categoryId)

        val newPoint = request.toPoint()
        val savedPoint = pointRepository.save(newPoint)

        return savedPoint.toAdminPointDetailResponseWithoutMedia(emptyList(), category.name)
    }

    @Transactional
    override fun patchPoint(
        id: Long,
        request: AdminPatchPointRequest,
    ): AdminPointDetailResponse {
        val point =
            pointRepository
                .findById(id)
                .orElseThrow { NotFoundException("Точка с id=$id не найдена") }

        val newCategory =
            request.categoryId
                ?.takeIf { it != point.category?.id }
                ?.let { categoryId ->
                    if (!pointCategoryService.existsById(categoryId)) {
                        throw NotFoundException("Категория с id=$categoryId не найдена")
                    }
                    AggregateReference.to(categoryId)
                }
                ?: point.category

        val updated =
            point.copy(
                title = request.title ?: point.title,
                description = request.description ?: point.description,
                shortDescription = request.shortDescription ?: point.shortDescription,
                category = newCategory,
                address = request.address ?: point.address,
                latitude = request.coordinates?.latitude?.toDouble() ?: point.latitude,
                longitude = request.coordinates?.longitude?.toDouble() ?: point.longitude,
                visitTimeMin = request.visitTime ?: point.visitTimeMin,
                workingHours = request.workingHours ?: point.workingHours,
                isActive = request.isActive ?: point.isActive,
            )
        pointRepository.save(updated)

        val view =
            pointRepository.findAdminDetailById(id)
                ?: throw NotFoundException("Точка с id=$id не найдена")
        val s3Domain = s3Service.getS3Domain()
        val media =
            pointMediaRepository
                .findMediaByPointId(id)
                .map { it.toAdminPointMediaItem(s3Domain) }
        return view.toAdminPointDetailResponse(media)
    }

    @Transactional
    override fun deletePoint(id: Long) {
        val point =
            pointRepository
                .findById(id)
                .orElseThrow { NotFoundException("Точка с id=$id не найдена") }

        val media = pointMediaRepository.findMediaByPointId(id)
        pointMediaRepository.deleteAll(media)
        pointRepository.delete(point)

        media.forEach { runCatching { s3Service.deleteObject(it.objectKey) } }
    }

    @Transactional
    override fun uploadPointMedia(
        pointId: Long,
        file: MultipartFile,
        request: AdminUploadPointMediaCommand,
    ): AdminPointMediaItem {
        if (!pointRepository.existsById(pointId)) {
            throw NotFoundException("Точка с id=$pointId не найдена")
        }

        val key = s3Service.uploadObject(file, pointId, request.type)

        val sortOrder =
            request.sortOrder
                ?: pointMediaRepository.findMediaByPointId(pointId).size

        val saved =
            try {
                pointMediaRepository.save(
                    PointMedia(
                        point = AggregateReference.to(pointId),
                        objectKey = key,
                        mediaType = request.type,
                        sortOrder = sortOrder,
                    ),
                )
            } catch (e: Throwable) {
                runCatching { s3Service.deleteObject(key) }
                throw e
            }

        return saved.toAdminPointMediaItem(s3Service.getS3Domain())
    }

    @Transactional
    override fun deletePointMedia(
        pointId: Long,
        mediaId: Long,
    ) {
        val media =
            pointMediaRepository
                .findById(mediaId)
                .orElseThrow { NotFoundException("Медиа с id=$mediaId не найдено") }

        if (media.point?.id != pointId) {
            throw NotFoundException("Медиа с id=$mediaId не принадлежит точке id=$pointId")
        }

        pointMediaRepository.delete(media)
        runCatching { s3Service.deleteObject(media.objectKey) }
    }

    fun getActivePointsByIds(ids: List<Long>): List<Point> {
        if (ids.isEmpty()) return emptyList()
        val pointsById = pointRepository.findAllById(ids).associateBy { it.id }
        val missing = ids.filterNot { it in pointsById }
        if (missing.isNotEmpty()) {
            throw BadRequestException("Точки не найдены: ${missing.joinToString()}")
        }
        val ordered = ids.map { pointsById.getValue(it) }
        val inactive = ordered.filterNot { it.isActive }.mapNotNull { it.id }
        if (inactive.isNotEmpty()) {
            throw BadRequestException("Точки неактивны: ${inactive.joinToString()}")
        }
        return ordered
    }

    fun computeRouteDistanceMeters(orderedIds: List<Long>): Int {
        if (orderedIds.size < 2) return 0
        return pointRepository.sumRouteDistanceMeters(orderedIds.toTypedArray())
    }
}
