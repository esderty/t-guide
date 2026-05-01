package t.lab.guide.security

import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Component
import java.time.Duration
import java.util.UUID

@Component
class RefreshSessionStore(
    private val template: RedisTemplate<String, RefreshSession>,
    private val stringTemplate: StringRedisTemplate,
) {
    fun save(
        session: RefreshSession,
        ttl: Duration,
    ) {
        template.opsForValue().set(sessionKey(session.id), session, ttl)
        stringTemplate.opsForSet().add(userIndexKey(session.userId), session.id.toString())
    }

    fun find(sessionId: UUID): RefreshSession? = template.opsForValue().get(sessionKey(sessionId))

    fun delete(session: RefreshSession) {
        template.delete(sessionKey(session.id))
        stringTemplate.opsForSet().remove(userIndexKey(session.userId), session.id.toString())
    }

    fun deleteAllByUser(userId: Long) {
        val ids = stringTemplate.opsForSet().members(userIndexKey(userId)).orEmpty()
        if (ids.isNotEmpty()) {
            template.delete(ids.map { sessionKey(UUID.fromString(it)) })
        }
        stringTemplate.delete(userIndexKey(userId))
    }

    private fun sessionKey(id: UUID) = "session:$id"

    private fun userIndexKey(userId: Long) = "user:$userId:sessions"
}
