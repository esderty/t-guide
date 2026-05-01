package t.lab.guide.dto.admin.point

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotNull
import t.lab.guide.enums.MediaType
import t.lab.guide.validation.ValidEnum

@Schema(description = "Метаданные загружаемого медиа-материала (фото, видео, аудио) к точке интереса")
data class AdminUploadPointMediaRequest(
    @Schema(
        description = "Тип загружаемого медиа-материала",
        requiredMode = Schema.RequiredMode.REQUIRED,
        example = "PHOTO",
        nullable = false,
    )
    @field:NotNull(message = "поле обязательно")
    @field:ValidEnum(
        enumClass = MediaType::class,
        message = "Не правильный тип медиа. Допустимые значения: PHOTO, VIDEO, AUDIO",
    )
    val type: String? = null,
    @Schema(
        description = "Порядковый номер для сортировки (0 = первым в списке)",
        requiredMode = Schema.RequiredMode.NOT_REQUIRED,
        example = "0",
    )
    @field:Min(0, message = "значение должно быть неотрицательным")
    val sortOrder: Int? = null,
)
