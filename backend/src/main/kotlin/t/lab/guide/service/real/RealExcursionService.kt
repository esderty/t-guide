package t.lab.guide.service.real

import org.springframework.context.annotation.Profile
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.data.jdbc.core.mapping.AggregateReference
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import t.lab.guide.domain.Excursion
import t.lab.guide.domain.ExcursionPoint
import t.lab.guide.domain.ExcursionReview
import t.lab.guide.domain.FavoriteExcursion
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
import t.lab.guide.dto.excursion.command.ExcursionPointOrderItemCommand
import t.lab.guide.dto.excursion.command.ExcursionSearchCommand
import t.lab.guide.dto.excursion.command.SetExcursionPointsCommand
import t.lab.guide.enums.ExcursionRouteType
import t.lab.guide.enums.ExcursionVisibility
import t.lab.guide.enums.SortDirection
import t.lab.guide.exception.BadRequestException
import t.lab.guide.exception.ConflictException
import t.lab.guide.exception.NotFoundException
import t.lab.guide.mapper.toAdminExcursionDetailResponse
import t.lab.guide.mapper.toExcursion
import t.lab.guide.mapper.toExcursionDetailResponse
import t.lab.guide.mapper.toExcursionListResponse
import t.lab.guide.mapper.toExcursionReviewResponse
import t.lab.guide.mapper.toExcursionShortItem
import t.lab.guide.mapper.toExcursionShortItemDetail
import t.lab.guide.repository.ExcursionPointRepository
import t.lab.guide.repository.ExcursionRepository
import t.lab.guide.repository.ExcursionReviewRepository
import t.lab.guide.repository.FavoriteExcursionRepository
import t.lab.guide.service.ExcursionService
import t.lab.guide.service.SecurityService

@Service
@Profile("!demo")
class RealExcursionService(
    private val excursionRepository: ExcursionRepository,
    private val excursionPointRepository: ExcursionPointRepository,
    private val favoriteExcursionRepository: FavoriteExcursionRepository,
    private val excursionReviewRepository: ExcursionReviewRepository,
    private val pointService: RealPointService,
    private val pointCategoryService: RealPointCategoryService,
    private val securityService: SecurityService,
) : ExcursionService {
    override fun searchExcursions(request: ExcursionSearchCommand): ExcursionListResponse {
        if (request.categoryIds.isNotEmpty()) {
            pointCategoryService.checkRequestCategory(request.categoryIds.toSet())
        }

        val excursions =
            excursionRepository
                .findExcursionsByLocation(
                    request.location.latitude.toDouble(),
                    request.location.longitude.toDouble(),
                    request.radiusKilometers * 1000,
                    if (request.categoryIds.isEmpty()) null else request.categoryIds.toTypedArray(),
                    request.visitTime,
                ).map { it.toExcursionShortItem(null) }

        return excursions.toExcursionListResponse()
    }

    @Transactional(readOnly = true)
    override fun getExcursionDetail(id: Long): ExcursionDetailResponse {
        val view =
            excursionRepository.findShortViewById(id)
                ?: throw NotFoundException("Экскурсия с id=$id не найдена")

        val currentUserId: Long? = runCatching { securityService.getCurrentUserId() }.getOrNull()
        val isOwner = currentUserId != null && view.ownerId == currentUserId

        val accessible =
            when (view.routeType) {
                ExcursionRouteType.PREBUILT -> view.visibility == ExcursionVisibility.PUBLIC
                ExcursionRouteType.CUSTOM -> view.visibility == ExcursionVisibility.PUBLIC || isOwner
            }
        if (!accessible) {
            throw NotFoundException("Экскурсия с id=$id не найдена")
        }

        val points = excursionPointRepository.findOrderedPointsByExcursionId(id)
        if (points.isEmpty()) {
            throw NotFoundException("У экскурсии id=$id нет точек маршрута")
        }

        return view.toExcursionDetailResponse(currentUserId, points)
    }

    override fun getUserCustomExcursions(query: CustomExcursionsPageQuery): UserCustomExcursionPageResponse {
        val currentUserId = securityService.getCurrentUserId()
        val pageable = PageRequest.of(query.page, query.size)

        val excursions = excursionRepository.findCustomExcursionsByUserId(currentUserId, pageable)
        val total = excursionRepository.countCustomExcursionsByUserId(currentUserId)
        val totalPages = ((total + query.size - 1) / query.size).toInt()

        return UserCustomExcursionPageResponse(
            excursions = excursions.map { it.toExcursionShortItem(currentUserId) },
            page = query.page,
            size = query.size,
            totalPages = totalPages,
            totalElements = total,
        )
    }

    @Transactional
    override fun createCustomExcursion(request: CreateCustomExcursionCommand): ExcursionDetailResponse {
        validatePoints(request.points)

        val currentUserId = securityService.getCurrentUserId()

        val orderedIds = request.points.sortedBy { it.order }.map { it.pointId }
        val orderedPoints = pointService.getActivePointsByIds(orderedIds)

        val distance = pointService.computeRouteDistanceMeters(orderedIds)
        val durationMin = orderedPoints.sumOf { it.visitTimeMin ?: 0 }

        val saved =
            excursionRepository.save(
                request.toExcursion(
                    ownerId = currentUserId,
                    distance = distance,
                    durationMin = durationMin,
                ),
            )
        val excursionId = requireNotNull(saved.id) { "Не удалось сохранить экскурсию" }

        excursionPointRepository.saveAll(
            request.points.map { item ->
                ExcursionPoint(
                    excursion = AggregateReference.to(excursionId),
                    point = AggregateReference.to(item.pointId),
                    orderIndex = item.order,
                )
            },
        )

        val view =
            excursionRepository.findShortViewById(excursionId)
                ?: throw NotFoundException("Экскурсия с id=$excursionId не найдена")
        val points = excursionPointRepository.findOrderedPointsByExcursionId(excursionId)
        return view.toExcursionDetailResponse(currentUserId, points)
    }

    @Transactional
    override fun updateCustomExcursion(
        id: Long,
        request: UpdateCustomExcursionRequest,
    ): ExcursionShortItem {
        val (excursion, currentUserId) = loadOwnCustomExcursion(id)

        val updated =
            excursion.copy(
                title = request.title ?: excursion.title,
                description = request.description ?: excursion.description,
                shortDescription = request.shortDescription ?: excursion.shortDescription,
                visibility = request.visibility ?: excursion.visibility,
            )
        excursionRepository.save(updated)

        val points = excursionPointRepository.findOrderedPointsByExcursionId(id)
        if (points.isEmpty()) {
            throw NotFoundException("У экскурсии id=$id нет точек маршрута")
        }
        val view =
            excursionRepository.findShortViewById(id)
                ?: throw NotFoundException("Экскурсия с id=$id не найдена")
        return view.toExcursionShortItemDetail(currentUserId, points)
    }

    @Transactional
    override fun setExcursionPoints(
        id: Long,
        request: SetExcursionPointsCommand,
    ): ExcursionDetailResponse {
        validatePoints(request.points)
        val (excursion, currentUserId) = loadOwnCustomExcursion(id)

        val orderedIds = request.points.sortedBy { it.order }.map { it.pointId }
        val orderedPoints = pointService.getActivePointsByIds(orderedIds)

        val distance = pointService.computeRouteDistanceMeters(orderedIds)
        val durationMin = orderedPoints.sumOf { it.visitTimeMin ?: 0 }

        excursionPointRepository.deleteByExcursionId(id)
        excursionPointRepository.saveAll(
            request.points.map { item ->
                ExcursionPoint(
                    excursion = AggregateReference.to(id),
                    point = AggregateReference.to(item.pointId),
                    orderIndex = item.order,
                )
            },
        )

        excursionRepository.save(
            excursion.copy(distance = distance, durationMin = durationMin),
        )
        val view =
            excursionRepository.findShortViewById(id)
                ?: throw NotFoundException("Экскурсия с id=$id не найдена")
        val points = excursionPointRepository.findOrderedPointsByExcursionId(id)
        return view.toExcursionDetailResponse(currentUserId, points)
    }

    @Transactional
    override fun deleteCustomExcursion(id: Long) {
        val (excursion, _) = loadOwnCustomExcursion(id)
        excursionRepository.delete(excursion)
    }

    private fun loadOwnCustomExcursion(id: Long): Pair<Excursion, Long> {
        val currentUserId = securityService.getCurrentUserId()
        val excursion =
            excursionRepository
                .findById(id)
                .orElseThrow { NotFoundException("Экскурсия с id=$id не найдена") }
        if (excursion.routeType != ExcursionRouteType.CUSTOM ||
            excursion.owner?.id != currentUserId
        ) {
            throw NotFoundException("Экскурсия с id=$id не найдена")
        }
        return excursion to currentUserId
    }

    override fun getUserFavoriteExcursions(query: FavoriteExcursionsPageQuery): UserFavoriteExcursionPageResponse {
        val currentUserId = securityService.getCurrentUserId()
        val pageable = PageRequest.of(query.page, query.size)

        val excursions = favoriteExcursionRepository.findFavoriteExcursionsByUserId(currentUserId, pageable)
        val total = favoriteExcursionRepository.countFavoriteExcursionsByUserId(currentUserId)
        val totalPages = ((total + query.size - 1) / query.size).toInt()

        return UserFavoriteExcursionPageResponse(
            excursions = excursions.map { it.toExcursionShortItem(currentUserId) },
            page = query.page,
            size = query.size,
            totalPages = totalPages,
            totalElements = total,
        )
    }

    override fun favoriteExcursion(excursionId: Long) {
        val currentUserId = securityService.getCurrentUserId()
        val excursion =
            excursionRepository
                .findById(excursionId)
                .orElseThrow { NotFoundException("Экскурсия с id=$excursionId не найдена") }

        val accessible =
            when (excursion.routeType) {
                ExcursionRouteType.PREBUILT -> {
                    excursion.visibility == ExcursionVisibility.PUBLIC
                }

                ExcursionRouteType.CUSTOM -> {
                    excursion.visibility == ExcursionVisibility.PUBLIC ||
                        excursion.owner?.id == currentUserId
                }
            }
        if (!accessible) {
            throw NotFoundException("Экскурсия с id=$excursionId не найдена")
        }

        if (favoriteExcursionRepository.existsByUserIdAndExcursionId(currentUserId, excursionId)) {
            return
        }
        favoriteExcursionRepository.save(
            FavoriteExcursion(
                user = AggregateReference.to(currentUserId),
                excursion = AggregateReference.to(excursionId),
            ),
        )
    }

    override fun unfavoriteExcursion(excursionId: Long) {
        val currentUserId = securityService.getCurrentUserId()
        favoriteExcursionRepository.deleteByUserIdAndExcursionId(currentUserId, excursionId)
    }

    override fun getAdminExcursionsPage(
        query: AdminExcursionPageQueryCommand,
    ): AdminExcursionPageResponse {
        val safeSearch = query.search?.trim()?.takeIf { it.isNotEmpty() }

        val direction =
            when (query.sortDirection) {
                SortDirection.ASC -> Sort.Direction.ASC
                SortDirection.DESC -> Sort.Direction.DESC
            }
        val pageable = PageRequest.of(query.page, query.size, Sort.by(direction, query.sortBy.value))
        val excursions = excursionRepository.findAdminExcursionPage(safeSearch, pageable)
        val total = excursionRepository.countAdminExcursionPage(safeSearch)
        val totalPages = ((total + query.size - 1) / query.size).toInt()

        return AdminExcursionPageResponse(
            excursions = excursions.map { it.toExcursionShortItem(null) },
            page = query.page,
            size = query.size,
            totalPages = totalPages,
            totalElements = total,
        )
    }

    override fun getAdminExcursionDetail(id: Long): AdminExcursionDetailResponse {
        val view =
            excursionRepository.findAdminDetailViewById(id)
                ?: throw NotFoundException("Экскурсия с id=$id не найдена")

        val points = excursionPointRepository.findAdminPointsByExcursionId(id)
        if (points.isEmpty()) {
            throw NotFoundException("У экскурсии id=$id нет точек маршрута")
        }

        return view.toAdminExcursionDetailResponse(points)
    }

    @Transactional
    override fun createPrebuiltExcursion(request: AdminCreatePrebuiltExcursionCommand): AdminExcursionDetailResponse {
        validatePoints(request.points)

        val currentUserId = securityService.getCurrentUserId()

        val orderedIds = request.points.sortedBy { it.order }.map { it.pointId }
        val orderedPoints = pointService.getActivePointsByIds(orderedIds)

        val distance = pointService.computeRouteDistanceMeters(orderedIds)
        val durationMin = orderedPoints.sumOf { it.visitTimeMin ?: 0 }

        val saved =
            excursionRepository.save(
                request.toExcursion(
                    createdById = currentUserId,
                    distance = distance,
                    durationMin = durationMin,
                ),
            )
        val excursionId = requireNotNull(saved.id) { "Не удалось сохранить экскурсию" }

        excursionPointRepository.saveAll(
            request.points.map { item ->
                ExcursionPoint(
                    excursion = AggregateReference.to(excursionId),
                    point = AggregateReference.to(item.pointId),
                    orderIndex = item.order,
                )
            },
        )

        val view =
            excursionRepository.findAdminDetailViewById(excursionId)
                ?: throw NotFoundException("Экскурсия с id=$excursionId не найдена")
        val points = excursionPointRepository.findAdminPointsByExcursionId(excursionId)
        return view.toAdminExcursionDetailResponse(points)
    }

    @Transactional
    override fun patchPrebuiltExcursion(
        id: Long,
        request: AdminPatchPrebuiltExcursionRequest,
    ): AdminExcursionDetailResponse {
        val excursion =
            excursionRepository
                .findById(id)
                .orElseThrow { NotFoundException("Экскурсия с id=$id не найдена") }

        if (excursion.routeType != ExcursionRouteType.PREBUILT) {
            throw BadRequestException("Можно обновлять только готовые (PREBUILT) экскурсии")
        }

        val updated =
            excursion.copy(
                title = request.title ?: excursion.title,
                description = request.description ?: excursion.description,
                shortDescription = request.shortDescription ?: excursion.shortDescription,
                visibility = request.visibility?.let(ExcursionVisibility::valueOf) ?: excursion.visibility,
            )
        excursionRepository.save(updated)

        val points = excursionPointRepository.findAdminPointsByExcursionId(id)
        if (points.isEmpty()) {
            throw NotFoundException("У экскурсии id=$id нет точек маршрута")
        }
        val view =
            excursionRepository.findAdminDetailViewById(id)
                ?: throw NotFoundException("Экскурсия с id=$id не найдена")
        return view.toAdminExcursionDetailResponse(points)
    }

    @Transactional
    override fun setAdminExcursionPoints(
        id: Long,
        request: SetExcursionPointsCommand,
    ): AdminExcursionDetailResponse {
        validatePoints(request.points)

        val excursion =
            excursionRepository
                .findById(id)
                .orElseThrow { NotFoundException("Экскурсия с id=$id не найдена") }

        val orderedIds = request.points.sortedBy { it.order }.map { it.pointId }
        val orderedPoints = pointService.getActivePointsByIds(orderedIds)

        val distance = pointService.computeRouteDistanceMeters(orderedIds)
        val durationMin = orderedPoints.sumOf { it.visitTimeMin ?: 0 }

        excursionPointRepository.deleteByExcursionId(id)
        excursionPointRepository.saveAll(
            request.points.map { item ->
                ExcursionPoint(
                    excursion = AggregateReference.to(id),
                    point = AggregateReference.to(item.pointId),
                    orderIndex = item.order,
                )
            },
        )

        excursionRepository.save(
            excursion.copy(distance = distance, durationMin = durationMin),
        )
        val view =
            excursionRepository.findAdminDetailViewById(id)
                ?: throw NotFoundException("Экскурсия с id=$id не найдена")
        val points = excursionPointRepository.findAdminPointsByExcursionId(id)
        return view.toAdminExcursionDetailResponse(points)
    }

    @Transactional
    override fun deleteExcursion(id: Long) {
        if (!excursionRepository.existsById(id)) {
            throw NotFoundException("Экскурсия с id=$id не найдена")
        }
        excursionRepository.deleteById(id)
    }

    @Transactional
    override fun addReview(
        excursionId: Long,
        command: ExcursionAddReviewCommand,
    ): ExcursionReviewResponse {
        val currentUserId = securityService.getCurrentUserId()
        ensureExcursionAccessible(excursionId, currentUserId)

        if (excursionReviewRepository.existsByExcursionIdAndUserId(excursionId, currentUserId)) {
            throw ConflictException("Отзыв на экскурсию уже оставлен")
        }

        val saved =
            excursionReviewRepository.save(
                ExcursionReview(
                    excursion = AggregateReference.to(excursionId),
                    user = AggregateReference.to(currentUserId),
                    rating = command.rating,
                    reviewText = command.reviewText,
                    visitDate = command.visitDate,
                    isActive = true,
                ),
            )
        return saved.toExcursionReviewResponse()
    }

    @Transactional
    override fun patchOwnReview(
        excursionId: Long,
        request: PatchExcursionReview,
    ): ExcursionReviewResponse {
        val currentUserId = securityService.getCurrentUserId()
        val review =
            excursionReviewRepository.findByExcursionIdAndUserId(excursionId, currentUserId)
                ?: throw NotFoundException("Отзыв на экскурсию id=$excursionId не найден")

        val updated =
            review.copy(
                rating = request.rating ?: review.rating,
                reviewText = request.reviewText ?: review.reviewText,
                visitDate = request.visitDate ?: review.visitDate,
            )
        return excursionReviewRepository.save(updated).toExcursionReviewResponse()
    }

    @Transactional
    override fun deleteOwnReview(excursionId: Long) {
        val currentUserId = securityService.getCurrentUserId()
        val review =
            excursionReviewRepository.findByExcursionIdAndUserId(excursionId, currentUserId)
                ?: throw NotFoundException("Отзыв на экскурсию id=$excursionId не найден")
        excursionReviewRepository.delete(review)
    }

    @Transactional(readOnly = true)
    override fun getReviews(excursionId: Long): ExcursionReviewListResponse {
        if (!excursionRepository.existsById(excursionId)) {
            throw NotFoundException("Экскурсия с id=$excursionId не найдена")
        }
        val reviews =
            excursionReviewRepository
                .findActiveByExcursionId(excursionId)
                .map { it.toExcursionReviewResponse() }
        return ExcursionReviewListResponse(reviews)
    }

    private fun ensureExcursionAccessible(
        excursionId: Long,
        currentUserId: Long,
    ) {
        val excursion =
            excursionRepository
                .findById(excursionId)
                .orElseThrow { NotFoundException("Экскурсия с id=$excursionId не найдена") }

        val accessible =
            when (excursion.routeType) {
                ExcursionRouteType.PREBUILT -> {
                    excursion.visibility == ExcursionVisibility.PUBLIC
                }

                ExcursionRouteType.CUSTOM -> {
                    excursion.visibility == ExcursionVisibility.PUBLIC ||
                        excursion.owner?.id == currentUserId
                }
            }
        if (!accessible) {
            throw NotFoundException("Экскурсия с id=$excursionId не найдена")
        }
    }

    private fun validatePoints(points: List<ExcursionPointOrderItemCommand>) {
        if (points.isEmpty()) {
            throw BadRequestException("Экскурсия должна содержать хотя бы одну точку")
        }
        val orders = points.map { it.order }
        val expectedOrders = (1..points.size).toSet()
        if (orders.toSet() != expectedOrders) {
            throw BadRequestException(
                "Порядок точек должен начинаться с 1, без пропусков и дубликатов",
            )
        }
        val pointIds = points.map { it.pointId }
        if (pointIds.toSet().size != pointIds.size) {
            throw BadRequestException("Точки не должны повторяться в маршруте")
        }
    }
}
