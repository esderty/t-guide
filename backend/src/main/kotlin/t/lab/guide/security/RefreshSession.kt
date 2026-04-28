package t.lab.guide.security

import java.time.Instant
import java.util.UUID

data class RefreshSession(
    val id: UUID,
    val userId: Long,
    val createdAt: Instant,
    val expiresAt: Instant,
    val lastUsedAt: Instant,
)
