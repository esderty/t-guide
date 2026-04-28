package t.lab.guide.domain

import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.OffsetDateTime

@Table(name = "password")
data class Password(
    @Id
    @Column("user_id")
    val id: Long? = null,
    val passwordHash: String,
    @LastModifiedDate
    val updatedAt: OffsetDateTime? = null,
)
