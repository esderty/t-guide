package t.lab.guide.service.mock

import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service
import t.lab.guide.dto.admin.excursion.AdminExcursionDetailResponse
import t.lab.guide.dto.admin.excursion.AdminExcursionPageResponse
import t.lab.guide.dto.admin.excursion.AdminPatchPrebuiltExcursionRequest
import t.lab.guide.dto.admin.excursion.command.AdminCreatePrebuiltExcursionCommand
import t.lab.guide.dto.admin.excursion.command.AdminExcursionPageQueryCommand
import t.lab.guide.dto.admin.point.AdminPointShortItem
import t.lab.guide.dto.common.GeoPoint
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
import t.lab.guide.dto.point.PointListResponse
import t.lab.guide.dto.point.PointShortItem
import t.lab.guide.enums.ExcursionRouteType
import t.lab.guide.enums.ExcursionVisibility
import t.lab.guide.exception.NotFoundException
import t.lab.guide.service.ExcursionService
import t.lab.guide.service.SecurityService
import java.math.BigDecimal
import java.time.OffsetDateTime

@Service
@Profile("demo")
class MockExcursionService(
    private val securityService: SecurityService,
) : ExcursionService {
    override fun searchExcursions(request: ExcursionSearchCommand): ExcursionListResponse {
        val categoryIds = request.categoryIds
        val visitTime = request.visitTime

        val filtered =
            mock
                .filter { matchesCategories(it.categoryIds, categoryIds) }
                .filter { matchesDuration(it.durationMin, visitTime) }

        return ExcursionListResponse(filtered)
    }

    override fun getExcursionDetail(id: Long): ExcursionDetailResponse =
        mockDetail.firstOrNull { it.id == id }
            ?: throw NotFoundException("Экскурсия с id=$id не найдена")

    override fun getUserCustomExcursions(query: CustomExcursionsPageQuery): UserCustomExcursionPageResponse {
        securityService.getCurrentUserId()
        val items = mock.filter { it.routeType == ExcursionRouteType.CUSTOM }
        return UserCustomExcursionPageResponse(
            excursions = items,
            page = query.page,
            size = query.size,
            totalElements = items.size.toLong(),
            totalPages = if (query.size <= 0) 0 else ((items.size + query.size - 1) / query.size),
        )
    }

    override fun createCustomExcursion(request: CreateCustomExcursionCommand): ExcursionDetailResponse {
        securityService.getCurrentUserId()
        val template = mockDetail[0]
        return ExcursionDetailResponse(
            id = 100L,
            routeType = ExcursionRouteType.CUSTOM,
            visibility = ExcursionVisibility.PRIVATE,
            isOwner = true,
            title = request.title,
            description = request.description,
            shortDescription = request.shortDescription,
            distance = template.distance,
            durationMin = template.durationMin,
            coordinates = template.coordinates,
            points = template.points,
            rating = null,
            reviewsCount = 0,
        )
    }

    override fun updateCustomExcursion(
        id: Long,
        request: UpdateCustomExcursionRequest,
    ): ExcursionShortItem {
        securityService.getCurrentUserId()
        val template = mock[0]
        return ExcursionShortItem(
            id = id,
            routeType = ExcursionRouteType.CUSTOM,
            visibility = request.visibility ?: ExcursionVisibility.PRIVATE,
            isOwner = true,
            title = request.title ?: template.title,
            description = request.description ?: template.description,
            shortDescription = request.shortDescription ?: template.shortDescription,
            distance = template.distance,
            durationMin = template.durationMin,
            pointsCount = template.pointsCount,
            coordinates = template.coordinates,
            categoryIds = template.categoryIds,
            rating = template.rating,
            reviewsCount = template.reviewsCount,
        )
    }

    override fun setExcursionPoints(
        id: Long,
        request: SetExcursionPointsCommand,
    ): ExcursionDetailResponse {
        securityService.getCurrentUserId()
        val template = mockDetail[0]
        return ExcursionDetailResponse(
            id = id,
            routeType = ExcursionRouteType.CUSTOM,
            visibility = ExcursionVisibility.PRIVATE,
            isOwner = true,
            title = template.title,
            description = template.description,
            shortDescription = template.shortDescription,
            distance = template.distance,
            durationMin = template.durationMin,
            coordinates = template.coordinates,
            points = template.points,
            rating = template.rating,
            reviewsCount = template.reviewsCount,
        )
    }

    override fun deleteCustomExcursion(id: Long) {
        securityService.getCurrentUserId()
    }

    override fun getUserFavoriteExcursions(query: FavoriteExcursionsPageQuery): UserFavoriteExcursionPageResponse {
        securityService.getCurrentUserId()
        val items = mock.subList(2, 3)
        return UserFavoriteExcursionPageResponse(
            excursions = items,
            page = query.page,
            size = query.size,
            totalElements = items.size.toLong(),
            totalPages = if (query.size <= 0) 0 else ((items.size + query.size - 1) / query.size),
        )
    }

    override fun favoriteExcursion(excursionId: Long) {
        securityService.getCurrentUserId()
    }

    override fun unfavoriteExcursion(excursionId: Long) {
        securityService.getCurrentUserId()
    }

    override fun getAdminExcursionsPage(
        query: AdminExcursionPageQueryCommand,
    ): AdminExcursionPageResponse {
        val filtered =
            mock.filter {
                query.search.isNullOrBlank() || it.title.lowercase().contains(query.search.lowercase())
            }

        val totalElements = filtered.size
        val totalPages = if (query.size > 0) ((totalElements + query.size - 1) / query.size) else 0

        val fromIndex = minOf(query.page * query.size, totalElements)
        val toIndex = minOf(fromIndex + query.size, totalElements)
        val pageContent = filtered.subList(fromIndex, toIndex)

        return AdminExcursionPageResponse(
            excursions = pageContent,
            page = query.page,
            size = query.size,
            totalElements = totalElements.toLong(),
            totalPages = totalPages,
        )
    }

    override fun getAdminExcursionDetail(id: Long): AdminExcursionDetailResponse {
        val existing = getExcursionDetail(id)
        return toAdminDetail(existing)
    }

    override fun createPrebuiltExcursion(request: AdminCreatePrebuiltExcursionCommand): AdminExcursionDetailResponse {
        val template = mockDetail[0]
        val now = OffsetDateTime.now()
        return AdminExcursionDetailResponse(
            id = 100L,
            ownerId = null,
            routeType = ExcursionRouteType.PREBUILT,
            visibility = request.visibility,
            title = request.title,
            description = request.description,
            shortDescription = request.shortDescription,
            distance = template.distance,
            durationMin = template.durationMin,
            coordinates = template.coordinates,
            points = toAdminPoints(template.points),
            createdBy = 1L,
            createdAt = now,
            updatedAt = now,
            rating = null,
            reviewsCount = 0,
        )
    }

    override fun patchPrebuiltExcursion(
        id: Long,
        request: AdminPatchPrebuiltExcursionRequest,
    ): AdminExcursionDetailResponse {
        val existing = getExcursionDetail(id)
        val now = OffsetDateTime.now()
        return AdminExcursionDetailResponse(
            id = existing.id,
            ownerId = null,
            routeType = ExcursionRouteType.PREBUILT,
            visibility = request.visibility?.let(ExcursionVisibility::valueOf) ?: existing.visibility,
            title = request.title ?: existing.title,
            description = request.description ?: existing.description,
            shortDescription = request.shortDescription ?: existing.shortDescription,
            distance = existing.distance,
            durationMin = existing.durationMin,
            coordinates = existing.coordinates,
            points = toAdminPoints(existing.points),
            createdBy = 1L,
            createdAt = now.minusDays(30),
            updatedAt = now,
            rating = existing.rating,
            reviewsCount = existing.reviewsCount,
        )
    }

    override fun setAdminExcursionPoints(
        id: Long,
        request: SetExcursionPointsCommand,
    ): AdminExcursionDetailResponse {
        val existing = getExcursionDetail(id)
        val now = OffsetDateTime.now()
        return AdminExcursionDetailResponse(
            id = existing.id,
            ownerId = null,
            routeType = ExcursionRouteType.PREBUILT,
            visibility = existing.visibility,
            title = existing.title,
            description = existing.description,
            shortDescription = existing.shortDescription,
            distance = existing.distance,
            durationMin = existing.durationMin,
            coordinates = existing.coordinates,
            points = toAdminPoints(existing.points),
            createdBy = 1L,
            createdAt = now.minusDays(30),
            updatedAt = now,
            rating = existing.rating,
            reviewsCount = existing.reviewsCount,
        )
    }

    override fun deleteExcursion(id: Long) {
        mockDetail.firstOrNull { it.id == id }
            ?: throw NotFoundException("Экскурсия с id=$id не найдена")
    }

    override fun addReview(
        excursionId: Long,
        command: ExcursionAddReviewCommand,
    ): ExcursionReviewResponse {
        val userId = securityService.getCurrentUserId()
        getExcursionDetail(excursionId)
        return ExcursionReviewResponse(
            id = 1L,
            excursionId = excursionId,
            userId = userId,
            rating = command.rating,
            reviewText = command.reviewText,
            visitDate = command.visitDate,
        )
    }

    override fun patchOwnReview(
        excursionId: Long,
        request: PatchExcursionReview,
    ): ExcursionReviewResponse {
        val userId = securityService.getCurrentUserId()
        getExcursionDetail(excursionId)
        return ExcursionReviewResponse(
            id = 1L,
            excursionId = excursionId,
            userId = userId,
            rating = request.rating ?: 5,
            reviewText = request.reviewText ?: "Отличная экскурсия",
            visitDate =
                request.visitDate ?: java.time.LocalDate
                    .now()
                    .minusDays(1),
        )
    }

    override fun deleteOwnReview(excursionId: Long) {
        securityService.getCurrentUserId()
        getExcursionDetail(excursionId)
    }

    override fun getReviews(excursionId: Long): ExcursionReviewListResponse {
        getExcursionDetail(excursionId)
        return ExcursionReviewListResponse(
            reviews =
                listOf(
                    ExcursionReviewResponse(
                        id = 1L,
                        excursionId = excursionId,
                        userId = 42L,
                        rating = 5,
                        reviewText = "Очень понравилось!",
                        visitDate =
                            java.time.LocalDate
                                .now()
                                .minusDays(7),
                    ),
                ),
        )
    }

    private fun toAdminDetail(source: ExcursionDetailResponse): AdminExcursionDetailResponse {
        val now = OffsetDateTime.now()
        return AdminExcursionDetailResponse(
            id = source.id,
            ownerId = if (source.routeType == ExcursionRouteType.PREBUILT) null else 42L,
            routeType = source.routeType,
            visibility = source.visibility,
            title = source.title,
            description = source.description,
            shortDescription = source.shortDescription,
            distance = source.distance,
            durationMin = source.durationMin,
            coordinates = source.coordinates,
            points = toAdminPoints(source.points),
            createdBy = 1L,
            createdAt = now.minusDays(30),
            updatedAt = now,
            rating = source.rating,
            reviewsCount = source.reviewsCount,
        )
    }

    private fun toAdminPoints(source: PointListResponse): List<AdminPointShortItem> = source.points.map { toAdminPoint(it) }

    private fun toAdminPoint(source: PointShortItem): AdminPointShortItem =
        AdminPointShortItem(
            id = source.id,
            title = source.title,
            categoryId = source.categoryId,
            categoryName = source.categoryName,
            visitTime = source.visitTime,
            isActive = true,
            createdAt = OffsetDateTime.now().minusDays(30),
        )

    private companion object {
        private fun matchesCategories(
            excursionCategories: List<Long>?,
            filterCategories: List<Long>?,
        ): Boolean {
            if (filterCategories.isNullOrEmpty()) return true
            if (excursionCategories.isNullOrEmpty()) return false
            return excursionCategories.any { it in filterCategories }
        }

        private fun matchesDuration(
            excursionDuration: Int?,
            maxDuration: Int?,
        ): Boolean {
            if (maxDuration == null) return true
            return excursionDuration != null && excursionDuration <= maxDuration
        }

        private val mock =
            listOf(
                ExcursionShortItem(
                    id = 1L,
                    routeType = ExcursionRouteType.PREBUILT,
                    visibility = ExcursionVisibility.PUBLIC,
                    isOwner = false,
                    title = "Исторический центр Москвы",
                    description = "Прогулка по главным достопримечательностям центра столицы: Красная площадь, ГУМ, Кремль.",
                    shortDescription = "Главные достопримечательности центра",
                    distance = 3200,
                    durationMin = 120,
                    pointsCount = 5,
                    coordinates = GeoPoint(BigDecimal("55.753544"), BigDecimal("37.621202")),
                    categoryIds = listOf(1L, 2L, 3L),
                    rating = 4.7,
                    reviewsCount = 128,
                ),
                ExcursionShortItem(
                    id = 2L,
                    routeType = ExcursionRouteType.PREBUILT,
                    visibility = ExcursionVisibility.PUBLIC,
                    isOwner = false,
                    title = "Музейный маршрут",
                    description = "Обзор главных московских музеев — от Третьяковки до Пушкинского.",
                    shortDescription = "Главные музеи Москвы",
                    distance = 5400,
                    durationMin = 240,
                    pointsCount = 4,
                    coordinates = GeoPoint(BigDecimal("55.741426"), BigDecimal("37.620137")),
                    categoryIds = listOf(3L),
                    rating = 4.5,
                    reviewsCount = 64,
                ),
                ExcursionShortItem(
                    id = 3L,
                    routeType = ExcursionRouteType.PREBUILT,
                    visibility = ExcursionVisibility.PUBLIC,
                    isOwner = false,
                    title = "Парки и набережные",
                    description = "Спокойный маршрут через Парк Горького, Нескучный сад и Воробьёвы горы.",
                    shortDescription = "Прогулка по паркам Москвы",
                    distance = 7800,
                    durationMin = 180,
                    pointsCount = 6,
                    coordinates = GeoPoint(BigDecimal("55.729812"), BigDecimal("37.601348")),
                    categoryIds = listOf(4L),
                    rating = 4.8,
                    reviewsCount = 92,
                ),
                ExcursionShortItem(
                    id = 4L,
                    routeType = ExcursionRouteType.CUSTOM,
                    visibility = ExcursionVisibility.PUBLIC,
                    isOwner = true,
                    title = "Гастрономическая Москва",
                    description = "Лучшие кафе и рестораны в пешей доступности от Тверской.",
                    shortDescription = "Кафе и рестораны от Тверской",
                    distance = 2100,
                    durationMin = 150,
                    pointsCount = 5,
                    coordinates = GeoPoint(BigDecimal("55.764804"), BigDecimal("37.604392")),
                    categoryIds = listOf(5L),
                    rating = 4.2,
                    reviewsCount = 17,
                ),
                ExcursionShortItem(
                    id = 5L,
                    routeType = ExcursionRouteType.CUSTOM,
                    visibility = ExcursionVisibility.PRIVATE,
                    isOwner = true,
                    title = "Шопинг и архитектура",
                    description = "ГУМ, ЦУМ, Петровский пассаж — торговая классика и модерн.",
                    shortDescription = "Торговая классика и модерн",
                    distance = 1800,
                    durationMin = 90,
                    pointsCount = 3,
                    coordinates = GeoPoint(BigDecimal("55.757500"), BigDecimal("37.618000")),
                    categoryIds = listOf(1L, 2L),
                    rating = null,
                    reviewsCount = 0,
                ),
            )

        private val mockDetail =
            listOf(
                ExcursionDetailResponse(
                    id = 1L,
                    routeType = ExcursionRouteType.PREBUILT,
                    visibility = ExcursionVisibility.PUBLIC,
                    isOwner = false,
                    title = "Исторический центр Москвы",
                    description = "Прогулка по главным достопримечательностям центра столицы: Красная площадь, ГУМ, Кремль.",
                    shortDescription = "Главные достопримечательности центра",
                    distance = 3200,
                    durationMin = 120,
                    coordinates = GeoPoint(BigDecimal("55.753544"), BigDecimal("37.621202")),
                    points =
                        PointListResponse(
                            listOf(
                                PointShortItem(
                                    id = 1L,
                                    title = "Красная площадь",
                                    shortDescription = "Главная площадь Москвы",
                                    categoryId = 1L,
                                    categoryName = "Достопримечательности",
                                    coordinates = GeoPoint(BigDecimal("55.753544"), BigDecimal("37.621202")),
                                    visitTime = 60,
                                ),
                                PointShortItem(
                                    id = 2L,
                                    title = "ГУМ",
                                    shortDescription = "Главный универсальный магазин",
                                    categoryId = 2L,
                                    categoryName = "Шопинг",
                                    coordinates = GeoPoint(BigDecimal("55.754713"), BigDecimal("37.621500")),
                                    visitTime = 90,
                                ),
                                PointShortItem(
                                    id = 6L,
                                    title = "Московский Кремль",
                                    shortDescription = "Древняя крепость в центре Москвы",
                                    categoryId = 1L,
                                    categoryName = "Достопримечательности",
                                    coordinates = GeoPoint(BigDecimal("55.751999"), BigDecimal("37.617734")),
                                    visitTime = 120,
                                ),
                            ),
                        ),
                    rating = 4.7,
                    reviewsCount = 128,
                ),
                ExcursionDetailResponse(
                    id = 2L,
                    routeType = ExcursionRouteType.PREBUILT,
                    visibility = ExcursionVisibility.PUBLIC,
                    isOwner = false,
                    title = "Музейный маршрут",
                    description = "Обзор главных московских музеев — от Третьяковки до Пушкинского.",
                    shortDescription = "Главные музеи Москвы",
                    distance = 5400,
                    durationMin = 240,
                    coordinates = GeoPoint(BigDecimal("55.741426"), BigDecimal("37.620137")),
                    points =
                        PointListResponse(
                            listOf(
                                PointShortItem(
                                    id = 3L,
                                    title = "Третьяковская галерея",
                                    shortDescription = "Крупнейшее собрание русского искусства",
                                    categoryId = 3L,
                                    categoryName = "Музеи",
                                    coordinates = GeoPoint(BigDecimal("55.741426"), BigDecimal("37.620137")),
                                    visitTime = 120,
                                ),
                                PointShortItem(
                                    id = 7L,
                                    title = "Пушкинский музей",
                                    shortDescription = "Коллекция мирового искусства",
                                    categoryId = 3L,
                                    categoryName = "Музеи",
                                    coordinates = GeoPoint(BigDecimal("55.747524"), BigDecimal("37.605068")),
                                    visitTime = 120,
                                ),
                            ),
                        ),
                    rating = 4.5,
                    reviewsCount = 64,
                ),
                ExcursionDetailResponse(
                    id = 3L,
                    routeType = ExcursionRouteType.PREBUILT,
                    visibility = ExcursionVisibility.PUBLIC,
                    isOwner = false,
                    title = "Парки и набережные",
                    description = "Спокойный маршрут через Парк Горького, Нескучный сад и Воробьёвы горы.",
                    shortDescription = "Прогулка по паркам Москвы",
                    distance = 7800,
                    durationMin = 180,
                    coordinates = GeoPoint(BigDecimal("55.729812"), BigDecimal("37.601348")),
                    points =
                        PointListResponse(
                            listOf(
                                PointShortItem(
                                    id = 4L,
                                    title = "Парк Горького",
                                    shortDescription = "Центральный парк культуры и отдыха",
                                    categoryId = 4L,
                                    categoryName = "Парки",
                                    coordinates = GeoPoint(BigDecimal("55.729812"), BigDecimal("37.601348")),
                                    visitTime = 150,
                                ),
                                PointShortItem(
                                    id = 8L,
                                    title = "Нескучный сад",
                                    shortDescription = "Старейший парк Москвы",
                                    categoryId = 4L,
                                    categoryName = "Парки",
                                    coordinates = GeoPoint(BigDecimal("55.716610"), BigDecimal("37.589569")),
                                    visitTime = 60,
                                ),
                                PointShortItem(
                                    id = 9L,
                                    title = "Воробьёвы горы",
                                    shortDescription = "Смотровая площадка над Москвой",
                                    categoryId = 4L,
                                    categoryName = "Парки",
                                    coordinates = GeoPoint(BigDecimal("55.710484"), BigDecimal("37.548517")),
                                    visitTime = 90,
                                ),
                            ),
                        ),
                    rating = 4.8,
                    reviewsCount = 92,
                ),
                ExcursionDetailResponse(
                    id = 4L,
                    routeType = ExcursionRouteType.CUSTOM,
                    visibility = ExcursionVisibility.PUBLIC,
                    isOwner = true,
                    title = "Гастрономическая Москва",
                    description = "Лучшие кафе и рестораны в пешей доступности от Тверской.",
                    shortDescription = "Кафе и рестораны от Тверской",
                    distance = 2100,
                    durationMin = 150,
                    coordinates = GeoPoint(BigDecimal("55.764804"), BigDecimal("37.604392")),
                    points =
                        PointListResponse(
                            listOf(
                                PointShortItem(
                                    id = 5L,
                                    title = "Кафе Пушкинъ",
                                    shortDescription = "Ресторан русской кухни в стиле XIX века",
                                    categoryId = 5L,
                                    categoryName = "Еда",
                                    coordinates = GeoPoint(BigDecimal("55.764804"), BigDecimal("37.604392")),
                                    visitTime = 90,
                                ),
                                PointShortItem(
                                    id = 10L,
                                    title = "White Rabbit",
                                    shortDescription = "Современная русская кухня с видом на Москву",
                                    categoryId = 5L,
                                    categoryName = "Еда",
                                    coordinates = GeoPoint(BigDecimal("55.749222"), BigDecimal("37.583688")),
                                    visitTime = 120,
                                ),
                            ),
                        ),
                    rating = 4.2,
                    reviewsCount = 17,
                ),
                ExcursionDetailResponse(
                    id = 5L,
                    routeType = ExcursionRouteType.CUSTOM,
                    visibility = ExcursionVisibility.PRIVATE,
                    isOwner = true,
                    title = "Шопинг и архитектура",
                    description = "ГУМ, ЦУМ, Петровский пассаж — торговая классика и модерн.",
                    shortDescription = "Торговая классика и модерн",
                    distance = 1800,
                    durationMin = 90,
                    coordinates = GeoPoint(BigDecimal("55.757500"), BigDecimal("37.618000")),
                    points =
                        PointListResponse(
                            listOf(
                                PointShortItem(
                                    id = 2L,
                                    title = "ГУМ",
                                    shortDescription = "Главный универсальный магазин",
                                    categoryId = 2L,
                                    categoryName = "Шопинг",
                                    coordinates = GeoPoint(BigDecimal("55.754713"), BigDecimal("37.621500")),
                                    visitTime = 90,
                                ),
                                PointShortItem(
                                    id = 11L,
                                    title = "ЦУМ",
                                    shortDescription = "Центральный универмаг",
                                    categoryId = 2L,
                                    categoryName = "Шопинг",
                                    coordinates = GeoPoint(BigDecimal("55.760359"), BigDecimal("37.620918")),
                                    visitTime = 60,
                                ),
                                PointShortItem(
                                    id = 12L,
                                    title = "Петровский пассаж",
                                    shortDescription = "Исторический торговый пассаж",
                                    categoryId = 2L,
                                    categoryName = "Шопинг",
                                    coordinates = GeoPoint(BigDecimal("55.762103"), BigDecimal("37.619614")),
                                    visitTime = 45,
                                ),
                            ),
                        ),
                    rating = null,
                    reviewsCount = 0,
                ),
            )
    }
}
