package t.lab.guide.dto.excursion.command

import t.lab.guide.dto.common.command.toCommand
import t.lab.guide.dto.excursion.CreateCustomExcursionRequest
import t.lab.guide.dto.excursion.ExcursionAddReviewRequest
import t.lab.guide.dto.excursion.ExcursionPointOrderItem
import t.lab.guide.dto.excursion.ExcursionSearchRequest
import t.lab.guide.dto.excursion.SetExcursionPointsRequest
import t.lab.guide.enums.ExcursionVisibility
import t.lab.guide.exception.BadRequestException

fun CreateCustomExcursionRequest.toCommand(): CreateCustomExcursionCommand =
    CreateCustomExcursionCommand(
        title = this.title ?: throw BadRequestException("Поле 'title' обязательно"),
        description = this.description,
        shortDescription = this.description,
        visibility = ExcursionVisibility.valueOf(this.visibility ?: throw BadRequestException("Поле 'visibility' обязательно")),
        points = this.points?.map { it.toCommand() } ?: throw BadRequestException("Поле 'points' обязательно"),
    )

fun ExcursionSearchRequest.toCommand(): ExcursionSearchCommand =
    ExcursionSearchCommand(
        location = this.location?.toCommand() ?: throw BadRequestException("Поле 'location' обязательно"),
        radiusKilometers = this.radiusKilometers ?: throw BadRequestException("Поле 'radiusKilometers' обязательно"),
        categoryIds = this.categoryIds,
        visitTime = this.visitTime,
    )

fun ExcursionPointOrderItem.toCommand(): ExcursionPointOrderItemCommand =
    ExcursionPointOrderItemCommand(
        pointId = this.pointId ?: throw BadRequestException("Поле 'pointId' обязательно"),
        order = this.order ?: throw BadRequestException("Поле 'order' обязательно"),
    )

fun SetExcursionPointsRequest.toCommand(): SetExcursionPointsCommand =
    SetExcursionPointsCommand(
        points = this.points?.map { it.toCommand() } ?: throw BadRequestException("Поле 'points' обязательно"),
    )

fun ExcursionAddReviewRequest.toCommand(): ExcursionAddReviewCommand =
    ExcursionAddReviewCommand(
        rating = this.rating ?: throw BadRequestException("Поле 'rating' обязательно"),
        reviewText = this.reviewText,
        visitDate = this.visitDate ?: throw BadRequestException("Поле 'visitDate' обязательно"),
    )
