package t.lab.guide.repository

import org.springframework.data.repository.CrudRepository
import t.lab.guide.domain.FavoriteExcursion

interface FavoriteExcursionRepository : CrudRepository<FavoriteExcursion, Long>
