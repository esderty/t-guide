package t.lab.guide.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import t.lab.guide.dto.ApiErrorResponse;
import t.lab.guide.dto.excursion.*;
import t.lab.guide.service.ExcursionService;

@Tag(name = "Excursions", description = "Операции с экскурсиями")
@RestController
@RequestMapping("/excursions")
@RequiredArgsConstructor
public class ExcursionController {
    private final ExcursionService excursionService;

    @Operation(
            summary = "Поиск экскурсий",
            description = "Возвращает список экскурсий, удовлетворяющих условиям фильтра (категории, длительность, тип маршрута и т.д.)"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Экскурсии успешно найдены",
                    content = @Content(schema = @Schema(implementation = ExcursionListResponse.class))),
            @ApiResponse(responseCode = "400",
                    description = "Некорректные параметры запроса",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @PostMapping("/search")
    public ResponseEntity<ExcursionListResponse> searchExcursions (@Valid @RequestBody ExcursionSearchRequest request) {
        ExcursionListResponse response = excursionService.searchExcursions(request);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Детали экскурсии",
            description = "Возвращает подробную информацию об экскурсии, включая список точек маршрута"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Информация об экскурсии успешно получена",
                    content = @Content(schema = @Schema(implementation = ExcursionDetailResponse.class))),
            @ApiResponse(responseCode = "404",
                    description = "Экскурсия с указанным идентификатором не найдена",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @GetMapping("/{excursionId}")
    public ResponseEntity<ExcursionDetailResponse> getExcursionDetail (
            @Parameter(description = "Идентификатор экскурсии", example = "1")
            @PathVariable Long excursionId) {
        ExcursionDetailResponse response = excursionService.getExcursionDetail(excursionId);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Создать кастомную экскурсию",
            description = "Создаёт пользовательскую экскурсию. Доступно только авторизованным пользователям.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Экскурсия успешно создана",
                    content = @Content(schema = @Schema(implementation = ExcursionDetailResponse.class))),
            @ApiResponse(responseCode = "400",
                    description = "Некорректные данные запроса",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "401",
                    description = "Пользователь не авторизован",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @PostMapping()
    public ResponseEntity<ExcursionDetailResponse> createCustomExcursion (@Valid @RequestBody CreateCustomExcursionRequest request) {
        ExcursionDetailResponse response = excursionService.createCustomExcursion(request);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Обновить кастомную экскурсию",
            description = "Частично обновляет данные пользовательской экскурсии (название, описание, видимость и т.д.). Доступно только владельцу.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Экскурсия успешно обновлена",
                    content = @Content(schema = @Schema(implementation = ExcursionShortItem.class))),
            @ApiResponse(responseCode = "400",
                    description = "Некорректные данные запроса",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "401",
                    description = "Пользователь не авторизован",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "403",
                    description = "Нет прав на изменение экскурсии",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "404",
                    description = "Экскурсия не найдена",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @PatchMapping("/{excursionId}")
    public ResponseEntity<ExcursionShortItem> updateCustomExcursion (
            @Parameter(description = "Идентификатор экскурсии", example = "1")
            @PathVariable Long excursionId,
            @Valid @RequestBody UpdateCustomExcursionRequest request) {
        ExcursionShortItem response = excursionService.updateCustomExcursion(excursionId, request);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Задать точки маршрута",
            description = "Полностью заменяет список точек маршрута кастомной экскурсии. Доступно только владельцу.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Точки маршрута успешно обновлены",
                    content = @Content(schema = @Schema(implementation = ExcursionDetailResponse.class))),
            @ApiResponse(responseCode = "400",
                    description = "Некорректные данные запроса",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "401",
                    description = "Пользователь не авторизован",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "403",
                    description = "Нет прав на изменение экскурсии",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "404",
                    description = "Экскурсия не найдена",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @PutMapping("/{excursionId}/points")
    public ResponseEntity<ExcursionDetailResponse> setExcursionPoints (
            @Parameter(description = "Идентификатор экскурсии", example = "1")
            @PathVariable Long excursionId,
            @Valid @RequestBody SetExcursionPointsRequest request) {
        ExcursionDetailResponse response = excursionService.setExcursionPoints(excursionId, request);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Удалить кастомную экскурсию",
            description = "Удаляет пользовательскую экскурсию. Доступно только владельцу.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204",
                    description = "Экскурсия успешно удалена"),
            @ApiResponse(responseCode = "401",
                    description = "Пользователь не авторизован",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "403",
                    description = "Нет прав на удаление экскурсии",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "404",
                    description = "Экскурсия не найдена",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @DeleteMapping("/{excursionId}")
    public ResponseEntity<Void> deleteCustomExcursion (
            @Parameter(description = "Идентификатор экскурсии", example = "1")
            @PathVariable Long excursionId) {
        excursionService.deleteCustomExcursion(excursionId);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Добавить в избранное",
            description = "Добавляет экскурсию в список избранного текущего пользователя",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204",
                    description = "Экскурсия добавлена в избранное"),
            @ApiResponse(responseCode = "401",
                    description = "Пользователь не авторизован",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "404",
                    description = "Экскурсия не найдена",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @PostMapping("/{excursionId}/favorite")
    public ResponseEntity<Void> favoriteExcursion (
            @Parameter(description = "Идентификатор экскурсии", example = "1")
            @PathVariable Long excursionId) {
        excursionService.favoriteExcursion(excursionId);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Убрать из избранного",
            description = "Удаляет экскурсию из списка избранного текущего пользователя",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204",
                    description = "Экскурсия убрана из избранного"),
            @ApiResponse(responseCode = "401",
                    description = "Пользователь не авторизован",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "404",
                    description = "Экскурсия не найдена",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @PostMapping("/{excursionId}/unfavorite")
    public ResponseEntity<Void> unfavoriteExcursion (
            @Parameter(description = "Идентификатор экскурсии", example = "1")
            @PathVariable Long excursionId) {
        excursionService.unfavoriteExcursion(excursionId);
        return ResponseEntity.noContent().build();
    }


}