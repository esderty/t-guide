package t.lab.guide.domain

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.relational.core.mapping.Table
import t.lab.guide.enums.UserLanguage
import t.lab.guide.enums.UserRole
import java.time.OffsetDateTime

@Table(name = "user")
data class User(
    @Id val id: Long? = null,
    val username: String,
    val name: String,
    val email: String,
    val lang: UserLanguage,
    val role: UserRole,
    val isActive: Boolean,
    @CreatedDate
    val createdAt: OffsetDateTime? = null,
    @LastModifiedDate
    val updatedAt: OffsetDateTime? = null,
)
