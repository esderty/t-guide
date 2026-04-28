package t.lab.guide.repository

import org.springframework.data.repository.CrudRepository
import t.lab.guide.domain.Point

interface PointRepository : CrudRepository<Point, Long>
