package t.lab.guide.repository

import org.springframework.data.repository.CrudRepository
import t.lab.guide.domain.PointCategory

interface PointCategoryRepository : CrudRepository<PointCategory, Long>
