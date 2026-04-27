package t.lab.guide.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.JacksonJsonRedisSerializer
import org.springframework.data.redis.serializer.StringRedisSerializer
import t.lab.guide.security.RefreshSession
import tools.jackson.databind.ObjectMapper

@Configuration
class RedisConfig {
    @Bean
    fun refreshSessionRedisTemplate(
        connectionFactory: RedisConnectionFactory,
        objectMapper: ObjectMapper,
    ): RedisTemplate<String, RefreshSession> =
        RedisTemplate<String, RefreshSession>().apply {
            setConnectionFactory(connectionFactory)
            keySerializer = StringRedisSerializer()
            valueSerializer = JacksonJsonRedisSerializer(objectMapper, RefreshSession::class.java)
            afterPropertiesSet()
        }
}
