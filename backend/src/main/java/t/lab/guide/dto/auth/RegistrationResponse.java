package t.lab.guide.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import t.lab.guide.dto.user.UserResponse;

@Schema(description = "Ответ при регистрации нового пользователя, содержит пару токенов и информацию о пользователе")
@Builder
public record RegistrationResponse(
        @Schema(description = "Пара access + refresh токенов")
        TokenPairResponse tokens,
        @Schema(description = "Информация о пользователе")
        UserResponse user
){}
