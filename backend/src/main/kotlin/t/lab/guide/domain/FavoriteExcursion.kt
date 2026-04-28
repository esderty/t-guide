package t.lab.guide.domain

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.jdbc.core.mapping.AggregateReference
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.OffsetDateTime

@Table(name = "favorite_excursion")
data class FavoriteExcursion(
    @Id
    val id: Long? = null,
    @Column("user_id")
    val user: AggregateReference<User, Long>? = null,
    @Column("excursion_id")
    val excursion: AggregateReference<Excursion, Long>? = null,
    @CreatedDate
    val favoriteAt: OffsetDateTime? = null,
)
