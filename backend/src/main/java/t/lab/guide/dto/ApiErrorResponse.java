package t.lab.guide.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.OffsetDateTime;
import java.util.Map;

@Schema(description = "Стандартный формат ответа при возникновении ошибки в API")
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiErrorResponse(
        @Schema(description = "HTTP статус ошибки", example = "404")
        Integer status,
        @Schema(description = "Код ошибки", example = "Not Found")
        String error,
        @Schema(description = "Подробное сообщение об ошибке", example = "Пользователь не найден")
        String message,
        @Schema(description = "Временная метка возникновения ошибки", example = "2026-04-13T12:00:00Z")
        OffsetDateTime timestamp,
        @Schema(description = "Ошибки валидации по полям", example = "{\"email\": \"must not be blank\"}")
        Map<String, String> errors
) {}