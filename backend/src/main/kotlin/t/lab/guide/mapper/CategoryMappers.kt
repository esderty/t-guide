package t.lab.guide.mapper

import t.lab.guide.domain.PointCategory
import t.lab.guide.dto.admin.category.command.AdminCreateCategoryCommand
import t.lab.guide.dto.category.CategoryItem

fun AdminCreateCategoryCommand.toCategory(): PointCategory =
    PointCategory(
        id = null,
        name = this.name,
        slug = this.slug,
    )

fun PointCategory.toCategoryItem(): CategoryItem =
    CategoryItem(
        id = this.id!!,
        name = this.name,
        slug = this.slug,
    )
