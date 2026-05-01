package t.lab.guide.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import java.time.Duration

@ConfigurationProperties(prefix = "security.jwt")
data class JwtProperties(
    val issuer: String,
    val secret: String,
    val accessTtl: Duration,
    val refreshTtl: Duration,
)
