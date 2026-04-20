package t.lab.guide.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import t.lab.guide.dto.ApiErrorResponse;
import t.lab.guide.dto.point.PointDetailResponse;
import t.lab.guide.dto.point.PointListResponse;
import t.lab.guide.dto.point.PointSearchRequest;
import t.lab.guide.service.PointService;

@Tag(name = "Points", description = "Операции с точками интереса")
@RestController
@RequestMapping("/points")
@RequiredArgsConstructor
public class PointController {
    private final PointService pointService;

    @Operation(
            summary = "Поиск точек",
            description = "Возвращает список точек интереса, удовлетворяющих условиям фильтра (категории, гео-радиус, текстовый запрос и т.д.)"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Точки успешно найдены",
                    content = @Content(schema = @Schema(implementation = PointListResponse.class))),
            @ApiResponse(responseCode = "400",
                    description = "Некорректные параметры запроса",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @PostMapping("/search")
    public ResponseEntity<PointListResponse> searchPoints(@Valid @RequestBody PointSearchRequest request) {
        PointListResponse response = pointService.searchPoints(request);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Детали точки",
            description = "Возвращает подробную информацию о точке интереса по её идентификатору"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Информация о точке успешно получена",
                    content = @Content(schema = @Schema(implementation = PointDetailResponse.class))),
            @ApiResponse(responseCode = "404",
                    description = "Точка с указанным идентификатором не найдена",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @GetMapping("/{pointId}")
    public ResponseEntity<PointDetailResponse> getPointDetail(
            @Parameter(description = "Идентификатор точки интереса", example = "1")
            @PathVariable Long pointId) {
        PointDetailResponse response = pointService.getPointDetail(pointId);
        return ResponseEntity.ok(response);
    }

}