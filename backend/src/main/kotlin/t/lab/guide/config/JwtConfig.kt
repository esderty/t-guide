package t.lab.guide.config

import com.nimbusds.jose.jwk.source.ImmutableSecret
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.oauth2.jose.jws.MacAlgorithm
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.jwt.JwtEncoder
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder
import t.lab.guide.properties.JwtProperties
import javax.crypto.spec.SecretKeySpec
import kotlin.io.encoding.Base64

@Configuration
@EnableConfigurationProperties(JwtProperties::class)
class JwtConfig {
    @Bean
    fun jwtEncoder(jwtProperties: JwtProperties): JwtEncoder {
        val secretBytes = decodeAndValidateSecret(jwtProperties.secret)

        val secretKey = SecretKeySpec(secretBytes, "HmacSHA256")

        return NimbusJwtEncoder(ImmutableSecret(secretKey))
    }

    @Bean
    fun jwtDecoder(jwtProperties: JwtProperties): JwtDecoder {
        val secretBytes = decodeAndValidateSecret(jwtProperties.secret)

        val secretKey = SecretKeySpec(secretBytes, "HmacSHA256")

        return NimbusJwtDecoder
            .withSecretKey(secretKey)
            .macAlgorithm(MacAlgorithm.HS256)
            .build()
    }

    private fun decodeAndValidateSecret(secret: String): ByteArray {
        val trimmed = secret.trim()
        val secretBytes =
            runCatching { Base64.decode(trimmed) }
                .recoverCatching { Base64.UrlSafe.decode(trimmed) }
                .getOrElse {
                    throw IllegalStateException("security.jwt.secret must be valid Base64 (standard or URL-safe)", it)
                }

        require(secretBytes.size >= MIN_HS256_KEY_SIZE_BYTES) {
            "security.jwt.secret must decode to at least $MIN_HS256_KEY_SIZE_BYTES bytes for HS256"
        }

        return secretBytes
    }

    companion object {
        private const val MIN_HS256_KEY_SIZE_BYTES = 32
    }
}