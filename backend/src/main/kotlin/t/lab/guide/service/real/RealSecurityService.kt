package t.lab.guide.service.real

import org.springframework.context.annotation.Profile
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import t.lab.guide.security.AuthPrincipal
import t.lab.guide.service.SecurityService

@Service
@Profile("!demo")
class RealSecurityService : SecurityService {
    override fun getCurrentUserId(): Long =
        (SecurityContextHolder.getContext().authentication?.principal as? AuthPrincipal)?.id
            ?: throw IllegalStateException("No authenticated user found")
}
