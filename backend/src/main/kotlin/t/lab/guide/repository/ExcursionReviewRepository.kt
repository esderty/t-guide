package t.lab.guide.repository

import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import t.lab.guide.domain.ExcursionReview

interface ExcursionReviewRepository : CrudRepository<ExcursionReview, Long> {
    @Query(
        """
            SELECT * FROM excursion_review
            WHERE excursion_id = :excursionId
                AND user_id = :userId
        """,
    )
    fun findByExcursionIdAndUserId(
        @Param("excursionId") excursionId: Long,
        @Param("userId") userId: Long,
    ): ExcursionReview?

    @Query(
        """
            SELECT EXISTS(
                SELECT 1 FROM excursion_review
                WHERE excursion_id = :excursionId
                    AND user_id = :userId
            )
        """,
    )
    fun existsByExcursionIdAndUserId(
        @Param("excursionId") excursionId: Long,
        @Param("userId") userId: Long,
    ): Boolean

    @Query(
        """
            SELECT * FROM excursion_review
            WHERE excursion_id = :excursionId
                AND is_active = TRUE
            ORDER BY created_at DESC
        """,
    )
    fun findActiveByExcursionId(
        @Param("excursionId") excursionId: Long,
    ): List<ExcursionReview>
}
