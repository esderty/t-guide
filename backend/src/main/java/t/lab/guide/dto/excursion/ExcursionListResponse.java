package t.lab.guide.dto.excursion;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.List;

@Schema(description = "Ответ с краткой информацией о найденных экскурсиях")
@Builder
public record ExcursionListResponse(
        @ArraySchema(
                arraySchema = @Schema(description = "Список экскурсий"),
                schema = @Schema(implementation = ExcursionShortItem.class)
        )
        List<ExcursionShortItem> excursions
) {}