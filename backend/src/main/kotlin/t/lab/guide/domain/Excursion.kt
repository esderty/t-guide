package t.lab.guide.domain

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jdbc.core.mapping.AggregateReference
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import t.lab.guide.enums.ExcursionRouteType
import t.lab.guide.enums.ExcursionVisibility
import java.time.OffsetDateTime

@Table(name = "excursion")
data class Excursion(
    @Id
    val id: Long? = null,
    @Column("owner_id")
    val owner: AggregateReference<User, Long>? = null,
    val routeType: ExcursionRouteType,
    val visibility: ExcursionVisibility,
    val title: String = "",
    val description: String? = null,
    val distance: Int = 0,
    val durationMin: Int = 0,
    val createdBy: AggregateReference<User, Long>? = null,
    @CreatedDate
    val createdAt: OffsetDateTime? = null,
    @LastModifiedDate
    val updatedAt: OffsetDateTime? = null,
)
