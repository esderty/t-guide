package t.lab.guide.repository

import org.springframework.data.jdbc.repository.query.Modifying
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import t.lab.guide.domain.ExcursionPoint
import t.lab.guide.repository.view.point.AdminPointShortView
import t.lab.guide.repository.view.point.PointShortView

interface ExcursionPointRepository : CrudRepository<ExcursionPoint, Long> {
    @Query(
        """
            SELECT
                p.id,
                p.title,
                p.short_description,
                p.category_id,
                pc.name AS category_name,
                p.latitude,
                p.longitude,
                p.visit_time_min AS visit_time
            FROM excursion_point ep
            JOIN point p ON p.id = ep.point_id
            JOIN point_category pc ON pc.id = p.category_id
            WHERE ep.excursion_id = :excursionId
            ORDER BY ep.order_index
        """,
    )
    fun findOrderedPointsByExcursionId(
        @Param("excursionId") excursionId: Long,
    ): List<PointShortView>

    @Query(
        """
            SELECT
                p.id,
                p.title,
                p.category_id,
                pc.name AS category_name,
                p.visit_time_min AS visit_time
                p.latitude,
                p.longitude,
                p.is_active,
                p.created_at
            FROM excursion_point ep
            JOIN point p ON p.id = ep.point_id
            JOIN point_category pc ON pc.id = p.category_id
            WHERE ep.excursion_id = :excursionId
            ORDER BY ep.order_index
        """,
    )
    fun findAdminPointsByExcursionId(
        @Param("excursionId") excursionId: Long,
    ): List<AdminPointShortView>

    @Modifying
    @Query("DELETE FROM excursion_point WHERE excursion_id = :excursionId")
    fun deleteByExcursionId(
        @Param("excursionId") excursionId: Long,
    ): Int
}
