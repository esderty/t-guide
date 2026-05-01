package t.lab.guide.dto.auth

import io.swagger.v3.oas.annotations.media.Schema
import t.lab.guide.dto.user.UserResponse

@Schema(description = "Ответ при регистрации нового пользователя, содержит пару токенов и информацию о пользователе")
data class RegistrationResponse(
    @Schema(description = "Пара access + refresh токенов")
    val tokens: TokenPairResponse,
    @Schema(description = "Информация о пользователе")
    val user: UserResponse,
)
