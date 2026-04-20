package t.lab.guide.dto.excursion;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Точка маршрута пользовательской экскурсии с явным порядком прохождения")
public record ExcursionPointOrderItem(

        @Schema(description = "Идентификатор точки интереса", example = "1")
        @NotNull(message = "поле обязательно!")
        Long pointId,

        @Schema(description = "Порядковый номер точки в маршруте (начиная с 0, без пропусков и дубликатов)",
                example = "0")
        @NotNull(message = "поле обязательно!")
        @Min(0)
        Integer order
) {
}