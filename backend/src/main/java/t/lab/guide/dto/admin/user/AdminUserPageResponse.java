package t.lab.guide.dto.admin.user;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Страница пользователей для админ-панели с метаданными пагинации")
public record AdminUserPageResponse(
        @Schema(description = "Список пользователей на текущей странице")
        List<AdminUserShortItem> users,

        @Schema(description = "Номер текущей страницы (с 0)", example = "0")
        Integer page,

        @Schema(description = "Размер страницы (количество элементов)", example = "20")
        Integer size,

        @Schema(description = "Общее количество пользователей", example = "531")
        Long totalElements,

        @Schema(description = "Общее количество страниц", example = "27")
        Integer totalPages
) {}