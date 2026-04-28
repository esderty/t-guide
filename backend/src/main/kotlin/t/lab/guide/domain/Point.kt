package t.lab.guide.domain

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jdbc.core.mapping.AggregateReference
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.OffsetDateTime

@Table(name = "point")
data class Point(
    @Id
    val id: Long? = null,
    @Column("category_id")
    val category: AggregateReference<PointCategory, Long>? = null,
    val title: String,
    val description: String? = null,
    val shortDescription: String? = null,
    val address: String? = null,
    val latitude: Double,
    val longitude: Double,
    val visitTimeMin: Int? = null,
    val workingHours: String? = null,
    val isActive: Boolean,
    @CreatedDate
    val createdAt: OffsetDateTime? = null,
    @LastModifiedDate
    val updatedAt: OffsetDateTime? = null,
)
