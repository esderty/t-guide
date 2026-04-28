package t.lab.guide.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.auditing.DateTimeProvider
import org.springframework.data.jdbc.repository.config.EnableJdbcAuditing
import java.time.OffsetDateTime
import java.util.Optional

@Configuration
@EnableJdbcAuditing(dateTimeProviderRef = "auditingDateTimeProvider")
class JdbcAuditingConfig {
    @Bean("auditingDateTimeProvider")
    fun auditingDateTimeProvider(): DateTimeProvider = DateTimeProvider { Optional.of(OffsetDateTime.now()) }
}
