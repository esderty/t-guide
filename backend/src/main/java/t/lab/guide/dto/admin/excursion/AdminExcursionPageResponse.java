package t.lab.guide.dto.admin.excursion;

import io.swagger.v3.oas.annotations.media.Schema;
import t.lab.guide.dto.excursion.ExcursionShortItem;

import java.util.List;

@Schema(description = "Страница экскурсий для админ-панели с метаданными пагинации")
public record AdminExcursionPageResponse(
        @Schema(description = "Список экскурсий на текущей странице")
        List<ExcursionShortItem> excursions,

        @Schema(description = "Номер текущей страницы (с 0)", example = "0")
        Integer page,

        @Schema(description = "Размер страницы (количество элементов)", example = "20")
        Integer size,

        @Schema(description = "Общее количество экскурсий", example = "84")
        Long totalElements,

        @Schema(description = "Общее количество страниц", example = "5")
        Integer totalPages
) {}