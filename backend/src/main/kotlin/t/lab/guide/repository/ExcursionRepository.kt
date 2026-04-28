package t.lab.guide.repository

import org.springframework.data.repository.CrudRepository
import t.lab.guide.domain.Excursion

interface ExcursionRepository : CrudRepository<Excursion, Long>
