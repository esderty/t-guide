package t.lab.guide.mapper

import org.springframework.data.jdbc.core.mapping.AggregateReference
import t.lab.guide.domain.Excursion
import t.lab.guide.dto.admin.excursion.AdminExcursionDetailResponse
import t.lab.guide.dto.admin.excursion.command.AdminCreatePrebuiltExcursionCommand
import t.lab.guide.dto.common.GeoPoint
import t.lab.guide.dto.excursion.ExcursionDetailResponse
import t.lab.guide.dto.excursion.ExcursionListResponse
import t.lab.guide.dto.excursion.ExcursionShortItem
import t.lab.guide.dto.excursion.command.CreateCustomExcursionCommand
import t.lab.guide.dto.point.PointListResponse
import t.lab.guide.enums.ExcursionRouteType
import t.lab.guide.repository.view.excursion.AdminExcursionDetailView
import t.lab.guide.repository.view.excursion.AdminExcursionShortView
import t.lab.guide.repository.view.excursion.ExcursionShortView
import t.lab.guide.repository.view.point.AdminPointShortView
import t.lab.guide.repository.view.point.PointShortView

fun ExcursionShortView.toExcursionShortItem(userId: Long?): ExcursionShortItem =
    ExcursionShortItem(
        id = this.id,
        routeType = this.routeType,
        visibility = this.visibility,
        isOwner = userId != null && this.ownerId == userId,
        title = this.title,
        description = this.description,
        shortDescription = this.shortDescription,
        distance = this.distance,
        durationMin = this.durationMin,
        pointsCount = this.pointsCount,
        coordinates =
            GeoPoint(
                latitude = this.latitude.toBigDecimal(),
                longitude = this.longitude.toBigDecimal(),
            ),
        categoryIds = categoryIds,
        rating = this.rating,
        reviewsCount = this.reviewsCount,
    )

fun AdminExcursionShortView.toExcursionShortItem(userId: Long?): ExcursionShortItem =
    ExcursionShortItem(
        id = this.id,
        routeType = this.routeType,
        visibility = this.visibility,
        isOwner = userId != null && this.ownerId == userId,
        title = this.title,
        description = this.description,
        shortDescription = this.shortDescription,
        distance = this.distance,
        durationMin = this.durationMin,
        pointsCount = this.pointsCount,
        coordinates =
            GeoPoint(
                latitude = this.latitude?.toBigDecimal(),
                longitude = this.longitude?.toBigDecimal(),
            ),
        categoryIds = categoryIds,
        rating = this.rating,
        reviewsCount = this.reviewsCount,
    )

fun List<ExcursionShortItem>.toExcursionListResponse(): ExcursionListResponse =
    ExcursionListResponse(
        this,
    )

fun ExcursionShortView.toExcursionShortItemDetail(
    currentUserId: Long?,
    points: List<PointShortView>,
): ExcursionShortItem =
    ExcursionShortItem(
        id = this.id,
        routeType = this.routeType,
        visibility = this.visibility,
        isOwner = currentUserId != null && this.ownerId == currentUserId,
        title = this.title,
        description = this.description,
        shortDescription = this.shortDescription,
        distance = this.distance,
        durationMin = this.durationMin,
        pointsCount = points.size,
        coordinates =
            GeoPoint(
                latitude = this.latitude.toBigDecimal(),
                longitude = this.longitude.toBigDecimal(),
            ),
        categoryIds = points.map { it.categoryId }.distinct(),
        rating = this.rating,
        reviewsCount = this.reviewsCount,
    )

fun CreateCustomExcursionCommand.toExcursion(
    ownerId: Long,
    distance: Int,
    durationMin: Int,
): Excursion =
    Excursion(
        owner = AggregateReference.to(ownerId),
        routeType = ExcursionRouteType.CUSTOM,
        visibility = this.visibility,
        title = this.title,
        description = this.description,
        shortDescription = this.shortDescription,
        distance = distance,
        durationMin = durationMin,
        createdBy = AggregateReference.to(ownerId),
    )

fun AdminCreatePrebuiltExcursionCommand.toExcursion(
    createdById: Long,
    distance: Int,
    durationMin: Int,
): Excursion =
    Excursion(
        owner = null,
        routeType = ExcursionRouteType.PREBUILT,
        visibility = this.visibility,
        title = this.title,
        description = this.description,
        shortDescription = this.shortDescription,
        distance = distance,
        durationMin = durationMin,
        createdBy = AggregateReference.to(createdById),
    )

fun ExcursionShortView.toExcursionDetailResponse(
    currentUserId: Long?,
    points: List<PointShortView>,
): ExcursionDetailResponse =
    ExcursionDetailResponse(
        id = this.id,
        routeType = this.routeType,
        visibility = this.visibility,
        isOwner = currentUserId != null && this.ownerId == currentUserId,
        title = this.title,
        description = this.description,
        shortDescription = this.shortDescription,
        distance = this.distance,
        durationMin = this.durationMin,
        coordinates =
            GeoPoint(
                latitude = this.latitude.toBigDecimal(),
                longitude = this.longitude.toBigDecimal(),
            ),
        points = PointListResponse(points.map { it.toPointShortItem() }),
        rating = this.rating,
        reviewsCount = this.reviewsCount,
    )

fun AdminExcursionDetailView.toAdminExcursionDetailResponse(
    points: List<AdminPointShortView>,
): AdminExcursionDetailResponse =
    AdminExcursionDetailResponse(
        id = this.id,
        routeType = this.routeType,
        visibility = this.visibility,
        ownerId = this.ownerId,
        title = this.title,
        description = this.description,
        shortDescription = this.shortDescription,
        distance = this.distance,
        durationMin = this.durationMin,
        coordinates =
            GeoPoint(
                latitude = this.latitude?.toBigDecimal(),
                longitude = this.longitude?.toBigDecimal(),
            ),
        points = points.map { it.toAdminPointShortItem() },
        createdBy = this.createdBy,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt,
        rating = this.rating,
        reviewsCount = this.reviewsCount,
    )
