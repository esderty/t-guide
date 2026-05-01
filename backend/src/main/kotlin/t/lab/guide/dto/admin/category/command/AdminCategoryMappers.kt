package t.lab.guide.dto.admin.category.command

import t.lab.guide.dto.admin.category.AdminCreateCategoryRequest

fun AdminCreateCategoryRequest.toCommand(): AdminCreateCategoryCommand =
    AdminCreateCategoryCommand(
        name = requireNotNull(this.name) { "name not validated" },
        slug = requireNotNull(this.slug) { "slug not validated" },
    )
