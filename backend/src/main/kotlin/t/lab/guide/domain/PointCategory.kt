package t.lab.guide.domain

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.OffsetDateTime

@Table(name = "point_category")
data class PointCategory(
    @Id
    val id: Long? = null,
    val name: String = "",
    val slug: String = "",
    @CreatedDate
    val createdAt: OffsetDateTime? = null,
)
