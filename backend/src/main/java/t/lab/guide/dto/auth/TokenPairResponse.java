package t.lab.guide.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Schema(description = "Ответ с токенами доступа и обновления")
@Builder
public record TokenPairResponse(
        @Schema(description = "Токен доступа, используемый для аутентификации при запросах к защищенным ресурсам",
                example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiYWRtaW4iOnRydWUsImlhdCI6MTUxNjIzOTAyMn0.KMUFsIDTnFmyG3nMiGM6H9FNFUROf3wh7SmqJp-QV30")
        String accessToken,
        @Schema(description = "Токен обновления, используемый для получения нового токена доступа после истечения срока действия текущего",
                example = "d1f2e3c4b5a6978876543210fedcba9876543210fedcba9876543210fedcba")
        String refreshToken
){}
