package t.lab.guide.repository

import org.springframework.data.domain.Pageable
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import t.lab.guide.domain.Point
import t.lab.guide.repository.view.point.AdminPointDetailView
import t.lab.guide.repository.view.point.AdminPointShortView
import t.lab.guide.repository.view.point.PointDetailView
import t.lab.guide.repository.view.point.PointShortView

interface PointRepository : CrudRepository<Point, Long> {
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
                p.visit_time_min as visit_time
            FROM
                point p
            JOIN point_category pc ON pc.id = p.category_id
            WHERE p.is_active = true
                AND st_dwithin(
                    p.geom,
                    st_setsrid(st_makepoint(:lng, :lat), 4326)::geography,
                    :radiusMeters
                )
                AND (:visitTime IS NULL OR p.visit_time_min <= :visitTime)
                AND (:categoryIds::bigint[] IS NULL OR pc.id = ANY(:categoryIds::bigint[]))
            ORDER BY p.geom <-> st_setsrid(st_makepoint(:lng, :lat), 4326)::geography
    """,
    )
    fun findPointByLocation(
        @Param("lat") lat: Double,
        @Param("lng") lng: Double,
        @Param("radiusMeters") radiusMeters: Int,
        @Param("categoryIds") categoryIds: Array<Long>?,
        @Param("visitTime") visitTime: Int?,
    ): List<PointShortView>

    @Query(
        """
        SELECT
            p.id,
            p.title,
            p.description,
            p.short_description,
            p.category_id,
            pc.name AS category_name,
            p.latitude,
            p.longitude,
            p.visit_time_min as visit_time
        FROM point p
        JOIN point_category pc ON pc.id = p.category_id
        WHERE p.is_active = true AND p.id = :id
    """,
    )
    fun findDetailById(
        @Param("id") id: Long,
    ): PointDetailView?

    @Query(
        """
            select
                p.id,
                p.title,
                p.category_id,
                pc.name AS category_name,
                p.visit_time_min as visit_time,
                p.is_active,
                p.created_at
            from point p
            join point_category pc ON pc.id = p.category_id
            where(:search is null or p.title LIKE '%' || :search || '%'
                                  or p.address LIKE '%' || :search || '%')
        """,
    )
    fun findPointsPage(
        @Param("search") search: String?,
        pageable: Pageable,
    ): List<AdminPointShortView>

    @Query(
        """
            select count(*) from point p
            where (:search is null or p.title LIKE '%' || :search || '%'
                                  or p.address LIKE '%' || :search || '%')
        """,
    )
    fun countPointsInPage(
        @Param("search") search: String?,
    ): Long

    @Query(
        """
            WITH ordered AS (
                SELECT p.geom, u.idx
                FROM unnest(:pointIds::bigint[]) WITH ORDINALITY AS u(point_id, idx)
                JOIN point p ON p.id = u.point_id
            ),
            pairs AS (
                SELECT geom, LAG(geom) OVER (ORDER BY idx) AS prev_geom
                FROM ordered
            )
            SELECT COALESCE(SUM(ST_Distance(prev_geom::geography, geom::geography)), 0)::int
            FROM pairs
            WHERE prev_geom IS NOT NULL
        """,
    )
    fun sumRouteDistanceMeters(
        @Param("pointIds") pointIds: Array<Long>,
    ): Int

    @Query(
        """
        SELECT
            p.id,
            p.title,
            p.description,
            p.short_description,
            p.category_id,
            pc.name AS category_name,
            p.latitude,
            p.longitude,
            p.visit_time_min as visit_time,
            p.working_hours,
            p.is_active,
            p.created_at,
            p.updated_at
        FROM point p
        JOIN point_category pc ON pc.id = p.category_id
        WHERE p.id = :id
    """,
    )
    fun findAdminDetailById(
        @Param("id") id: Long,
    ): AdminPointDetailView?
}
