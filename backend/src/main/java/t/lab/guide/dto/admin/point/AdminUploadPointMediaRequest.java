package t.lab.guide.dto.admin.point;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import t.lab.guide.entity.enums.MediaType;

@Schema(description = "Метаданные загружаемого медиа-материала (фото, видео, аудио) к точке интереса")
public record AdminUploadPointMediaRequest(
        @Schema(description = "Тип загружаемого медиа-материала",
                requiredMode = Schema.RequiredMode.REQUIRED,
                example = "PHOTO")
        @NotNull
        MediaType type,

        @Schema(description = "Порядковый номер для сортировки (0 = первым в списке)",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED,
                example = "0")
        @Min(0)
        Integer sortOrder
) {}