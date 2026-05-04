package t.lab.guide.repository

import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import t.lab.guide.domain.PointMedia

interface PointMediaRepository : CrudRepository<PointMedia, Long> {
    @Query(
        """
        SELECT * FROM point_media pm
        WHERE pm.point_id = :pointId
        ORDER BY pm.sort_order, id
    """,
    )
    fun findMediaByPointId(
        @Param("pointId") pointId: Long,
    ): List<PointMedia>
}
