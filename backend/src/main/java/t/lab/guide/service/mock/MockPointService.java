package t.lab.guide.service.mock;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import t.lab.guide.dto.admin.point.AdminCreatePointRequest;
import t.lab.guide.dto.admin.point.AdminPatchPointRequest;
import t.lab.guide.dto.admin.point.AdminPointDetailResponse;
import t.lab.guide.dto.admin.point.AdminPointMediaItem;
import t.lab.guide.dto.admin.point.AdminPointPageResponse;
import t.lab.guide.dto.admin.point.AdminPointShortItem;
import t.lab.guide.dto.admin.point.AdminUploadPointMediaRequest;
import t.lab.guide.dto.common.GeoPoint;
import t.lab.guide.dto.point.PointDetailResponse;
import t.lab.guide.dto.point.PointListResponse;
import t.lab.guide.dto.point.PointMediaItem;
import t.lab.guide.dto.point.PointSearchRequest;
import t.lab.guide.dto.point.PointShortItem;
import t.lab.guide.dto.point.category.CategoryItem;
import t.lab.guide.entity.enums.MediaType;
import t.lab.guide.exception.NotFoundException;
import t.lab.guide.service.PointService;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.IntStream;

@Service
@Profile("demo")
public class MockPointService implements PointService {

    private static final List<CategoryItem> categories = List.of(
            new CategoryItem(1L, "Достопримечательности", "attraction"),
            new CategoryItem(2L, "Шопинг", "shop"),
            new CategoryItem(3L, "Музеи", "museum"),
            new CategoryItem(4L, "Парки", "park"),
            new CategoryItem(5L, "Еда", "food")
    );

    private static final List<PointShortItem> mock = List.of(
            new PointShortItem(1L, "Красная площадь", 1L, "Достопримечательности",
                    new GeoPoint(new BigDecimal("55.753544"), new BigDecimal("37.621202")), 60),
            new PointShortItem(2L, "ГУМ", 2L, "Шопинг",
                    new GeoPoint(new BigDecimal("55.754713"), new BigDecimal("37.621500")), 90),
            new PointShortItem(3L, "Третьяковская галерея", 3L, "Музеи",
                    new GeoPoint(new BigDecimal("55.741426"), new BigDecimal("37.620137")), 120),
            new PointShortItem(4L, "Парк Горького", 4L, "Парки",
                    new GeoPoint(new BigDecimal("55.729812"), new BigDecimal("37.601348")), 150),
            new PointShortItem(5L, "Кафе Пушкинъ", 5L, "Еда",
                    new GeoPoint(new BigDecimal("55.764804"), new BigDecimal("37.604392")), 90)
    );

    private static final List<PointDetailResponse> mockDetail = List.of(
            new PointDetailResponse(
                    1L,
                    "Красная площадь",
                    "Главная площадь Москвы, расположенная в самом центре города между Московским Кремлём и Китай-городом. Включена в список Всемирного наследия ЮНЕСКО.",
                    1L,
                    "Достопримечательности",
                    "Москва, Красная площадь",
                    new GeoPoint(new BigDecimal("55.753544"), new BigDecimal("37.621202")),
                    60,
                    "Круглосуточно",
                    List.of(
                            new PointMediaItem("https://cdn.t-guide.mock/points/1/photo-1.jpg", MediaType.PHOTO, 0),
                            new PointMediaItem("https://cdn.t-guide.mock/points/1/photo-2.jpg", MediaType.PHOTO, 1),
                            new PointMediaItem("https://cdn.t-guide.mock/points/1/audio-guide.mp3", MediaType.AUDIO, 2)
                    )
            ),
            new PointDetailResponse(
                    2L,
                    "ГУМ",
                    "Главный универсальный магазин — крупнейший торговый комплекс Москвы, расположенный на Красной площади. Памятник архитектуры псевдорусского стиля.",
                    2L,
                    "Шопинг",
                    "Москва, Красная площадь, 3",
                    new GeoPoint(new BigDecimal("55.754713"), new BigDecimal("37.621500")),
                    90,
                    "10:00 - 22:00",
                    List.of(
                            new PointMediaItem("https://cdn.t-guide.mock/points/2/photo-1.jpg", MediaType.PHOTO, 0),
                            new PointMediaItem("https://cdn.t-guide.mock/points/2/photo-2.jpg", MediaType.PHOTO, 1)
                    )
            ),
            new PointDetailResponse(
                    3L,
                    "Третьяковская галерея",
                    "Художественный музей в Москве, основанный в 1856 году купцом Павлом Третьяковым. Имеет одну из самых крупных в мире коллекций русского изобразительного искусства.",
                    3L,
                    "Музеи",
                    "Москва, Лаврушинский переулок, 10",
                    new GeoPoint(new BigDecimal("55.741426"), new BigDecimal("37.620137")),
                    120,
                    "10:00 - 18:00, выходной — понедельник",
                    List.of(
                            new PointMediaItem("https://cdn.t-guide.mock/points/3/photo-1.jpg", MediaType.PHOTO, 0),
                            new PointMediaItem("https://cdn.t-guide.mock/points/3/video-tour.mp4", MediaType.VIDEO, 1),
                            new PointMediaItem("https://cdn.t-guide.mock/points/3/audio-guide.mp3", MediaType.AUDIO, 2)
                    )
            ),
            new PointDetailResponse(
                    4L,
                    "Парк Горького",
                    "Центральный парк культуры и отдыха в Москве. Современное городское пространство с набережной, велодорожками, кафе и лекториями.",
                    4L,
                    "Парки",
                    "Москва, ул. Крымский Вал, 9",
                    new GeoPoint(new BigDecimal("55.729812"), new BigDecimal("37.601348")),
                    150,
                    "Круглосуточно",
                    List.of(
                            new PointMediaItem("https://cdn.t-guide.mock/points/4/photo-1.jpg", MediaType.PHOTO, 0),
                            new PointMediaItem("https://cdn.t-guide.mock/points/4/photo-2.jpg", MediaType.PHOTO, 1),
                            new PointMediaItem("https://cdn.t-guide.mock/points/4/video-aerial.mp4", MediaType.VIDEO, 2)
                    )
            ),
            new PointDetailResponse(
                    5L,
                    "Кафе Пушкинъ",
                    "Знаменитый московский ресторан русской кухни в стиле дворянской усадьбы XIX века. Расположен на Тверском бульваре.",
                    5L,
                    "Еда",
                    "Москва, Тверской бульвар, 26А",
                    new GeoPoint(new BigDecimal("55.764804"), new BigDecimal("37.604392")),
                    90,
                    "Круглосуточно",
                    List.of(
                            new PointMediaItem("https://cdn.t-guide.mock/points/5/photo-1.jpg", MediaType.PHOTO, 0),
                            new PointMediaItem("https://cdn.t-guide.mock/points/5/photo-interior.jpg", MediaType.PHOTO, 1)
                    )
            )
    );

    @Override
    public PointListResponse searchPoints(PointSearchRequest request) {
        var categorySlugs = request.categorySlugs();
        var visitTime = request.visitTime();

        var filterIds = categorySlugs == null ? List.<Long>of() : categories.stream()
                .filter(c -> categorySlugs.contains(c.slug()))
                .map(CategoryItem::id)
                .toList();

        var filtered = mock.stream()
                .filter(p -> matchesCategories(p.categoryId(), categorySlugs, filterIds))
                .filter(p -> matchesDuration(p.visitTime(), visitTime))
                .toList();

        return new PointListResponse(filtered);
    }

    private static boolean matchesCategories(Long pointCategoryId, List<String> filterSlugs, List<Long> filterIds) {
        if (filterSlugs == null || filterSlugs.isEmpty()) {
            return true;
        }
        return pointCategoryId != null && filterIds.contains(pointCategoryId);
    }

    private static boolean matchesDuration(Integer pointVisitTime, Integer maxVisitTime) {
        if (maxVisitTime == null) {
            return true;
        }
        return pointVisitTime != null && pointVisitTime <= maxVisitTime;
    }

    @Override
    public PointDetailResponse getPointDetail(Long id) {
        return mockDetail.stream()
                .filter(p -> p.id().equals(id))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Точка с id=" + id + " не найдена"));
    }

    @Override
    public AdminPointPageResponse getPointsPage(int page, int size, String sortBy, String sortDirection, String search){
        long totalElements = 247;
        page = Math.max(page, 0);
        size = Math.min(Math.max(size, 0), 100);
        int totalPages = size == 0 ? 0 : (int) Math.ceil((double) totalElements / size);
        int from = page * size;
        int to = (int) Math.min(from + size, totalElements);
        int count = Math.max(0, to - from);

        List<AdminPointShortItem> points = IntStream.range(0, count)
                .mapToObj(i -> {
                    long id = from + i + 1L;
                    CategoryItem category = categories.get((int) ((id - 1) % categories.size()));
                    Integer visitTime = 30 + (int) ((id % 6) * 15);
                    boolean isActive = id % 11 != 0;
                    return new AdminPointShortItem(
                            id,
                            "Точка интереса №" + id,
                            category.id(),
                            category.name(),
                            visitTime,
                            isActive,
                            OffsetDateTime.now().minusDays(id)
                    );
                })
                .toList();

        return new AdminPointPageResponse(points, page, size, totalElements, totalPages);
    }

    @Override
    public AdminPointDetailResponse getAdminPointDetail(Long id) {
        PointDetailResponse detail = getPointDetail(id);
        List<AdminPointMediaItem> adminMedia = IntStream.range(0, detail.media().size())
                .mapToObj(i -> {
                    PointMediaItem m = detail.media().get(i);
                    return new AdminPointMediaItem(
                            (long) (i + 1),
                            detail.id(),
                            m.url(),
                            m.type(),
                            m.sortOrder(),
                            OffsetDateTime.now().minusDays(i + 1)
                    );
                })
                .toList();
        return AdminPointDetailResponse.builder()
                .id(detail.id())
                .title(detail.title())
                .description(detail.description())
                .categoryId(detail.categoryId())
                .categoryName(detail.categoryName())
                .address(detail.address())
                .coordinates(detail.coordinates())
                .visitTime(detail.visitTime())
                .workingHours(detail.workingHours())
                .isActive(true)
                .media(adminMedia)
                .createdAt(OffsetDateTime.now().minusDays(30))
                .updatedAt(OffsetDateTime.now().minusDays(1))
                .build();
    }

    @Override
    public AdminPointDetailResponse createPoint(AdminCreatePointRequest request) {
        long newId = mock.size() + 1L;
        String categoryName = categories.stream()
                .filter(c -> c.id().equals(request.categoryId()))
                .map(CategoryItem::name)
                .findFirst()
                .orElseThrow(() -> new NotFoundException(
                        "Категория с id=" + request.categoryId() + " не найдена"));
        OffsetDateTime now = OffsetDateTime.now();
        return AdminPointDetailResponse.builder()
                .id(newId)
                .title(request.title())
                .description(request.description())
                .categoryId(request.categoryId())
                .categoryName(categoryName)
                .address(request.address())
                .coordinates(request.coordinates())
                .visitTime(request.visitTime())
                .workingHours(request.workingHours())
                .isActive(request.isActive() == null || request.isActive())
                .media(List.of())
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    @Override
    public AdminPointDetailResponse patchPoint(Long id, AdminPatchPointRequest request) {
        AdminPointDetailResponse current = getAdminPointDetail(id);
        Long categoryId = request.categoryId() != null ? request.categoryId() : current.categoryId();
        String categoryName = request.categoryId() != null
                ? categories.stream()
                    .filter(c -> c.id().equals(request.categoryId()))
                    .map(CategoryItem::name)
                    .findFirst()
                    .orElseThrow(() -> new NotFoundException(
                            "Категория с id=" + request.categoryId() + " не найдена"))
                : current.categoryName();
        return AdminPointDetailResponse.builder()
                .id(current.id())
                .title(request.title() != null ? request.title() : current.title())
                .description(request.description() != null ? request.description() : current.description())
                .categoryId(categoryId)
                .categoryName(categoryName)
                .address(request.address() != null ? request.address() : current.address())
                .coordinates(request.coordinates() != null ? request.coordinates() : current.coordinates())
                .visitTime(request.visitTime() != null ? request.visitTime() : current.visitTime())
                .workingHours(request.workingHours() != null ? request.workingHours() : current.workingHours())
                .isActive(request.isActive() != null ? request.isActive() : current.isActive())
                .media(current.media())
                .createdAt(current.createdAt())
                .updatedAt(OffsetDateTime.now())
                .build();
    }

    @Override
    public void deletePoint(Long id) {
        mockDetail.stream()
                .filter(p -> p.id().equals(id))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Точка с id=" + id + " не найдена"));
    }

    @Override
    public AdminPointMediaItem uploadPointMedia(Long pointId, MultipartFile file, AdminUploadPointMediaRequest request) {
        getPointDetail(pointId);
        String fileName = file != null && file.getOriginalFilename() != null
                ? file.getOriginalFilename()
                : "upload.bin";
        String mockUrl = "https://cdn.t-guide.mock/points/" + pointId + "/" + fileName;
        return new AdminPointMediaItem(
                System.currentTimeMillis(),
                pointId,
                mockUrl,
                request.type(),
                request.sortOrder() != null ? request.sortOrder() : 0,
                OffsetDateTime.now()
        );
    }

    @Override
    public void deletePointMedia(Long pointId, Long mediaId) {
        getPointDetail(pointId);
    }

}