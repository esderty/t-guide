package t.lab.guide.security

import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.oauth2.jose.jws.MacAlgorithm
import org.springframework.security.oauth2.jwt.JwsHeader
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.jwt.JwtClaimsSet
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.jwt.JwtEncoder
import org.springframework.security.oauth2.jwt.JwtEncoderParameters
import org.springframework.security.oauth2.jwt.JwtException
import org.springframework.stereotype.Service
import t.lab.guide.mapper.toAppUserDetails
import t.lab.guide.properties.JwtProperties
import t.lab.guide.repository.UserRepository
import java.time.Instant
import java.util.UUID

@Service
class JwtTokenService(
    private val encoder: JwtEncoder,
    private val decoder: JwtDecoder,
    private val sessionStore: RefreshSessionStore,
    private val properties: JwtProperties,
    private val userRepository: UserRepository,
) {
    fun issueTokens(user: AppUserDetails): TokenPair {
        val now = Instant.now()
        val sessionId = UUID.randomUUID()

        val accessToken = encodeAccess(user)
        val refreshToken = encodeRefresh(user.id, sessionId)

        val refreshSession =
            RefreshSession(
                id = sessionId,
                userId = user.id,
                createdAt = now,
                expiresAt = now.plus(properties.refreshTtl),
                lastUsedAt = now,
            )
        sessionStore.save(session = refreshSession, ttl = properties.refreshTtl)

        return TokenPair(accessToken = accessToken, refreshToken = refreshToken)
    }

    fun parseAccessToken(accessToken: String): Jwt = decoder.decode(accessToken)

    fun rotate(refreshToken: String): TokenPair {
        val jwt = decoder.decode(refreshToken)
        val sid = UUID.fromString(jwt.getClaimAsString("sid"))
        val session =
            sessionStore.find(sid)
                ?: throw BadCredentialsException("Invalid refresh token")

        val view =
            userRepository.findLoginViewByUserId(session.userId)
                ?: throw BadCredentialsException("User not found for refresh token")

        if (!view.isActive) throw BadCredentialsException("User is not active")

        val userDetails = view.toAppUserDetails()

        val tokenPair = issueTokens(userDetails)

        sessionStore.delete(session)

        return tokenPair
    }

    fun revoke(refreshToken: String) {
        val jwt =
            try {
                decoder.decode(refreshToken)
            } catch (_: JwtException) {
                return
            }
        val sid = jwt.getClaimAsString("sid")?.let { runCatching { UUID.fromString(it) }.getOrNull() } ?: return
        sessionStore.find(sid)?.let { sessionStore.delete(it) }
    }

    fun revokeAllUserTokens(userId: Long) {
        sessionStore.deleteAllByUser(userId)
    }

    private fun encodeAccess(user: AppUserDetails): String {
        val now = Instant.now()
        val claims =
            JwtClaimsSet
                .builder()
                .issuer(properties.issuer)
                .subject(user.id.toString())
                .issuedAt(now)
                .claim("role", user.role.name)
                .expiresAt(now.plus(properties.accessTtl))
                .build()

        return encoder.encode(JwtEncoderParameters.from(JwsHeader.with(MacAlgorithm.HS256).build(), claims)).tokenValue
    }

    private fun encodeRefresh(
        userId: Long,
        sessionId: UUID,
    ): String {
        val now = Instant.now()

        val claims =
            JwtClaimsSet
                .builder()
                .issuer(properties.issuer)
                .subject(userId.toString())
                .claim("sid", sessionId.toString())
                .issuedAt(now)
                .expiresAt(now.plus(properties.refreshTtl))
                .build()

        return encoder.encode(JwtEncoderParameters.from(JwsHeader.with(MacAlgorithm.HS256).build(), claims)).tokenValue
    }
}

data class TokenPair(
    val accessToken: String,
    val refreshToken: String,
)
