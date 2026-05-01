package t.lab.guide.service.mock

import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service
import t.lab.guide.dto.admin.category.AdminPatchCategoryRequest
import t.lab.guide.dto.admin.category.command.AdminCreateCategoryCommand
import t.lab.guide.dto.category.CategoryItem
import t.lab.guide.dto.category.CategoryListResponse
import t.lab.guide.exception.NotFoundException
import t.lab.guide.service.PointCategoryService

@Service
@Profile("demo")
class MockPointCategoryService : PointCategoryService {
    override fun getAllCategories(): CategoryListResponse = CategoryListResponse(categories)

    override fun createCategory(request: AdminCreateCategoryCommand): CategoryItem {
        val newId = categories.size + 1L
        return CategoryItem(newId, request.name, request.slug)
    }

    override fun patchCategory(
        id: Long,
        request: AdminPatchCategoryRequest,
    ): CategoryItem {
        val current =
            categories.firstOrNull { it.id == id }
                ?: throw NotFoundException("Категория с id=$id не найдена")
        return CategoryItem(
            id = current.id,
            name = request.name ?: current.name,
            slug = request.slug ?: current.slug,
        )
    }

    override fun deleteCategory(id: Long) {
        categories.firstOrNull { it.id == id }
            ?: throw NotFoundException("Категория с id=$id не найдена")
    }

    companion object {
        private val categories =
            listOf(
                CategoryItem(1L, "Достопримечательности", "attraction"),
                CategoryItem(2L, "Шопинг", "shop"),
                CategoryItem(3L, "Музеи", "museum"),
                CategoryItem(4L, "Парки", "park"),
                CategoryItem(5L, "Еда", "food"),
            )
    }
}
