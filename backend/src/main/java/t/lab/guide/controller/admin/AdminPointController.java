package t.lab.guide.controller.admin;

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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import t.lab.guide.dto.ApiErrorResponse;
import t.lab.guide.dto.admin.point.AdminCreatePointRequest;
import t.lab.guide.dto.admin.point.AdminPatchPointRequest;
import t.lab.guide.dto.admin.point.AdminPointDetailResponse;
import t.lab.guide.dto.admin.point.AdminPointMediaItem;
import t.lab.guide.dto.admin.point.AdminPointPageResponse;
import t.lab.guide.dto.admin.point.AdminUploadPointMediaRequest;
import t.lab.guide.service.PointService;

@Tag(name = "Admin Points", description = "Управление точками интереса")
@RestController
@RequestMapping("/admin/points")
@RequiredArgsConstructor
public class AdminPointController {
    private final PointService pointService;

    @Operation(
            summary = "Список точек интереса",
            description = "Возвращает страницу точек интереса с пагинацией, сортировкой и текстовым поиском по title.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Страница точек интереса успешно получена",
                    content = @Content(schema = @Schema(implementation = AdminPointPageResponse.class))),
            @ApiResponse(responseCode = "400",
                    description = "Некорректные параметры запроса",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "401",
                    description = "Пользователь не авторизован",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "403",
                    description = "Недостаточно прав",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @GetMapping("/page")
    public ResponseEntity<AdminPointPageResponse> getPointsPage(
            @Parameter(description = "Номер страницы (с 0)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Размер страницы", example = "25")
            @RequestParam(defaultValue = "25") int size,
            @Parameter(description = "Поле для сортировки (например, id, title, createdAt)", example = "createdAt")
            @RequestParam(required = false) String sortBy,
            @Parameter(description = "Направление сортировки: ASC или DESC", example = "DESC")
            @RequestParam(required = false) String sortDirection,
            @Parameter(description = "Строка поиска по title", example = "площадь")
            @RequestParam(required = false) String search
    ) {
        AdminPointPageResponse response = pointService.getPointsPage(page, size, sortBy, sortDirection, search);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Детали точки интереса",
            description = "Возвращает полную информацию о точке интереса по её идентификатору, включая медиа-материалы и служебные поля.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Информация о точке интереса успешно получена",
                    content = @Content(schema = @Schema(implementation = AdminPointDetailResponse.class))),
            @ApiResponse(responseCode = "401",
                    description = "Пользователь не авторизован",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "403",
                    description = "Недостаточно прав",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "404",
                    description = "Точка интереса не найдена",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @GetMapping("/{pointId}")
    public ResponseEntity<AdminPointDetailResponse> getPointById(
            @Parameter(description = "Идентификатор точки интереса", example = "1")
            @PathVariable Long pointId
    ) {
        AdminPointDetailResponse response = pointService.getAdminPointDetail(pointId);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Создать точку интереса",
            description = "Создаёт новую точку интереса с указанными параметрами.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Точка интереса успешно создана",
                    content = @Content(schema = @Schema(implementation = AdminPointDetailResponse.class))),
            @ApiResponse(responseCode = "400",
                    description = "Некорректные данные запроса",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "401",
                    description = "Пользователь не авторизован",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "403",
                    description = "Недостаточно прав",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "404",
                    description = "Категория не найдена",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "409",
                    description = "Точка с такими параметрами уже существует",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @PostMapping
    public ResponseEntity<AdminPointDetailResponse> createPoint(
            @Valid @RequestBody AdminCreatePointRequest request
    ) {
        AdminPointDetailResponse response = pointService.createPoint(request);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Обновить точку интереса",
            description = "Частично обновляет данные точки интереса. Передаются только изменяемые поля.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Точка интереса успешно обновлена",
                    content = @Content(schema = @Schema(implementation = AdminPointDetailResponse.class))),
            @ApiResponse(responseCode = "400",
                    description = "Некорректные данные запроса",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "401",
                    description = "Пользователь не авторизован",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "403",
                    description = "Недостаточно прав",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "404",
                    description = "Точка интереса или категория не найдена",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "409",
                    description = "Конфликт параметров точки интереса",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @PatchMapping("/{pointId}")
    public ResponseEntity<AdminPointDetailResponse> patchPoint(
            @Parameter(description = "Идентификатор точки интереса", example = "1")
            @PathVariable Long pointId,
            @Valid @RequestBody AdminPatchPointRequest request
    ) {
        AdminPointDetailResponse response = pointService.patchPoint(pointId, request);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Удалить точку интереса",
            description = "Удаляет точку интереса вместе со связанными медиа-материалами.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204",
                    description = "Точка интереса успешно удалена"),
            @ApiResponse(responseCode = "401",
                    description = "Пользователь не авторизован",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "403",
                    description = "Недостаточно прав",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "404",
                    description = "Точка интереса не найдена",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @DeleteMapping("/{pointId}")
    public ResponseEntity<Void> deletePoint(
            @Parameter(description = "Идентификатор точки интереса", example = "1")
            @PathVariable Long pointId
    ) {
        pointService.deletePoint(pointId);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Загрузить медиа-материал к точке",
            description = "Загружает фото, видео или аудио к существующей точке интереса.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Медиа-материал успешно загружен",
                    content = @Content(schema = @Schema(implementation = AdminPointMediaItem.class))),
            @ApiResponse(responseCode = "400",
                    description = "Некорректные параметры запроса или файла",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "401",
                    description = "Пользователь не авторизован",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "403",
                    description = "Недостаточно прав",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "404",
                    description = "Точка интереса не найдена",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @PostMapping(value = "/{pointId}/media", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<AdminPointMediaItem> uploadPointMedia(
            @Parameter(description = "Идентификатор точки интереса", example = "1")
            @PathVariable Long pointId,
            @Parameter(description = "Файл медиа-материала (фото, видео, аудио)")
            @RequestPart("file") MultipartFile file,
            @Valid @RequestPart("metadata") AdminUploadPointMediaRequest metadata
    ) {
        AdminPointMediaItem response = pointService.uploadPointMedia(pointId, file, metadata);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Удалить медиа-материал точки",
            description = "Удаляет медиа-материал, связанный с точкой интереса.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204",
                    description = "Медиа-материал успешно удалён"),
            @ApiResponse(responseCode = "401",
                    description = "Пользователь не авторизован",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "403",
                    description = "Недостаточно прав",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "404",
                    description = "Точка интереса или медиа-материал не найдены",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @DeleteMapping("/{pointId}/media/{mediaId}")
    public ResponseEntity<Void> deletePointMedia(
            @Parameter(description = "Идентификатор точки интереса", example = "1")
            @PathVariable Long pointId,
            @Parameter(description = "Идентификатор медиа-материала", example = "1")
            @PathVariable Long mediaId
    ) {
        pointService.deletePointMedia(pointId, mediaId);
        return ResponseEntity.noContent().build();
    }
}