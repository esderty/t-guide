package t.lab.guide.repository

import org.springframework.data.domain.Pageable
import org.springframework.data.jdbc.repository.query.Modifying
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import t.lab.guide.domain.FavoriteExcursion
import t.lab.guide.repository.view.excursion.ExcursionShortView

interface FavoriteExcursionRepository : CrudRepository<FavoriteExcursion, Long> {
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
            FROM favorite_excursion fe
            JOIN excursion ex ON ex.id = fe.excursion_id
            JOIN LATERAL (
                SELECT ep.point_id
                FROM excursion_point ep
                WHERE ep.excursion_id = ex.id
                ORDER BY ep.order_index
                LIMIT 1
            ) fp_ep ON TRUE
            JOIN point fp ON fp.id = fp_ep.point_id
            WHERE fe.user_id = :userId
            ORDER BY fe.favorite_at DESC
        """,
    )
    fun findFavoriteExcursionsByUserId(
        @Param("userId") userId: Long,
        pageable: Pageable,
    ): List<ExcursionShortView>

    @Query("SELECT count(*) FROM favorite_excursion WHERE user_id = :userId")
    fun countFavoriteExcursionsByUserId(
        @Param("userId") userId: Long,
    ): Long

    @Query(
        """
            SELECT EXISTS(
                SELECT 1 FROM favorite_excursion
                WHERE user_id = :userId AND excursion_id = :excursionId
            )
        """,
    )
    fun existsByUserIdAndExcursionId(
        @Param("userId") userId: Long,
        @Param("excursionId") excursionId: Long,
    ): Boolean

    @Modifying
    @Query("DELETE FROM favorite_excursion WHERE user_id = :userId AND excursion_id = :excursionId")
    fun deleteByUserIdAndExcursionId(
        @Param("userId") userId: Long,
        @Param("excursionId") excursionId: Long,
    ): Int
}
