package t.lab.guide.dto.point.category;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.List;

@Schema(description = "Ответ с списком категорий")
@Builder
public record CategoryListResponse (
    @ArraySchema(
            arraySchema = @Schema(description = "Список категорий"),
            schema = @Schema(implementation = CategoryItem.class)
    )
    List<CategoryItem> categories
){}
