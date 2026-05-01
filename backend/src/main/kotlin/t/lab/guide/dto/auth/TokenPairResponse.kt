package t.lab.guide.dto.auth

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Ответ с токенами доступа и обновления")
data class TokenPairResponse(
    @Schema(
        description = "Токен доступа, используемый для аутентификации при запросах к защищенным ресурсам",
        example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9....",
    )
    val accessToken: String,
    @Schema(
        description = "Токен обновления, используемый для получения нового токена доступа",
        example = "d1f2e3c4b5a6978876543210fedcba9876543210fedcba9876543210fedcba",
    )
    val refreshToken: String,
)
