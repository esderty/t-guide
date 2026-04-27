package t.lab.guide.repository

import org.springframework.data.repository.CrudRepository
import t.lab.guide.domain.PointMedia

interface PointMediaRepository : CrudRepository<PointMedia, Long>
