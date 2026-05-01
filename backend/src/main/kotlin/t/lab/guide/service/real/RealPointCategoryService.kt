package t.lab.guide.service.real

import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service
import t.lab.guide.dto.admin.category.AdminPatchCategoryRequest
import t.lab.guide.dto.admin.category.command.AdminCreateCategoryCommand
import t.lab.guide.dto.category.CategoryItem
import t.lab.guide.dto.category.CategoryListResponse
import t.lab.guide.service.PointCategoryService

@Service
@Profile("!demo")
class RealPointCategoryService : PointCategoryService {
    override fun getAllCategories(): CategoryListResponse {
        TODO("Not yet implemented")
    }

    override fun createCategory(request: AdminCreateCategoryCommand): CategoryItem {
        TODO("Not yet implemented")
    }

    override fun patchCategory(
        id: Long,
        request: AdminPatchCategoryRequest,
    ): CategoryItem {
        TODO("Not yet implemented")
    }

    override fun deleteCategory(id: Long) {
        TODO("Not yet implemented")
    }
}
