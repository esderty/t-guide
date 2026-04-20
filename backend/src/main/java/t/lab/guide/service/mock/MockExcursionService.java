package t.lab.guide.service.mock;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import t.lab.guide.dto.admin.excursion.AdminCreatePrebuiltExcursionRequest;
import t.lab.guide.dto.admin.excursion.AdminExcursionDetailResponse;
import t.lab.guide.dto.admin.excursion.AdminExcursionPageResponse;
import t.lab.guide.dto.admin.excursion.AdminPatchPrebuiltExcursionRequest;
import t.lab.guide.dto.admin.point.AdminPointShortItem;
import t.lab.guide.dto.common.GeoPoint;
import t.lab.guide.dto.excursion.*;
import t.lab.guide.dto.point.PointListResponse;
import t.lab.guide.dto.point.PointShortItem;
import t.lab.guide.entity.enums.ExcursionRouteType;
import t.lab.guide.entity.enums.ExcursionVisibility;
import t.lab.guide.exception.NotFoundException;
import t.lab.guide.service.ExcursionService;
import t.lab.guide.service.SecurityService;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

@Service
@Profile("demo")
@RequiredArgsConstructor
public class MockExcursionService implements ExcursionService {

    private static final List<ExcursionShortItem> mock = List.of(
            ExcursionShortItem.builder()
                    .id(1L)
                    .routeType(ExcursionRouteType.PREBUILT)
                    .visibility(ExcursionVisibility.PUBLIC)
                    .isOwner(false)
                    .title("Исторический центр Москвы")
                    .description("Прогулка по главным достопримечательностям центра столицы: Красная площадь, ГУМ, Кремль.")
                    .distance(3200)
                    .duration_min(120)
                    .pointCounts(5)
                    .coordinates(new GeoPoint(new BigDecimal("55.753544"), new BigDecimal("37.621202")))
                    .categoryIds(List.of(1L, 2L, 3L))
                    .build(),
            ExcursionShortItem.builder()
                    .id(2L)
                    .routeType(ExcursionRouteType.PREBUILT)
                    .visibility(ExcursionVisibility.PUBLIC)
                    .isOwner(false)
                    .title("Музейный маршрут")
                    .description("Обзор главных московских музеев — от Третьяковки до Пушкинского.")
                    .distance(5400)
                    .duration_min(240)
                    .pointCounts(4)
                    .coordinates(new GeoPoint(new BigDecimal("55.741426"), new BigDecimal("37.620137")))
                    .categoryIds(List.of(3L))
                    .build(),
            ExcursionShortItem.builder()
                    .id(3L)
                    .routeType(ExcursionRouteType.PREBUILT)
                    .visibility(ExcursionVisibility.PUBLIC)
                    .isOwner(false)
                    .title("Парки и набережные")
                    .description("Спокойный маршрут через Парк Горького, Нескучный сад и Воробьёвы горы.")
                    .distance(7800)
                    .duration_min(180)
                    .pointCounts(6)
                    .coordinates(new GeoPoint(new BigDecimal("55.729812"), new BigDecimal("37.601348")))
                    .categoryIds(List.of(4L))
                    .build(),
            ExcursionShortItem.builder()
                    .id(4L)
                    .routeType(ExcursionRouteType.CUSTOM)
                    .visibility(ExcursionVisibility.PUBLIC)
                    .isOwner(true)
                    .title("Гастрономическая Москва")
                    .description("Лучшие кафе и рестораны в пешей доступности от Тверской.")
                    .distance(2100)
                    .duration_min(150)
                    .pointCounts(5)
                    .coordinates(new GeoPoint(new BigDecimal("55.764804"), new BigDecimal("37.604392")))
                    .categoryIds(List.of(5L))
                    .build(),
            ExcursionShortItem.builder()
                    .id(5L)
                    .routeType(ExcursionRouteType.CUSTOM)
                    .visibility(ExcursionVisibility.PRIVATE)
                    .isOwner(true)
                    .title("Шопинг и архитектура")
                    .description("ГУМ, ЦУМ, Петровский пассаж — торговая классика и модерн.")
                    .distance(1800)
                    .duration_min(90)
                    .pointCounts(3)
                    .coordinates(new GeoPoint(new BigDecimal("55.757500"), new BigDecimal("37.618000")))
                    .categoryIds(List.of(1L, 2L))
                    .build()
    );
    private static final List<ExcursionDetailResponse> mockDetail = List.of(
            ExcursionDetailResponse.builder()
                    .id(1L)
                    .routeType(ExcursionRouteType.PREBUILT)
                    .visibility(ExcursionVisibility.PUBLIC)
                    .isOwner(false)
                    .title("Исторический центр Москвы")
                    .description("Прогулка по главным достопримечательностям центра столицы: Красная площадь, ГУМ, Кремль.")
                    .distance(3200)
                    .duration(120)
                    .coordinates(new GeoPoint(new BigDecimal("55.753544"), new BigDecimal("37.621202")))
                    .points(new PointListResponse(List.of(
                            new PointShortItem(1L, "Красная площадь", 1L, "Достопримечательности",
                                    new GeoPoint(new BigDecimal("55.753544"), new BigDecimal("37.621202")), 60),
                            new PointShortItem(2L, "ГУМ", 2L, "Шопинг",
                                    new GeoPoint(new BigDecimal("55.754713"), new BigDecimal("37.621500")), 90),
                            new PointShortItem(6L, "Московский Кремль", 1L, "Достопримечательности",
                                    new GeoPoint(new BigDecimal("55.751999"), new BigDecimal("37.617734")), 120)
                    )))
                    .build(),
            ExcursionDetailResponse.builder()
                    .id(2L)
                    .routeType(ExcursionRouteType.PREBUILT)
                    .visibility(ExcursionVisibility.PUBLIC)
                    .isOwner(false)
                    .title("Музейный маршрут")
                    .description("Обзор главных московских музеев — от Третьяковки до Пушкинского.")
                    .distance(5400)
                    .duration(240)
                    .coordinates(new GeoPoint(new BigDecimal("55.741426"), new BigDecimal("37.620137")))
                    .points(new PointListResponse(List.of(
                            new PointShortItem(3L, "Третьяковская галерея", 3L, "Музеи",
                                    new GeoPoint(new BigDecimal("55.741426"), new BigDecimal("37.620137")), 120),
                            new PointShortItem(7L, "Пушкинский музей", 3L, "Музеи",
                                    new GeoPoint(new BigDecimal("55.747524"), new BigDecimal("37.605068")), 120)
                    )))
                    .build(),
            ExcursionDetailResponse.builder()
                    .id(3L)
                    .routeType(ExcursionRouteType.PREBUILT)
                    .visibility(ExcursionVisibility.PUBLIC)
                    .isOwner(false)
                    .title("Парки и набережные")
                    .description("Спокойный маршрут через Парк Горького, Нескучный сад и Воробьёвы горы.")
                    .distance(7800)
                    .duration(180)
                    .coordinates(new GeoPoint(new BigDecimal("55.729812"), new BigDecimal("37.601348")))
                    .points(new PointListResponse(List.of(
                            new PointShortItem(4L, "Парк Горького", 4L, "Парки",
                                    new GeoPoint(new BigDecimal("55.729812"), new BigDecimal("37.601348")), 150),
                            new PointShortItem(8L, "Нескучный сад", 4L, "Парки",
                                    new GeoPoint(new BigDecimal("55.716610"), new BigDecimal("37.589569")), 60),
                            new PointShortItem(9L, "Воробьёвы горы", 4L, "Парки",
                                    new GeoPoint(new BigDecimal("55.710484"), new BigDecimal("37.548517")), 90)
                    )))
                    .build(),
            ExcursionDetailResponse.builder()
                    .id(4L)
                    .routeType(ExcursionRouteType.CUSTOM)
                    .visibility(ExcursionVisibility.PUBLIC)
                    .isOwner(true)
                    .title("Гастрономическая Москва")
                    .description("Лучшие кафе и рестораны в пешей доступности от Тверской.")
                    .distance(2100)
                    .duration(150)
                    .coordinates(new GeoPoint(new BigDecimal("55.764804"), new BigDecimal("37.604392")))
                    .points(new PointListResponse(List.of(
                            new PointShortItem(5L, "Кафе Пушкинъ", 5L, "Еда",
                                    new GeoPoint(new BigDecimal("55.764804"), new BigDecimal("37.604392")), 90),
                            new PointShortItem(10L, "White Rabbit", 5L, "Еда",
                                    new GeoPoint(new BigDecimal("55.749222"), new BigDecimal("37.583688")), 120)
                    )))
                    .build(),
            ExcursionDetailResponse.builder()
                    .id(5L)
                    .routeType(ExcursionRouteType.CUSTOM)
                    .visibility(ExcursionVisibility.PRIVATE)
                    .isOwner(true)
                    .title("Шопинг и архитектура")
                    .description("ГУМ, ЦУМ, Петровский пассаж — торговая классика и модерн.")
                    .distance(1800)
                    .duration(90)
                    .coordinates(new GeoPoint(new BigDecimal("55.757500"), new BigDecimal("37.618000")))
                    .points(new PointListResponse(List.of(
                            new PointShortItem(2L, "ГУМ", 2L, "Шопинг",
                                    new GeoPoint(new BigDecimal("55.754713"), new BigDecimal("37.621500")), 90),
                            new PointShortItem(11L, "ЦУМ", 2L, "Шопинг",
                                    new GeoPoint(new BigDecimal("55.760359"), new BigDecimal("37.620918")), 60),
                            new PointShortItem(12L, "Петровский пассаж", 2L, "Шопинг",
                                    new GeoPoint(new BigDecimal("55.762103"), new BigDecimal("37.619614")), 45)
                    )))
                    .build()
    );
    private final SecurityService securityService;

    private static boolean matchesCategories (List<Long> excursionCategories, List<Long> filterCategories) {
        if (filterCategories == null || filterCategories.isEmpty()) {
            return true;
        }
        if (excursionCategories == null || excursionCategories.isEmpty()) {
            return false;
        }
        return excursionCategories.stream().anyMatch(filterCategories::contains);
    }

    private static boolean matchesDuration (Integer excursionDuration, Integer maxDuration) {
        if (maxDuration == null) {
            return true;
        }
        return excursionDuration != null && excursionDuration <= maxDuration;
    }

    @Override
    public ExcursionListResponse searchExcursions (ExcursionSearchRequest request) {
        // Экскурсии с PREBUILT маршрутом и PUBLIC видимостью
        var categoryIds = request.categoryIds();
        var visitTime = request.visitTime();

        var filtered = mock.stream()
                .filter(e -> matchesCategories(e.categoryIds(), categoryIds))
                .filter(e -> matchesDuration(e.duration_min(), visitTime))
                .toList();

        return new ExcursionListResponse(filtered);
    }

    @Override
    public ExcursionDetailResponse getExcursionDetail (Long id) {
        // Проверка, что это PREBUILT + PUBLIC, CUSTOM + PUBLIC, CUSTOM + пользователь owner
        return mockDetail.stream()
                .filter(e -> e.id().equals(id))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Экскурсия с id=" + id + " не найдена"));
    }

    @Override
    public ExcursionDetailResponse createCustomExcursion (CreateCustomExcursionRequest request) {
        Long userId = securityService.getCurrentUserId();
        // Валидировать существование точек, а так же правильность заполнения order(с 1 до n без пропусков, дубликаты)
        ExcursionDetailResponse template = mockDetail.get(0);
        return ExcursionDetailResponse.builder()
                .id(100L)
                .routeType(ExcursionRouteType.CUSTOM)
                .visibility(ExcursionVisibility.PRIVATE)
                .isOwner(true)
                .title(request.title())
                .description(request.description())
                .distance(template.distance())
                .duration(template.duration())
                .coordinates(template.coordinates())
                .points(template.points())
                .build();
    }

    @Override
    public ExcursionShortItem updateCustomExcursion (Long id, UpdateCustomExcursionRequest request) {
        Long userId = securityService.getCurrentUserId();
        //Проверяем что excursion.ownerId == userId | Если нет, то кидаем ошибку доступа, если да, то обновляем экскурсию
        ExcursionShortItem template = mock.get(0);
        return ExcursionShortItem.builder()
                .id(id)
                .routeType(ExcursionRouteType.CUSTOM)
                .visibility(request.visibility() != null ? request.visibility() : ExcursionVisibility.PRIVATE)
                .isOwner(true)
                .title(request.title() != null ? request.title() : template.title())
                .description(request.description() != null ? request.description() : template.description())
                .distance(template.distance())
                .duration_min(template.duration_min())
                .pointCounts(template.pointCounts())
                .coordinates(template.coordinates())
                .categoryIds(template.categoryIds())
                .build();
    }

    @Override
    public ExcursionDetailResponse setExcursionPoints (Long id, SetExcursionPointsRequest request) {
        Long userId = securityService.getCurrentUserId();
        // Проверяем что excursion.ownerId == userId | Если нет, то кидаем ошибку доступа, если да, то обновляем точки экскурсии
        // Валидировать существование точек, а так же правильность заполнения order(с 1 до n без пропусков, дубликаты)
        ExcursionDetailResponse template = mockDetail.get(0);
        return ExcursionDetailResponse.builder()
                .id(id)
                .routeType(ExcursionRouteType.CUSTOM)
                .visibility(ExcursionVisibility.PRIVATE)
                .isOwner(true)
                .title(template.title())
                .description(template.description())
                .distance(template.distance())
                .duration(template.duration())
                .coordinates(template.coordinates())
                .points(template.points())
                .build();
    }

    @Override
    public void deleteCustomExcursion (Long id) {
        Long userId = securityService.getCurrentUserId();
        //Проверяем что excursion.ownerId == userId | Если нет, то кидаем ошибку доступа, если да, то удаляем экскурсию
    }

    @Override
    public void favoriteExcursion (Long excursionId) {
        Long userId = securityService.getCurrentUserId();
        //Сейвим связь пользователя с экскурсией в БД
    }

    @Override
    public void unfavoriteExcursion (Long excursionId) {
        Long userId = securityService.getCurrentUserId();
        // TODO ???Проверяем что есть связь | Если нет связи, то окей или error???, убираем связь пользователя с экскурсией
    }

    @Override
    public AdminExcursionPageResponse getAdminExcursionsPage (int page, int size, String sortBy, String sortDirection, String search) {
        // Применяем текстовый поиск по title (без учёта регистра)
        var filtered = mock.stream()
                .filter(e -> search == null || search.isBlank()
                        || e.title().toLowerCase().contains(search.toLowerCase()))
                .toList();

        int totalElements = filtered.size();
        int totalPages = size > 0 ? (int) Math.ceil((double) totalElements / size) : 0;

        int fromIndex = Math.min(page * size, totalElements);
        int toIndex = Math.min(fromIndex + size, totalElements);
        List<ExcursionShortItem> pageContent = filtered.subList(fromIndex, toIndex);

        return new AdminExcursionPageResponse(pageContent, page, size, (long) totalElements, totalPages);
    }

    @Override
    public AdminExcursionDetailResponse getAdminExcursionDetail (Long id) {
        ExcursionDetailResponse existing = getExcursionDetail(id);
        return toAdminDetail(existing);
    }

    @Override
    public AdminExcursionDetailResponse createPrebuiltExcursion (AdminCreatePrebuiltExcursionRequest request) {
        // Валидировать существование точек, правильность заполнения order (с 1 до n без пропусков, дубликатов)
        ExcursionDetailResponse template = mockDetail.get(0);
        OffsetDateTime now = OffsetDateTime.now();
        return AdminExcursionDetailResponse.builder()
                .id(100L)
                .ownerId(null)
                .routeType(ExcursionRouteType.PREBUILT)
                .visibility(request.visibility() != null ? request.visibility() : ExcursionVisibility.PUBLIC)
                .title(request.title())
                .description(request.description())
                .distance(template.distance())
                .durationMin(template.duration())
                .coordinates(template.coordinates())
                .points(toAdminPoints(template.points()))
                .createdBy(1L)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    @Override
    public AdminExcursionDetailResponse patchPrebuiltExcursion (Long id, AdminPatchPrebuiltExcursionRequest request) {
        ExcursionDetailResponse existing = getExcursionDetail(id);
        OffsetDateTime now = OffsetDateTime.now();
        return AdminExcursionDetailResponse.builder()
                .id(existing.id())
                .ownerId(null)
                .routeType(ExcursionRouteType.PREBUILT)
                .visibility(request.visibility() != null ? request.visibility() : existing.visibility())
                .title(request.title() != null ? request.title() : existing.title())
                .description(request.description() != null ? request.description() : existing.description())
                .distance(existing.distance())
                .durationMin(existing.duration())
                .coordinates(existing.coordinates())
                .points(toAdminPoints(existing.points()))
                .createdBy(1L)
                .createdAt(now.minusDays(30))
                .updatedAt(now)
                .build();
    }

    @Override
    public AdminExcursionDetailResponse setAdminExcursionPoints (Long id, SetExcursionPointsRequest request) {
        // Валидировать существование точек, правильность заполнения order (с 1 до n без пропусков, дубликатов)
        ExcursionDetailResponse existing = getExcursionDetail(id);
        OffsetDateTime now = OffsetDateTime.now();
        return AdminExcursionDetailResponse.builder()
                .id(existing.id())
                .ownerId(null)
                .routeType(ExcursionRouteType.PREBUILT)
                .visibility(existing.visibility())
                .title(existing.title())
                .description(existing.description())
                .distance(existing.distance())
                .durationMin(existing.duration())
                .coordinates(existing.coordinates())
                .points(toAdminPoints(existing.points()))
                .createdBy(1L)
                .createdAt(now.minusDays(30))
                .updatedAt(now)
                .build();
    }

    private AdminExcursionDetailResponse toAdminDetail (ExcursionDetailResponse source) {
        OffsetDateTime now = OffsetDateTime.now();
        return AdminExcursionDetailResponse.builder()
                .id(source.id())
                .ownerId(source.routeType() == ExcursionRouteType.PREBUILT ? null : 42L)
                .routeType(source.routeType())
                .visibility(source.visibility())
                .title(source.title())
                .description(source.description())
                .distance(source.distance())
                .durationMin(source.duration())
                .coordinates(source.coordinates())
                .points(toAdminPoints(source.points()))
                .createdBy(1L)
                .createdAt(now.minusDays(30))
                .updatedAt(now)
                .build();
    }

    @Override
    public void deleteExcursion (Long id) {
        // Проверяем, что экскурсия существует, удаляем с каскадом точек маршрута
        mockDetail.stream()
                .filter(e -> e.id().equals(id))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Экскурсия с id=" + id + " не найдена"));
    }

    private List<AdminPointShortItem> toAdminPoints (PointListResponse source) {
        if (source == null || source.points() == null) {
            return List.of();
        }
        return source.points().stream()
                .map(this::toAdminPoint)
                .toList();
    }

    private AdminPointShortItem toAdminPoint (PointShortItem source) {
        return new AdminPointShortItem(
                source.id(),
                source.title(),
                source.categoryId(),
                source.categoryName(),
                source.visitTime(),
                true,
                OffsetDateTime.now().minusDays(30)
        );
    }
}