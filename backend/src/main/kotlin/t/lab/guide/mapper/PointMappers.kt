package t.lab.guide.mapper

import org.springframework.data.jdbc.core.mapping.AggregateReference
import t.lab.guide.domain.Point
import t.lab.guide.domain.PointMedia
import t.lab.guide.dto.admin.point.AdminPointDetailResponse
import t.lab.guide.dto.admin.point.AdminPointMediaItem
import t.lab.guide.dto.admin.point.AdminPointShortItem
import t.lab.guide.dto.admin.point.command.AdminCreatePointCommand
import t.lab.guide.dto.common.GeoPoint
import t.lab.guide.dto.point.PointDetailResponse
import t.lab.guide.dto.point.PointMediaItem
import t.lab.guide.dto.point.PointShortItem
import t.lab.guide.repository.view.point.AdminPointDetailView
import t.lab.guide.repository.view.point.AdminPointShortView
import t.lab.guide.repository.view.point.PointDetailView
import t.lab.guide.repository.view.point.PointShortView

fun PointShortView.toPointShortItem(): PointShortItem =
    PointShortItem(
        id = this.id,
        title = this.title,
        shortDescription = this.shortDescription,
        categoryId = this.categoryId,
        categoryName = this.categoryName,
        coordinates = GeoPoint(this.latitude.toBigDecimal(), this.longitude.toBigDecimal()),
        visitTime = this.visitTime,
    )

fun PointMedia.toPointMediaItem(domain: String): PointMediaItem =
    PointMediaItem(
        url = "$domain/${this.objectKey}",
        type = this.mediaType,
        sortOrder = this.sortOrder,
    )

fun PointDetailView.toPointDetailResponse(media: List<PointMediaItem>): PointDetailResponse =
    PointDetailResponse(
        id = this.id,
        title = this.title,
        description = this.description,
        shortDescription = this.shortDescription,
        categoryId = this.categoryId,
        categoryName = this.categoryName,
        address = this.address,
        coordinates = GeoPoint(this.latitude.toBigDecimal(), this.longitude.toBigDecimal()),
        visitTime = this.visitTime,
        workingHours = this.workingHours,
        media = media,
    )

fun AdminPointShortView.toAdminPointShortItem(): AdminPointShortItem =
    AdminPointShortItem(
        id = this.id,
        title = this.title,
        categoryId = this.categoryId,
        categoryName = this.categoryName,
        visitTime = this.visitTime,
        isActive = this.isActive,
        createdAt = this.createdAt,
    )

fun PointMedia.toAdminPointMediaItem(domain: String): AdminPointMediaItem =
    AdminPointMediaItem(
        id = this.id!!,
        url = "$domain/${this.objectKey}",
        type = this.mediaType,
        sortOrder = this.sortOrder,
        createdAt = this.createdAt!!,
    )

fun AdminPointDetailView.toAdminPointDetailResponse(media: List<AdminPointMediaItem>): AdminPointDetailResponse =
    AdminPointDetailResponse(
        id = this.id,
        title = this.title,
        description = this.description,
        shortDescription = this.shortDescription,
        categoryId = this.categoryId,
        categoryName = this.categoryName,
        address = this.address,
        media = media,
        coordinates = GeoPoint(this.latitude.toBigDecimal(), this.longitude.toBigDecimal()),
        visitTime = this.visitTime,
        workingHours = this.workingHours,
        isActive = this.isActive,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt,
    )

fun AdminCreatePointCommand.toPoint(): Point =
    Point(
        title = this.title,
        description = this.description,
        shortDescription = this.shortDescription,
        category = AggregateReference.to(this.categoryId),
        address = this.address,
        latitude = this.coordinates.latitude.toDouble(),
        longitude = this.coordinates.longitude.toDouble(),
        visitTimeMin = this.visitTime,
        workingHours = this.workingHours,
        isActive = this.isActive,
    )

fun Point.toAdminPointDetailResponseWithoutMedia(media: List<AdminPointMediaItem>, categoryName: String): AdminPointDetailResponse =
    AdminPointDetailResponse(
        id = this.id!!,
        title = this.title,
        description = this.description,
        shortDescription = this.shortDescription,
        categoryId = this.category!!.id,
        categoryName = categoryName,
        address = this.address,
        media = media,
        coordinates = GeoPoint(this.latitude.toBigDecimal(), this.longitude.toBigDecimal()),
        visitTime = this.visitTimeMin,
        workingHours = this.workingHours,
        isActive = this.isActive,
        createdAt = this.createdAt!!,
        updatedAt = this.updatedAt!!,
    )
