package t.lab.guide.repository

import org.springframework.data.domain.Pageable
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import t.lab.guide.domain.Excursion
import t.lab.guide.repository.view.excursion.AdminExcursionDetailView
import t.lab.guide.repository.view.excursion.AdminExcursionShortView
import t.lab.guide.repository.view.excursion.ExcursionShortView

interface ExcursionRepository : CrudRepository<Excursion, Long> {
    @Query(
        """
            SELECT
                ex.id,
                ex.title,
                ex.description,
                ex.short_description,
                ex.distance,
                ex.duration_min,
                ex.route_type,
                ex.visibility,
                ex.owner_id,
                fp.latitude,
                fp.longitude,
                (
                    SELECT count(*)
                    FROM excursion_point ep
                    WHERE ep.excursion_id = ex.id
                ) AS points_count,
                COALESCE(
                    (
                        SELECT array_agg(DISTINCT p.category_id)
                        FROM excursion_point ep
                        JOIN point p ON p.id = ep.point_id
                        WHERE ep.excursion_id = ex.id
                    ),
                    ARRAY[]::bigint[]
                ) AS category_ids,
                (
                    SELECT AVG(er.rating)::float8
                    FROM excursion_review er
                    WHERE er.excursion_id = ex.id AND er.is_active = TRUE
                ) AS rating,
                (
                    SELECT COUNT(*)::int
                    FROM excursion_review er
                    WHERE er.excursion_id = ex.id AND er.is_active = TRUE
                ) AS reviews_count
            FROM excursion ex
            JOIN LATERAL (
                SELECT ep.point_id
                FROM excursion_point ep
                WHERE ep.excursion_id = ex.id
                ORDER BY ep.order_index
                LIMIT 1
            ) fp_ep ON TRUE
            JOIN point fp ON fp.id = fp_ep.point_id
            WHERE ex.route_type = 'PREBUILT'
                AND ex.visibility = 'PUBLIC'
                AND (:visitTime IS NULL OR ex.duration_min <= :visitTime)
                AND EXISTS (
                    SELECT 1
                    FROM excursion_point ep
                    JOIN point p ON p.id = ep.point_id
                    WHERE ep.excursion_id = ex.id
                        AND st_dwithin(
                            p.geom,
                            st_setsrid(st_makepoint(:lng, :lat), 4326)::geography,
                            :radiusMeters
                        )
                        AND (:categoryIds::bigint[] IS NULL OR p.category_id = ANY(:categoryIds::bigint[]))
                )
            ORDER BY fp.geom <-> st_setsrid(st_makepoint(:lng, :lat), 4326)::geography
    """,
    )
    fun findExcursionsByLocation(
        @Param("lat") lat: Double,
        @Param("lng") lng: Double,
        @Param("radiusMeters") radiusMeters: Int,
        @Param("categoryIds") categoryIds: Array<Long>?,
        @Param("visitTime") visitTime: Int?,
    ): List<ExcursionShortView>

    @Query(
        """
            SELECT
                ex.id,
                ex.title,
                ex.description,
                ex.short_description,
                ex.distance,
                ex.duration_min,
                ex.route_type,
                ex.visibility,
                ex.owner_id,
                fp.latitude,
                fp.longitude,
                (
                    SELECT count(*)
                    FROM excursion_point ep
                    WHERE ep.excursion_id = ex.id
                ) AS points_count,
                COALESCE(
                    (
                        SELECT array_agg(DISTINCT p.category_id)
                        FROM excursion_point ep
                        JOIN point p ON p.id = ep.point_id
                        WHERE ep.excursion_id = ex.id
                    ),
                    ARRAY[]::bigint[]
                ) AS category_ids,
                (
                    SELECT AVG(er.rating)::float8
                    FROM excursion_review er
                    WHERE er.excursion_id = ex.id AND er.is_active = TRUE
                ) AS rating,
                (
                    SELECT COUNT(*)::int
                    FROM excursion_review er
                    WHERE er.excursion_id = ex.id AND er.is_active = TRUE
                ) AS reviews_count
            FROM excursion ex
            JOIN LATERAL (
                SELECT ep.point_id
                FROM excursion_point ep
                WHERE ep.excursion_id = ex.id
                ORDER BY ep.order_index
                LIMIT 1
            ) fp_ep ON TRUE
            JOIN point fp ON fp.id = fp_ep.point_id
            WHERE ex.route_type = 'CUSTOM'
                AND ex.owner_id = :userId
            ORDER BY ex.created_at
    """,
    )
    fun findCustomExcursionsByUserId(
        @Param("userId") userId: Long,
        pageable: Pageable,
    ): List<ExcursionShortView>

    @Query(
        """
    select count(*) from excursion
    where route_type = 'CUSTOM' and owner_id = :userId
    """,
    )
    fun countCustomExcursionsByUserId(
        @Param("userId") userId: Long,
    ): Long

    @Query(
        """
            SELECT
                ex.id,
                ex.title,
                ex.description,
                ex.short_description,
                ex.distance,
                ex.duration_min,
                ex.route_type,
                ex.visibility,
                ex.owner_id,
                fp.latitude,
                fp.longitude,
                (
                    SELECT count(*)
                    FROM excursion_point ep
                    WHERE ep.excursion_id = ex.id
                ) AS points_count,
                COALESCE(
                    (
                        SELECT array_agg(DISTINCT p.category_id)
                        FROM excursion_point ep
                        JOIN point p ON p.id = ep.point_id
                        WHERE ep.excursion_id = ex.id
                    ),
                    ARRAY[]::bigint[]
                ) AS category_ids,
                (
                    SELECT AVG(er.rating)::float8
                    FROM excursion_review er
                    WHERE er.excursion_id = ex.id AND er.is_active = TRUE
                ) AS rating,
                (
                    SELECT COUNT(*)::int
                    FROM excursion_review er
                    WHERE er.excursion_id = ex.id AND er.is_active = TRUE
                ) AS reviews_count
            FROM excursion ex
            LEFT JOIN LATERAL (
                SELECT ep.point_id
                FROM excursion_point ep
                WHERE ep.excursion_id = ex.id
                ORDER BY ep.order_index
                LIMIT 1
            ) fp_ep ON TRUE
            LEFT JOIN point fp ON fp.id = fp_ep.point_id
            WHERE (:search is null or ex.title ilike '%' || :search || '%')

    """,
    )
    fun findAdminExcursionPage(
        @Param("search") search: String?,
        pageable: Pageable,
    ): List<AdminExcursionShortView>

    @Query(
        """
            SELECT
                count(*)
            FROM excursion ex
            LEFT JOIN LATERAL (
                SELECT ep.point_id
                FROM excursion_point ep
                WHERE ep.excursion_id = ex.id
                ORDER BY ep.order_index
                LIMIT 1
            ) fp_ep ON TRUE
            LEFT JOIN point fp ON fp.id = fp_ep.point_id
            WHERE (:search is null or ex.title ilike '%' || :search || '%')

    """,
    )
    fun countAdminExcursionPage(
        @Param("search") search: String?,
    ): Long

    @Query(
        """
            SELECT
                ex.id,
                ex.title,
                ex.description,
                ex.short_description,
                ex.distance,
                ex.duration_min,
                ex.route_type,
                ex.visibility,
                ex.owner_id,
                fp.latitude,
                fp.longitude,
                (
                    SELECT count(*)
                    FROM excursion_point ep
                    WHERE ep.excursion_id = ex.id
                ) AS points_count,
                COALESCE(
                    (
                        SELECT array_agg(DISTINCT p.category_id)
                        FROM excursion_point ep
                        JOIN point p ON p.id = ep.point_id
                        WHERE ep.excursion_id = ex.id
                    ),
                    ARRAY[]::bigint[]
                ) AS category_ids,
                (
                    SELECT AVG(er.rating)::float8
                    FROM excursion_review er
                    WHERE er.excursion_id = ex.id AND er.is_active = TRUE
                ) AS rating,
                (
                    SELECT COUNT(*)::int
                    FROM excursion_review er
                    WHERE er.excursion_id = ex.id AND er.is_active = TRUE
                ) AS reviews_count
            FROM excursion ex
            LEFT JOIN LATERAL (
                SELECT ep.point_id
                FROM excursion_point ep
                WHERE ep.excursion_id = ex.id
                ORDER BY ep.order_index
                LIMIT 1
            ) fp_ep ON TRUE
            LEFT JOIN point fp ON fp.id = fp_ep.point_id
            WHERE ex.id = :id
        """,
    )
    fun findShortViewById(
        @Param("id") id: Long,
    ): ExcursionShortView?

    @Query(
        """
            SELECT
                ex.id,
                ex.title,
                ex.description,
                ex.short_description,
                ex.distance,
                ex.duration_min,
                ex.route_type,
                ex.visibility,
                ex.owner_id,
                fp.latitude,
                fp.longitude,
                (
                    SELECT count(*)
                    FROM excursion_point ep
                    WHERE ep.excursion_id = ex.id
                ) AS points_count,
                COALESCE(
                    (
                        SELECT array_agg(DISTINCT p.category_id)
                        FROM excursion_point ep
                        JOIN point p ON p.id = ep.point_id
                        WHERE ep.excursion_id = ex.id
                    ),
                    ARRAY[]::bigint[]
                ) AS category_ids,
                (
                    SELECT AVG(er.rating)::float8
                    FROM excursion_review er
                    WHERE er.excursion_id = ex.id AND er.is_active = TRUE
                ) AS rating,
                (
                    SELECT COUNT(*)::int
                    FROM excursion_review er
                    WHERE er.excursion_id = ex.id AND er.is_active = TRUE
                ) AS reviews_count,
                ex.created_by,
                ex.created_at,
                ex.updated_at
            FROM excursion ex
            LEFT JOIN LATERAL (
                SELECT ep.point_id
                FROM excursion_point ep
                WHERE ep.excursion_id = ex.id
                ORDER BY ep.order_index
                LIMIT 1
            ) fp_ep ON TRUE
            LEFT JOIN point fp ON fp.id = fp_ep.point_id
            WHERE ex.id = :id
        """,
    )
    fun findAdminDetailViewById(
        @Param("id") id: Long,
    ): AdminExcursionDetailView?
}
