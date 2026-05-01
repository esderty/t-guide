package t.lab.guide.service.mock

import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import t.lab.guide.dto.admin.point.AdminPatchPointRequest
import t.lab.guide.dto.admin.point.AdminPointDetailResponse
import t.lab.guide.dto.admin.point.AdminPointMediaItem
import t.lab.guide.dto.admin.point.AdminPointPageResponse
import t.lab.guide.dto.admin.point.AdminPointShortItem
import t.lab.guide.dto.admin.point.command.AdminCreatePointCommand
import t.lab.guide.dto.admin.point.command.AdminUploadPointMediaCommand
import t.lab.guide.dto.category.CategoryItem
import t.lab.guide.dto.common.GeoPoint
import t.lab.guide.dto.common.command.toDto
import t.lab.guide.dto.point.PointDetailResponse
import t.lab.guide.dto.point.PointListResponse
import t.lab.guide.dto.point.PointMediaItem
import t.lab.guide.dto.point.PointShortItem
import t.lab.guide.dto.point.command.PointSearchCommand
import t.lab.guide.enums.AdminPointSortField
import t.lab.guide.enums.MediaType
import t.lab.guide.enums.SortDirection
import t.lab.guide.exception.NotFoundException
import t.lab.guide.service.PointService
import java.math.BigDecimal
import java.time.OffsetDateTime

@Service
@Profile("demo")
class MockPointService : PointService {
    override fun searchPoints(request: PointSearchCommand): PointListResponse {
        val categorySlugs = request.categorySlugs
        val maxVisitTime = request.visitTime
        val filterIds =
            if (categorySlugs.isEmpty()) {
                emptyList()
            } else {
                categories.filter { it.slug in categorySlugs }.map { it.id }
            }
        val filtered =
            mock
                .filter { categorySlugs.isEmpty() || it.categoryId in filterIds }
                .filter { maxVisitTime == null || (it.visitTime ?: 0) <= maxVisitTime }
        return PointListResponse(filtered)
    }

    override fun getPointDetail(id: Long): PointDetailResponse =
        mockDetail.firstOrNull { it.id == id }
            ?: throw NotFoundException("Точка с id=$id не найдена")

    override fun getPointsPage(
        page: Int,
        size: Int,
        sortBy: AdminPointSortField?,
        sortDirection: SortDirection?,
        search: String?,
    ): AdminPointPageResponse {
        val totalElements = 247L
        val safePage = page.coerceAtLeast(0)
        val safeSize = size.coerceIn(0, 100)
        val totalPages =
            if (safeSize == 0) {
                0
            } else {
                ((totalElements + safeSize - 1) / safeSize).toInt()
            }
        val from = safePage * safeSize
        val to = minOf((from + safeSize).toLong(), totalElements).toInt()
        val count = (to - from).coerceAtLeast(0)

        val points =
            (0 until count).map { i ->
                val id = (from + i + 1).toLong()
                val category = categories[((id - 1) % categories.size).toInt()]
                val visitTime = 30 + (id % 6).toInt() * 15
                val isActive = id % 11 != 0L
                AdminPointShortItem(
                    id = id,
                    title = "Точка интереса №$id",
                    categoryId = category.id,
                    categoryName = category.name,
                    visitTime = visitTime,
                    isActive = isActive,
                    createdAt = OffsetDateTime.now().minusDays(id),
                )
            }
        return AdminPointPageResponse(
            points = points,
            page = safePage,
            size = safeSize,
            totalElements = totalElements,
            totalPages = totalPages,
        )
    }

    override fun getAdminPointDetail(id: Long): AdminPointDetailResponse {
        val detail = getPointDetail(id)
        val adminMedia =
            detail.media.mapIndexed { i, m ->
                AdminPointMediaItem(
                    id = (i + 1).toLong(),
                    pointId = detail.id,
                    url = m.url,
                    type = m.type,
                    sortOrder = m.sortOrder,
                    createdAt = OffsetDateTime.now().minusDays((i + 1).toLong()),
                )
            }
        return AdminPointDetailResponse(
            id = detail.id,
            title = detail.title,
            description = detail.description,
            shortDescription = detail.shortDescription,
            categoryId = detail.categoryId,
            categoryName = detail.categoryName,
            address = detail.address,
            coordinates = detail.coordinates,
            visitTime = detail.visitTime,
            workingHours = detail.workingHours,
            isActive = true,
            media = adminMedia,
            createdAt = OffsetDateTime.now().minusDays(30),
            updatedAt = OffsetDateTime.now().minusDays(1),
        )
    }

    override fun createPoint(request: AdminCreatePointCommand): AdminPointDetailResponse {
        val newId = mock.size + 1L
        val categoryName =
            categories.firstOrNull { it.id == request.categoryId }?.name
                ?: throw NotFoundException("Категория с id=${request.categoryId} не найдена")
        val now = OffsetDateTime.now()
        return AdminPointDetailResponse(
            id = newId,
            title = request.title,
            description = request.description,
            shortDescription = request.shortDescription,
            categoryId = request.categoryId,
            categoryName = categoryName,
            address = request.address,
            coordinates = request.coordinates.toDto(),
            visitTime = request.visitTime,
            workingHours = request.workingHours,
            isActive = request.isActive,
            media = emptyList(),
            createdAt = now,
            updatedAt = now,
        )
    }

    override fun patchPoint(
        id: Long,
        request: AdminPatchPointRequest,
    ): AdminPointDetailResponse {
        val current = getAdminPointDetail(id)
        val newCategoryId = request.categoryId ?: current.categoryId
        val newCategoryName =
            if (request.categoryId != null) {
                categories.firstOrNull { it.id == request.categoryId }?.name
                    ?: throw NotFoundException("Категория с id=${request.categoryId} не найдена")
            } else {
                current.categoryName
            }
        return AdminPointDetailResponse(
            id = current.id,
            title = request.title ?: current.title,
            description = request.description ?: current.description,
            shortDescription = request.shortDescription ?: current.shortDescription,
            categoryId = newCategoryId,
            categoryName = newCategoryName,
            address = request.address ?: current.address,
            coordinates = request.coordinates ?: current.coordinates,
            visitTime = request.visitTime ?: current.visitTime,
            workingHours = request.workingHours ?: current.workingHours,
            isActive = request.isActive ?: current.isActive,
            media = current.media,
            createdAt = current.createdAt,
            updatedAt = OffsetDateTime.now(),
        )
    }

    override fun deletePoint(id: Long) {
        mockDetail.firstOrNull { it.id == id }
            ?: throw NotFoundException("Точка с id=$id не найдена")
    }

    override fun uploadPointMedia(
        pointId: Long,
        file: MultipartFile,
        request: AdminUploadPointMediaCommand,
    ): AdminPointMediaItem {
        getPointDetail(pointId)
        val fileName = file.originalFilename ?: "upload.bin"
        val mockUrl = "https://cdn.t-guide.mock/points/$pointId/$fileName"
        return AdminPointMediaItem(
            id = System.currentTimeMillis(),
            pointId = pointId,
            url = mockUrl,
            type = request.type,
            sortOrder = request.sortOrder ?: 0,
            createdAt = OffsetDateTime.now(),
        )
    }

    override fun deletePointMedia(
        pointId: Long,
        mediaId: Long,
    ) {
        getPointDetail(pointId)
    }

    companion object {
        private val categories =
            listOf(
                CategoryItem(1L, "Достопримечательности", "attraction"),
                CategoryItem(2L, "Шопинг", "shop"),
                CategoryItem(3L, "Музеи", "museum"),
                CategoryItem(4L, "Парки", "park"),
                CategoryItem(5L, "Еда", "food"),
            )

        private val mock =
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
                    id = 3L,
                    title = "Третьяковская галерея",
                    shortDescription = "Крупнейшее собрание русского искусства",
                    categoryId = 3L,
                    categoryName = "Музеи",
                    coordinates = GeoPoint(BigDecimal("55.741426"), BigDecimal("37.620137")),
                    visitTime = 120,
                ),
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
                    id = 5L,
                    title = "Кафе Пушкинъ",
                    shortDescription = "Ресторан русской кухни в стиле XIX века",
                    categoryId = 5L,
                    categoryName = "Еда",
                    coordinates = GeoPoint(BigDecimal("55.764804"), BigDecimal("37.604392")),
                    visitTime = 90,
                ),
            )

        private val mockDetail =
            listOf(
                PointDetailResponse(
                    id = 1L,
                    title = "Красная площадь",
                    description =
                        "Главная площадь Москвы, расположенная в самом центре города между " +
                            "Московским Кремлём и Китай-городом. Включена в список Всемирного наследия ЮНЕСКО.",
                    shortDescription = "Главная площадь Москвы",
                    categoryId = 1L,
                    categoryName = "Достопримечательности",
                    address = "Москва, Красная площадь",
                    coordinates = GeoPoint(BigDecimal("55.753544"), BigDecimal("37.621202")),
                    visitTime = 60,
                    workingHours = "Круглосуточно",
                    media =
                        listOf(
                            PointMediaItem("https://cdn.t-guide.mock/points/1/photo-1.jpg", MediaType.PHOTO, 0),
                            PointMediaItem("https://cdn.t-guide.mock/points/1/photo-2.jpg", MediaType.PHOTO, 1),
                            PointMediaItem("https://cdn.t-guide.mock/points/1/audio-guide.mp3", MediaType.AUDIO, 2),
                        ),
                ),
                PointDetailResponse(
                    id = 2L,
                    title = "ГУМ",
                    description =
                        "Главный универсальный магазин — крупнейший торговый комплекс Москвы, " +
                            "расположенный на Красной площади. Памятник архитектуры псевдорусского стиля.",
                    shortDescription = "Главный универсальный магазин",
                    categoryId = 2L,
                    categoryName = "Шопинг",
                    address = "Москва, Красная площадь, 3",
                    coordinates = GeoPoint(BigDecimal("55.754713"), BigDecimal("37.621500")),
                    visitTime = 90,
                    workingHours = "10:00 - 22:00",
                    media =
                        listOf(
                            PointMediaItem("https://cdn.t-guide.mock/points/2/photo-1.jpg", MediaType.PHOTO, 0),
                            PointMediaItem("https://cdn.t-guide.mock/points/2/photo-2.jpg", MediaType.PHOTO, 1),
                        ),
                ),
                PointDetailResponse(
                    id = 3L,
                    title = "Третьяковская галерея",
                    description =
                        "Художественный музей в Москве, основанный в 1856 году купцом Павлом Третьяковым. " +
                            "Имеет одну из самых крупных в мире коллекций русского изобразительного искусства.",
                    shortDescription = "Крупнейшее собрание русского искусства",
                    categoryId = 3L,
                    categoryName = "Музеи",
                    address = "Москва, Лаврушинский переулок, 10",
                    coordinates = GeoPoint(BigDecimal("55.741426"), BigDecimal("37.620137")),
                    visitTime = 120,
                    workingHours = "10:00 - 18:00, выходной — понедельник",
                    media =
                        listOf(
                            PointMediaItem("https://cdn.t-guide.mock/points/3/photo-1.jpg", MediaType.PHOTO, 0),
                            PointMediaItem("https://cdn.t-guide.mock/points/3/video-tour.mp4", MediaType.VIDEO, 1),
                            PointMediaItem("https://cdn.t-guide.mock/points/3/audio-guide.mp3", MediaType.AUDIO, 2),
                        ),
                ),
                PointDetailResponse(
                    id = 4L,
                    title = "Парк Горького",
                    description =
                        "Центральный парк культуры и отдыха в Москве. Современное городское пространство " +
                            "с набережной, велодорожками, кафе и лекториями.",
                    shortDescription = "Центральный парк культуры и отдыха",
                    categoryId = 4L,
                    categoryName = "Парки",
                    address = "Москва, ул. Крымский Вал, 9",
                    coordinates = GeoPoint(BigDecimal("55.729812"), BigDecimal("37.601348")),
                    visitTime = 150,
                    workingHours = "Круглосуточно",
                    media =
                        listOf(
                            PointMediaItem("https://cdn.t-guide.mock/points/4/photo-1.jpg", MediaType.PHOTO, 0),
                            PointMediaItem("https://cdn.t-guide.mock/points/4/photo-2.jpg", MediaType.PHOTO, 1),
                            PointMediaItem("https://cdn.t-guide.mock/points/4/video-aerial.mp4", MediaType.VIDEO, 2),
                        ),
                ),
                PointDetailResponse(
                    id = 5L,
                    title = "Кафе Пушкинъ",
                    description =
                        "Знаменитый московский ресторан русской кухни в стиле дворянской усадьбы XIX века. " +
                            "Расположен на Тверском бульваре.",
                    shortDescription = "Ресторан русской кухни в стиле XIX века",
                    categoryId = 5L,
                    categoryName = "Еда",
                    address = "Москва, Тверской бульвар, 26А",
                    coordinates = GeoPoint(BigDecimal("55.764804"), BigDecimal("37.604392")),
                    visitTime = 90,
                    workingHours = "Круглосуточно",
                    media =
                        listOf(
                            PointMediaItem("https://cdn.t-guide.mock/points/5/photo-1.jpg", MediaType.PHOTO, 0),
                            PointMediaItem("https://cdn.t-guide.mock/points/5/photo-interior.jpg", MediaType.PHOTO, 1),
                        ),
                ),
            )
    }
}
