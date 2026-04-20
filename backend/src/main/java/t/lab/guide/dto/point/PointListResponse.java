package t.lab.guide.dto.point;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.List;

@Schema(description = "Ответ с краткой информацией о найденных точках интереса")
@Builder
public record PointListResponse(
        @ArraySchema(
                arraySchema = @Schema(description = "Список точек интереса"),
                schema = @Schema(implementation = PointShortItem.class)
        )
        List<PointShortItem> points
) {}