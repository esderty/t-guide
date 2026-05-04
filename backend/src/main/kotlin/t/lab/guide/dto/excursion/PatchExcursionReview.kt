package t.lab.guide.dto.excursion

import com.fasterxml.jackson.annotation.JsonIgnore
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.AssertTrue
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.PastOrPresent
import jakarta.validation.constraints.Size
import java.time.LocalDate

data class PatchExcursionReview(
    @Schema(
        description = "Оценка экскурсии по шкале от 1 до 5",
        example = "5",
        minimum = "1",
        maximum = "5",
    )
    @field:Min(value = 1, message = "Оценка должна быть не меньше 1")
    @field:Max(value = 5, message = "Оценка должна быть не больше 5")
    val rating: Short?,
    @Schema(
        description = "Текст отзыва",
        example = "Отличная экскурсия, узнали много нового!",
        maxLength = 5000,
        nullable = true,
    )
    @field:Size(max = 5000, message = "Максимальная длина отзыва - 5000 символов")
    val reviewText: String?,
    @Schema(
        description = "Дата посещения экскурсии (не может быть в будущем)",
        example = "2024-05-01",
        format = "date",
    )
    @field:PastOrPresent(message = "Дата посещения не может быть в будущем")
    val visitDate: LocalDate?,
) {
    @AssertTrue(message = "Хотя бы одно поле должно быть заполнено для обновления")
    @JsonIgnore
    @Schema(hidden = true)
    fun isAnyFieldPresent(): Boolean = rating != null || reviewText != null || visitDate != null
}
