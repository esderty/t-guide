package t.lab.guide.repository

import org.springframework.data.repository.CrudRepository
import t.lab.guide.domain.PointCategory

interface PointCategoryRepository : CrudRepository<PointCategory, Long> {
    fun findAllByIdIn(slugs: Collection<Long>): List<PointCategory>

    fun existsBySlug(slug: String): Boolean
}
