package t.lab.guide.domain

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.jdbc.core.mapping.AggregateReference
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import t.lab.guide.enums.MediaType
import java.time.OffsetDateTime

@Table(name = "point_media")
data class PointMedia(
    @Id
    val id: Long? = null,
    @Column("point_id")
    val point: AggregateReference<Point, Long>? = null,
    val objectKey: String,
    val mediaType: MediaType,
    val sortOrder: Int,
    @CreatedDate
    val createdAt: OffsetDateTime? = null,
)
