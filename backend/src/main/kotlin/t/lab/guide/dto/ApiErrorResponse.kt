package t.lab.guide.dto

import io.swagger.v3.oas.annotations.media.Schema
import java.time.OffsetDateTime

@Schema(description = "Стандартный формат ответа при возникновении ошибки в API")
data class ApiErrorResponse(
    @Schema(description = "HTTP статус ошибки", example = "404")
    val status: Int,
    @Schema(description = "Код ошибки", example = "Not Found")
    val error: String,
    @Schema(description = "Подробное сообщение об ошибке", example = "Пользователь не найден")
    val message: String,
    @Schema(description = "Временная метка возникновения ошибки", example = "2026-04-13T12:00:00Z")
    val timestamp: OffsetDateTime,
    @Schema(description = "Ошибки валидации по полям", example = "{\"email\": \"must not be blank\"}")
    val errors: Map<String, String> = emptyMap(),
)
