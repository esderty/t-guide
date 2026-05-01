package t.lab.guide.service.mock

import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service
import t.lab.guide.service.SecurityService

@Service
@Profile("demo")
class MockSecurityService : SecurityService {
    override fun getCurrentUserId(): Long = 1L
}
