package t.lab.guide.dto.admin.category.command

import t.lab.guide.dto.admin.category.AdminCreateCategoryRequest
import t.lab.guide.exception.BadRequestException

fun AdminCreateCategoryRequest.toCommand(): AdminCreateCategoryCommand =
    AdminCreateCategoryCommand(
        name = this.name ?: throw BadRequestException("Поле 'name' обязательно"),
        slug = this.slug ?: throw BadRequestException("Поле 'slug' обязательно"),
    )
