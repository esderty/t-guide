package t.lab.guide.domain

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jdbc.core.mapping.AggregateReference
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDate
import java.time.OffsetDateTime

@Table(name = "excursion_review")
data class ExcursionReview(
    @Id
    val id: Long? = null,
    @Column("excursion_id")
    val excursion: AggregateReference<Excursion, Long>? = null,
    @Column("user_id")
    val user: AggregateReference<User, Long>? = null,
    val rating: Short,
    val reviewText: String? = null,
    val visitDate: LocalDate,
    val isActive: Boolean,
    @CreatedDate
    val createdAt: OffsetDateTime? = null,
    @LastModifiedDate
    val updatedAt: OffsetDateTime? = null,
)
