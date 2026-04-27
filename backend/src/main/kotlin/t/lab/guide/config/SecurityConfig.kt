package t.lab.guide.config

import jakarta.servlet.http.HttpServletResponse
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import t.lab.guide.dto.ApiErrorResponse
import t.lab.guide.properties.CorsProperties
import t.lab.guide.properties.JwtProperties
import t.lab.guide.security.JwtAuthenticationFilter
import tools.jackson.databind.ObjectMapper
import java.time.OffsetDateTime

@Configuration
@EnableConfigurationProperties(CorsProperties::class)
class SecurityConfig {
    @Bean
    fun securityFilterChain(
        http: HttpSecurity,
        corsProperties: CorsProperties,
        jwtDecoder: JwtDecoder,
        jwtProperties: JwtProperties,
        objectMapper: ObjectMapper,
    ): SecurityFilterChain {
        http
            .csrf { it.disable() }
            .cors { it.configurationSource(corsConfigurationSource(corsProperties)) }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .authorizeHttpRequests { auth ->
                auth
                    .requestMatchers(
                        "/admin/**",
                    ).hasRole("ADMIN")
                    .requestMatchers(
                        HttpMethod.GET,
                        "/excursions/**",
                        "/points/**",
                    ).permitAll()
                    .requestMatchers(
                        "/auth/**",
                        "/swagger-ui/**",
                        "/v3/api-docs/**",
                        "/api-docs/**",
                        "/actuator/health",
                    ).permitAll()
                    .anyRequest()
                    .authenticated()
            }.exceptionHandling { eh ->
                eh.authenticationEntryPoint { _, response, _ ->
                    writeError(response, objectMapper, HttpStatus.UNAUTHORIZED, "Authentication required")
                }
                eh.accessDeniedHandler { _, response, _ ->
                    writeError(response, objectMapper, HttpStatus.FORBIDDEN, "Access denied")
                }
            }.addFilterBefore(
                JwtAuthenticationFilter(jwtDecoder, jwtProperties),
                UsernamePasswordAuthenticationFilter::class.java,
            ).formLogin { it.disable() }
            .httpBasic { it.disable() }

        return http.build()
    }

    private fun writeError(
        response: HttpServletResponse,
        objectMapper: ObjectMapper,
        status: HttpStatus,
        message: String,
    ) {
        response.status = status.value()
        response.contentType = MediaType.APPLICATION_JSON_VALUE
        response.characterEncoding = Charsets.UTF_8.name()
        objectMapper.writeValue(
            response.outputStream,
            ApiErrorResponse(
                status = status.value(),
                error = status.reasonPhrase,
                message = message,
                timestamp = OffsetDateTime.now(),
            ),
        )
    }

    @Bean
    fun authenticationManager(config: AuthenticationConfiguration): AuthenticationManager = config.authenticationManager

    @Bean
    fun corsConfigurationSource(corsProperties: CorsProperties): CorsConfigurationSource {
        val cors =
            CorsConfiguration().apply {
                allowedOrigins = corsProperties.allowedOrigins
                allowedMethods = listOf("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                allowedHeaders = listOf("Authorization", "Content-Type", "X-XSRF-TOKEN")
                exposedHeaders = listOf("Authorization")
                allowCredentials = true
                maxAge = 3600
            }

        return UrlBasedCorsConfigurationSource().apply {
            registerCorsConfiguration("/**", cors)
        }
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder = Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8()
}
