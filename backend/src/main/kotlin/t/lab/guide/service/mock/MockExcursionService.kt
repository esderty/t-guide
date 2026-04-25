package t.lab.guide.service.mock

import java.math.BigDecimal
import java.time.OffsetDateTime
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service
import t.lab.guide.dto.admin.excursion.AdminCreatePrebuiltExcursionRequest
import t.lab.guide.dto.admin.excursion.AdminExcursionDetailResponse
import t.lab.guide.dto.admin.excursion.AdminExcursionPageResponse
import t.lab.guide.dto.admin.excursion.AdminPatchPrebuiltExcursionRequest
import t.lab.guide.dto.admin.point.AdminPointShortItem
import t.lab.guide.dto.common.GeoPoint
import t.lab.guide.dto.excursion.CreateCustomExcursionRequest
import t.lab.guide.dto.excursion.ExcursionDetailResponse
import t.lab.guide.dto.excursion.ExcursionListResponse
import t.lab.guide.dto.excursion.ExcursionSearchRequest
import t.lab.guide.dto.excursion.ExcursionShortItem
import t.lab.guide.dto.excursion.SetExcursionPointsRequest
import t.lab.guide.dto.excursion.UpdateCustomExcursionRequest
import t.lab.guide.dto.point.PointListResponse
import t.lab.guide.dto.point.PointShortItem
import t.lab.guide.entity.enums.ExcursionRouteType
import t.lab.guide.entity.enums.ExcursionVisibility
import t.lab.guide.exception.NotFoundException
import t.lab.guide.service.ExcursionService
import t.lab.guide.service.SecurityService

@Service
@Profile("demo")
class MockExcursionService(
    private val securityService: SecurityService,
) : ExcursionService {
    override fun searchExcursions(request: ExcursionSearchRequest): ExcursionListResponse {
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

    override fun createCustomExcursion(request: CreateCustomExcursionRequest): ExcursionDetailResponse {
        securityService.getCurrentUserId()
        val template = mockDetail[0]
        return ExcursionDetailResponse(
            id = 100L,
            routeType = ExcursionRouteType.CUSTOM,
            visibility = ExcursionVisibility.PRIVATE,
            isOwner = true,
            title = request.title,
            description = request.description,
            distance = template.distance,
            duration = template.duration,
            coordinates = template.coordinates,
            points = template.points,
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
            distance = template.distance,
            durationMin = template.durationMin,
            pointCounts = template.pointCounts,
            coordinates = template.coordinates,
            categoryIds = template.categoryIds,
        )
    }

    override fun setExcursionPoints(
        id: Long,
        request: SetExcursionPointsRequest,
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
            distance = template.distance,
            duration = template.duration,
            coordinates = template.coordinates,
            points = template.points,
        )
    }

    override fun deleteCustomExcursion(id: Long) {
        securityService.getCurrentUserId()
    }

    override fun favoriteExcursion(excursionId: Long) {
        securityService.getCurrentUserId()
    }

    override fun unfavoriteExcursion(excursionId: Long) {
        securityService.getCurrentUserId()
    }

    override fun getAdminExcursionsPage(
        page: Int,
        size: Int,
        sortBy: String?,
        sortDirection: String?,
        search: String?,
    ): AdminExcursionPageResponse {
        val filtered =
            mock.filter {
                search.isNullOrBlank() || it.title.lowercase().contains(search.lowercase())
            }

        val totalElements = filtered.size
        val totalPages = if (size > 0) ((totalElements + size - 1) / size) else 0

        val fromIndex = minOf(page * size, totalElements)
        val toIndex = minOf(fromIndex + size, totalElements)
        val pageContent = filtered.subList(fromIndex, toIndex)

        return AdminExcursionPageResponse(
            excursions = pageContent,
            page = page,
            size = size,
            totalElements = totalElements.toLong(),
            totalPages = totalPages,
        )
    }

    override fun getAdminExcursionDetail(id: Long): AdminExcursionDetailResponse {
        val existing = getExcursionDetail(id)
        return toAdminDetail(existing)
    }

    override fun createPrebuiltExcursion(request: AdminCreatePrebuiltExcursionRequest): AdminExcursionDetailResponse {
        val template = mockDetail[0]
        val now = OffsetDateTime.now()
        return AdminExcursionDetailResponse(
            id = 100L,
            ownerId = null,
            routeType = ExcursionRouteType.PREBUILT,
            visibility = request.visibility ?: ExcursionVisibility.PUBLIC,
            title = request.title,
            description = request.description,
            distance = template.distance,
            durationMin = template.duration,
            coordinates = template.coordinates,
            points = toAdminPoints(template.points),
            createdBy = 1L,
            createdAt = now,
            updatedAt = now,
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
            visibility = request.visibility ?: existing.visibility,
            title = request.title ?: existing.title,
            description = request.description ?: existing.description,
            distance = existing.distance,
            durationMin = existing.duration,
            coordinates = existing.coordinates,
            points = toAdminPoints(existing.points),
            createdBy = 1L,
            createdAt = now.minusDays(30),
            updatedAt = now,
        )
    }

    override fun setAdminExcursionPoints(
        id: Long,
        request: SetExcursionPointsRequest,
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
            distance = existing.distance,
            durationMin = existing.duration,
            coordinates = existing.coordinates,
            points = toAdminPoints(existing.points),
            createdBy = 1L,
            createdAt = now.minusDays(30),
            updatedAt = now,
        )
    }

    override fun deleteExcursion(id: Long) {
        mockDetail.firstOrNull { it.id == id }
            ?: throw NotFoundException("Экскурсия с id=$id не найдена")
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
            distance = source.distance,
            durationMin = source.duration,
            coordinates = source.coordinates,
            points = toAdminPoints(source.points),
            createdBy = 1L,
            createdAt = now.minusDays(30),
            updatedAt = now,
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
                    distance = 3200,
                    durationMin = 120,
                    pointCounts = 5,
                    coordinates = GeoPoint(BigDecimal("55.753544"), BigDecimal("37.621202")),
                    categoryIds = listOf(1L, 2L, 3L),
                ),
                ExcursionShortItem(
                    id = 2L,
                    routeType = ExcursionRouteType.PREBUILT,
                    visibility = ExcursionVisibility.PUBLIC,
                    isOwner = false,
                    title = "Музейный маршрут",
                    description = "Обзор главных московских музеев — от Третьяковки до Пушкинского.",
                    distance = 5400,
                    durationMin = 240,
                    pointCounts = 4,
                    coordinates = GeoPoint(BigDecimal("55.741426"), BigDecimal("37.620137")),
                    categoryIds = listOf(3L),
                ),
                ExcursionShortItem(
                    id = 3L,
                    routeType = ExcursionRouteType.PREBUILT,
                    visibility = ExcursionVisibility.PUBLIC,
                    isOwner = false,
                    title = "Парки и набережные",
                    description = "Спокойный маршрут через Парк Горького, Нескучный сад и Воробьёвы горы.",
                    distance = 7800,
                    durationMin = 180,
                    pointCounts = 6,
                    coordinates = GeoPoint(BigDecimal("55.729812"), BigDecimal("37.601348")),
                    categoryIds = listOf(4L),
                ),
                ExcursionShortItem(
                    id = 4L,
                    routeType = ExcursionRouteType.CUSTOM,
                    visibility = ExcursionVisibility.PUBLIC,
                    isOwner = true,
                    title = "Гастрономическая Москва",
                    description = "Лучшие кафе и рестораны в пешей доступности от Тверской.",
                    distance = 2100,
                    durationMin = 150,
                    pointCounts = 5,
                    coordinates = GeoPoint(BigDecimal("55.764804"), BigDecimal("37.604392")),
                    categoryIds = listOf(5L),
                ),
                ExcursionShortItem(
                    id = 5L,
                    routeType = ExcursionRouteType.CUSTOM,
                    visibility = ExcursionVisibility.PRIVATE,
                    isOwner = true,
                    title = "Шопинг и архитектура",
                    description = "ГУМ, ЦУМ, Петровский пассаж — торговая классика и модерн.",
                    distance = 1800,
                    durationMin = 90,
                    pointCounts = 3,
                    coordinates = GeoPoint(BigDecimal("55.757500"), BigDecimal("37.618000")),
                    categoryIds = listOf(1L, 2L),
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
                    distance = 3200,
                    duration = 120,
                    coordinates = GeoPoint(BigDecimal("55.753544"), BigDecimal("37.621202")),
                    points =
                        PointListResponse(
                            listOf(
                                PointShortItem(
                                    1L,
                                    "Красная площадь",
                                    1L,
                                    "Достопримечательности",
                                    GeoPoint(BigDecimal("55.753544"), BigDecimal("37.621202")),
                                    60,
                                ),
                                PointShortItem(
                                    2L,
                                    "ГУМ",
                                    2L,
                                    "Шопинг",
                                    GeoPoint(BigDecimal("55.754713"), BigDecimal("37.621500")),
                                    90,
                                ),
                                PointShortItem(
                                    6L,
                                    "Московский Кремль",
                                    1L,
                                    "Достопримечательности",
                                    GeoPoint(BigDecimal("55.751999"), BigDecimal("37.617734")),
                                    120,
                                ),
                            ),
                        ),
                ),
                ExcursionDetailResponse(
                    id = 2L,
                    routeType = ExcursionRouteType.PREBUILT,
                    visibility = ExcursionVisibility.PUBLIC,
                    isOwner = false,
                    title = "Музейный маршрут",
                    description = "Обзор главных московских музеев — от Третьяковки до Пушкинского.",
                    distance = 5400,
                    duration = 240,
                    coordinates = GeoPoint(BigDecimal("55.741426"), BigDecimal("37.620137")),
                    points =
                        PointListResponse(
                            listOf(
                                PointShortItem(
                                    3L,
                                    "Третьяковская галерея",
                                    3L,
                                    "Музеи",
                                    GeoPoint(BigDecimal("55.741426"), BigDecimal("37.620137")),
                                    120,
                                ),
                                PointShortItem(
                                    7L,
                                    "Пушкинский музей",
                                    3L,
                                    "Музеи",
                                    GeoPoint(BigDecimal("55.747524"), BigDecimal("37.605068")),
                                    120,
                                ),
                            ),
                        ),
                ),
                ExcursionDetailResponse(
                    id = 3L,
                    routeType = ExcursionRouteType.PREBUILT,
                    visibility = ExcursionVisibility.PUBLIC,
                    isOwner = false,
                    title = "Парки и набережные",
                    description = "Спокойный маршрут через Парк Горького, Нескучный сад и Воробьёвы горы.",
                    distance = 7800,
                    duration = 180,
                    coordinates = GeoPoint(BigDecimal("55.729812"), BigDecimal("37.601348")),
                    points =
                        PointListResponse(
                            listOf(
                                PointShortItem(
                                    4L,
                                    "Парк Горького",
                                    4L,
                                    "Парки",
                                    GeoPoint(BigDecimal("55.729812"), BigDecimal("37.601348")),
                                    150,
                                ),
                                PointShortItem(
                                    8L,
                                    "Нескучный сад",
                                    4L,
                                    "Парки",
                                    GeoPoint(BigDecimal("55.716610"), BigDecimal("37.589569")),
                                    60,
                                ),
                                PointShortItem(
                                    9L,
                                    "Воробьёвы горы",
                                    4L,
                                    "Парки",
                                    GeoPoint(BigDecimal("55.710484"), BigDecimal("37.548517")),
                                    90,
                                ),
                            ),
                        ),
                ),
                ExcursionDetailResponse(
                    id = 4L,
                    routeType = ExcursionRouteType.CUSTOM,
                    visibility = ExcursionVisibility.PUBLIC,
                    isOwner = true,
                    title = "Гастрономическая Москва",
                    description = "Лучшие кафе и рестораны в пешей доступности от Тверской.",
                    distance = 2100,
                    duration = 150,
                    coordinates = GeoPoint(BigDecimal("55.764804"), BigDecimal("37.604392")),
                    points =
                        PointListResponse(
                            listOf(
                                PointShortItem(
                                    5L,
                                    "Кафе Пушкинъ",
                                    5L,
                                    "Еда",
                                    GeoPoint(BigDecimal("55.764804"), BigDecimal("37.604392")),
                                    90,
                                ),
                                PointShortItem(
                                    10L,
                                    "White Rabbit",
                                    5L,
                                    "Еда",
                                    GeoPoint(BigDecimal("55.749222"), BigDecimal("37.583688")),
                                    120,
                                ),
                            ),
                        ),
                ),
                ExcursionDetailResponse(
                    id = 5L,
                    routeType = ExcursionRouteType.CUSTOM,
                    visibility = ExcursionVisibility.PRIVATE,
                    isOwner = true,
                    title = "Шопинг и архитектура",
                    description = "ГУМ, ЦУМ, Петровский пассаж — торговая классика и модерн.",
                    distance = 1800,
                    duration = 90,
                    coordinates = GeoPoint(BigDecimal("55.757500"), BigDecimal("37.618000")),
                    points =
                        PointListResponse(
                            listOf(
                                PointShortItem(
                                    2L,
                                    "ГУМ",
                                    2L,
                                    "Шопинг",
                                    GeoPoint(BigDecimal("55.754713"), BigDecimal("37.621500")),
                                    90,
                                ),
                                PointShortItem(
                                    11L,
                                    "ЦУМ",
                                    2L,
                                    "Шопинг",
                                    GeoPoint(BigDecimal("55.760359"), BigDecimal("37.620918")),
                                    60,
                                ),
                                PointShortItem(
                                    12L,
                                    "Петровский пассаж",
                                    2L,
                                    "Шопинг",
                                    GeoPoint(BigDecimal("55.762103"), BigDecimal("37.619614")),
                                    45,
                                ),
                            ),
                        ),
                ),
            )
    }
}
