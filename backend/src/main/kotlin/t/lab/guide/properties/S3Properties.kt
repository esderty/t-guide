package t.lab.guide.properties

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("app.storage")
data class S3Properties(
    val bucket: String,
    val publicBaseUrl: String,
)
