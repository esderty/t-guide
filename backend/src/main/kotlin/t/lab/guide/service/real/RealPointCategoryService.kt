package t.lab.guide.service.real

import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service
import t.lab.guide.dto.admin.category.AdminCreateCategoryRequest
import t.lab.guide.dto.admin.category.AdminPatchCategoryRequest
import t.lab.guide.dto.point.category.CategoryItem
import t.lab.guide.dto.point.category.CategoryListResponse
import t.lab.guide.service.PointCategoryService

@Service
@Profile("!demo")
class RealPointCategoryService : PointCategoryService {
    override fun getAllCategories(): CategoryListResponse {
        TODO("Not yet implemented")
    }

    override fun createCategory(request: AdminCreateCategoryRequest): CategoryItem {
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
