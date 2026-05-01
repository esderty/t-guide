package t.lab.guide.repository

import org.springframework.data.repository.CrudRepository
import t.lab.guide.domain.ExcursionPoint

interface ExcursionPointRepository : CrudRepository<ExcursionPoint, Long>
