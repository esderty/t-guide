package t.lab.guide.security

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.jwt.JwtException
import org.springframework.web.filter.OncePerRequestFilter
import t.lab.guide.enums.UserRole
import t.lab.guide.properties.JwtProperties

class JwtAuthenticationFilter(
    private val jwtDecoder: JwtDecoder,
    private val jwtProperties: JwtProperties,
) : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        val token = extractBearerToken(request)
        if (token != null) {
            authenticate(token, request)
        }
        filterChain.doFilter(request, response)
    }

    private fun extractBearerToken(request: HttpServletRequest): String? {
        val header = request.getHeader(HttpHeaders.AUTHORIZATION) ?: return null
        if (!header.startsWith(BEARER_PREFIX)) return null
        return header.removePrefix(BEARER_PREFIX).trim().takeIf { it.isNotEmpty() }
    }

    private fun authenticate(
        token: String,
        request: HttpServletRequest,
    ) {
        val jwt =
            try {
                jwtDecoder.decode(token)
            } catch (e: JwtException) {
                log.debug("JWT parsing failed path={}: {}", request.requestURI, e.message)
                return
            }

        if (jwt.getClaimAsString("iss") != jwtProperties.issuer) return

        val userId = jwt.subject?.toLongOrNull() ?: return
        val role =
            jwt
                .getClaimAsString("role")
                ?.let { runCatching { UserRole.valueOf(it) }.getOrNull() }
                ?: return

        val principal = AuthPrincipal(id = userId, role = role)
        val authorities = listOf(SimpleGrantedAuthority("ROLE_${role.name}"))
        SecurityContextHolder.getContext().authentication =
            UsernamePasswordAuthenticationToken(principal, null, authorities)
    }

    private companion object {
        const val BEARER_PREFIX = "Bearer "
        private val log = LoggerFactory.getLogger(JwtAuthenticationFilter::class.java)
    }
}
