package t.lab.guide.config

import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration
import t.lab.guide.properties.S3Properties

@Configuration
@EnableConfigurationProperties(S3Properties::class)
class S3Config
