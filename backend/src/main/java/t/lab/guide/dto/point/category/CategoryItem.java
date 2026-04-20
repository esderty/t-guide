package t.lab.guide.dto.point.category;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Schema(description = "Краткая информация о категории, используется в списках категорий")
@Builder
public record CategoryItem(
        @Schema(description = "Уникальный идентификатор категории", example = "1")
        Long id,
        @Schema(description = "Отображаемое имя категории", example = "Ресторан")
        String name,
        @Schema(description = "Уникальный человекочитаемый идентификатор категории, используемый в URL", example = "restaurant")
        String slug
) {}