package t.lab.guide.service

import t.lab.guide.dto.admin.category.AdminPatchCategoryRequest
import t.lab.guide.dto.admin.category.command.AdminCreateCategoryCommand
import t.lab.guide.dto.category.CategoryItem
import t.lab.guide.dto.category.CategoryListResponse

interface PointCategoryService {
    fun getAllCategories(): CategoryListResponse

    fun createCategory(request: AdminCreateCategoryCommand): CategoryItem

    fun patchCategory(
        id: Long,
        request: AdminPatchCategoryRequest,
    ): CategoryItem

    fun deleteCategory(id: Long)

// TODO когда будут entity(model), добавить сервисные методы
}
