package t.lab.guide.domain

import org.springframework.data.annotation.Id
import org.springframework.data.jdbc.core.mapping.AggregateReference
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table(name = "excursion_point")
data class ExcursionPoint(
    @Id
    val id: Long? = null,
    @Column("excursion_id")
    val excursion: AggregateReference<Excursion, Long>? = null,
    @Column("point_id")
    val point: AggregateReference<Point, Long>? = null,
    val orderIndex: Int = 0,
)
