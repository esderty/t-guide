package t.lab.guide.dto.admin.point;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Страница точек интереса для админ-панели с метаданными пагинации")
public record AdminPointPageResponse(
        @Schema(description = "Список точек интереса на текущей странице")
        List<AdminPointShortItem> points,

        @Schema(description = "Номер текущей страницы (с 0)", example = "0")
        Integer page,

        @Schema(description = "Размер страницы (количество элементов)", example = "20")
        Integer size,

        @Schema(description = "Общее количество точек", example = "247")
        Long totalElements,

        @Schema(description = "Общее количество страниц", example = "13")
        Integer totalPages
) {}