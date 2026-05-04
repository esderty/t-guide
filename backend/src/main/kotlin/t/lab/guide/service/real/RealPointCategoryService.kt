package t.lab.guide.service.real

import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service
import t.lab.guide.domain.PointCategory
import t.lab.guide.dto.admin.category.AdminPatchCategoryRequest
import t.lab.guide.dto.admin.category.command.AdminCreateCategoryCommand
import t.lab.guide.dto.category.CategoryItem
import t.lab.guide.dto.category.CategoryListResponse
import t.lab.guide.exception.ConflictException
import t.lab.guide.exception.NotFoundException
import t.lab.guide.mapper.toCategory
import t.lab.guide.mapper.toCategoryItem
import t.lab.guide.repository.PointCategoryRepository
import t.lab.guide.service.PointCategoryService

@Service
@Profile("!demo")
class RealPointCategoryService(
    private val pointCategoryRepository: PointCategoryRepository,
) : PointCategoryService {
    override fun getAllCategories(): CategoryListResponse {
        val categories = pointCategoryRepository.findAll()
        return CategoryListResponse(categories.map { it.toCategoryItem() })
    }

    override fun createCategory(request: AdminCreateCategoryCommand): CategoryItem {
        if (pointCategoryRepository.existsBySlug(request.slug)) {
            throw ConflictException("Категория со slug '${request.slug}' уже существует")
        }
        val newCategory = request.toCategory()
        val savedCategory = pointCategoryRepository.save(newCategory)
        return savedCategory.toCategoryItem()
    }

    override fun patchCategory(
        id: Long,
        request: AdminPatchCategoryRequest,
    ): CategoryItem {
        val category =
            pointCategoryRepository
                .findById(id)
                .orElseThrow { NotFoundException("Категория с id=$id не найдена") }

        if (!request.slug.isNullOrBlank() &&
            category.slug != request.slug &&
            pointCategoryRepository.existsBySlug(request.slug)
        ) {
            throw ConflictException("Slug '${request.slug}' уже занят")
        }

        val updatedCategory =
            category.copy(
                name = request.name?.takeIf { it.isNotBlank() } ?: category.name,
                slug = request.slug?.takeIf { it.isNotBlank() } ?: category.slug,
            )

        val savedCategory = pointCategoryRepository.save(updatedCategory)

        return savedCategory.toCategoryItem()
    }

    override fun deleteCategory(id: Long) {
        val category =
            pointCategoryRepository
                .findById(id)
                .orElseThrow { NotFoundException("Категория с id=$id не найдена") }

        pointCategoryRepository.delete(category)
    }

    fun existsById(id: Long): Boolean = pointCategoryRepository.existsById(id)

    fun checkRequestCategory(requestedIds: Collection<Long>): Collection<Long> {
        val existingIds = pointCategoryRepository.findAllByIdIn(requestedIds).map { it.id }.toSet()
        val notFoundIds = requestedIds - existingIds
        if (notFoundIds.isNotEmpty()) {
            throw NotFoundException("Категории не найдены: ${notFoundIds.joinToString(", ")}")
        }
        return requestedIds
    }

    fun getCategoryById(categoryId: Long): PointCategory =
        pointCategoryRepository.findById(categoryId).orElseThrow { NotFoundException("Категория с id=$categoryId не найдена") }
}
