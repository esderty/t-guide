package t.lab.guide.controller.admin

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import t.lab.guide.dto.ApiErrorResponse
import t.lab.guide.dto.admin.excursion.AdminCreatePrebuiltExcursionRequest
import t.lab.guide.dto.admin.excursion.AdminExcursionDetailResponse
import t.lab.guide.dto.admin.excursion.AdminExcursionPageResponse
import t.lab.guide.dto.admin.excursion.AdminPatchPrebuiltExcursionRequest
import t.lab.guide.dto.admin.excursion.command.toCommand
import t.lab.guide.dto.excursion.SetExcursionPointsRequest
import t.lab.guide.dto.excursion.command.toCommand
import t.lab.guide.enums.AdminExcursionSortField
import t.lab.guide.enums.SortDirection
import t.lab.guide.service.ExcursionService

@Tag(name = "Admin Excursions", description = "Управление готовыми экскурсиями")
@RestController
@RequestMapping("/admin/excursions")
@Validated
class AdminExcursionController(
    private val excursionService: ExcursionService,
) {
    @Operation(
        summary = "Список экскурсий",
        description = "Возвращает страницу экскурсий с пагинацией, сортировкой и текстовым поиском по названию.",
        security = [SecurityRequirement(name = "bearerAuth")],
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Страница экскурсий успешно получена",
                content = [Content(schema = Schema(implementation = AdminExcursionPageResponse::class))],
            ),
            ApiResponse(
                responseCode = "400",
                description = "Некорректные параметры запроса",
                content = [Content(schema = Schema(implementation = ApiErrorResponse::class))],
            ),
            ApiResponse(
                responseCode = "401",
                description = "Пользователь не авторизован",
                content = [Content(schema = Schema(implementation = ApiErrorResponse::class))],
            ),
            ApiResponse(
                responseCode = "403",
                description = "Недостаточно прав",
                content = [Content(schema = Schema(implementation = ApiErrorResponse::class))],
            ),
        ],
    )
    @GetMapping("/page")
    fun getExcursionsPage(
        @Parameter(description = "Номер страницы (с 0)", example = "0") @RequestParam(defaultValue = "0") @Min(0) page: Int,
        @Parameter(
            description = "Размер страницы",
            example = "25",
        ) @RequestParam(defaultValue = "25") @Min(0) @Max(100) size: Int,
        @Parameter(
            description = "Поле для сортировки (например, id, title, createdAt)",
            example = "createdAt",
        ) @RequestParam(required = false) sortBy: AdminExcursionSortField?,
        @Parameter(
            description = "Направление сортировки: ASC или DESC",
            example = "DESC",
        ) @RequestParam(required = false) sortDirection: SortDirection?,
        @Parameter(
            description = "Строка поиска по названию",
            example = "центр",
        ) @RequestParam(required = false) search: String?,
    ): ResponseEntity<AdminExcursionPageResponse> {
        val response: AdminExcursionPageResponse =
            excursionService.getAdminExcursionsPage(page, size, sortBy, sortDirection, search)
        return ResponseEntity.ok(response)
    }

    @Operation(
        summary = "Детали экскурсии",
        description = "Возвращает полную информацию об экскурсии по её идентификатору, включая маршрут и служебные поля.",
        security = [SecurityRequirement(name = "bearerAuth")],
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Информация об экскурсии успешно получена",
                content = [Content(schema = Schema(implementation = AdminExcursionDetailResponse::class))],
            ),
            ApiResponse(
                responseCode = "401",
                description = "Пользователь не авторизован",
                content = [Content(schema = Schema(implementation = ApiErrorResponse::class))],
            ),
            ApiResponse(
                responseCode = "403",
                description = "Недостаточно прав",
                content = [Content(schema = Schema(implementation = ApiErrorResponse::class))],
            ),
            ApiResponse(
                responseCode = "404",
                description = "Экскурсия не найдена",
                content = [Content(schema = Schema(implementation = ApiErrorResponse::class))],
            ),
        ],
    )
    @GetMapping("/{excursionId}")
    fun getExcursionById(
        @Parameter(description = "Идентификатор экскурсии", example = "1") @PathVariable excursionId: Long,
    ): ResponseEntity<AdminExcursionDetailResponse> {
        val response: AdminExcursionDetailResponse = excursionService.getAdminExcursionDetail(excursionId)
        return ResponseEntity.ok(response)
    }

    @Operation(
        summary = "Создать готовую экскурсию",
        description = "Создаёт новую готовую (PREBUILT) экскурсию с указанными параметрами и маршрутом.",
        security = [SecurityRequirement(name = "bearerAuth")],
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Экскурсия успешно создана",
                content = [Content(schema = Schema(implementation = AdminExcursionDetailResponse::class))],
            ),
            ApiResponse(
                responseCode = "400",
                description = "Некорректные данные запроса",
                content = [Content(schema = Schema(implementation = ApiErrorResponse::class))],
            ),
            ApiResponse(
                responseCode = "401",
                description = "Пользователь не авторизован",
                content = [Content(schema = Schema(implementation = ApiErrorResponse::class))],
            ),
            ApiResponse(
                responseCode = "403",
                description = "Недостаточно прав",
                content = [Content(schema = Schema(implementation = ApiErrorResponse::class))],
            ),
            ApiResponse(
                responseCode = "404",
                description = "Одна из точек маршрута не найдена",
                content = [Content(schema = Schema(implementation = ApiErrorResponse::class))],
            ),
            ApiResponse(
                responseCode = "409",
                description = "Экскурсия с такими параметрами уже существует",
                content = [Content(schema = Schema(implementation = ApiErrorResponse::class))],
            ),
        ],
    )
    @PostMapping
    fun createPrebuiltExcursion(
        @Valid @RequestBody request: AdminCreatePrebuiltExcursionRequest,
    ): ResponseEntity<AdminExcursionDetailResponse> {
        val response: AdminExcursionDetailResponse = excursionService.createPrebuiltExcursion(request.toCommand())
        return ResponseEntity.ok(response)
    }

    @Operation(
        summary = "Обновить готовую экскурсию",
        description =
            "Частично обновляет данные готовой (PREBUILT) экскурсии." +
                "Передаются только изменяемые поля; маршрут правится отдельным эндпоинтом.",
        security = [SecurityRequirement(name = "bearerAuth")],
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Экскурсия успешно обновлена",
                content = [Content(schema = Schema(implementation = AdminExcursionDetailResponse::class))],
            ),
            ApiResponse(
                responseCode = "400",
                description = "Некорректные данные запроса",
                content = [Content(schema = Schema(implementation = ApiErrorResponse::class))],
            ),
            ApiResponse(
                responseCode = "401",
                description = "Пользователь не авторизован",
                content = [Content(schema = Schema(implementation = ApiErrorResponse::class))],
            ),
            ApiResponse(
                responseCode = "403",
                description = "Недостаточно прав",
                content = [Content(schema = Schema(implementation = ApiErrorResponse::class))],
            ),
            ApiResponse(
                responseCode = "404",
                description = "Экскурсия не найдена",
                content = [Content(schema = Schema(implementation = ApiErrorResponse::class))],
            ),
            ApiResponse(
                responseCode = "409",
                description = "Конфликт параметров экскурсии",
                content = [Content(schema = Schema(implementation = ApiErrorResponse::class))],
            ),
        ],
    )
    @PatchMapping("/{excursionId}")
    fun patchPrebuiltExcursion(
        @Parameter(description = "Идентификатор экскурсии", example = "1") @PathVariable excursionId: Long,
        @Valid @RequestBody request: AdminPatchPrebuiltExcursionRequest,
    ): ResponseEntity<AdminExcursionDetailResponse> {
        val response: AdminExcursionDetailResponse = excursionService.patchPrebuiltExcursion(excursionId, request)
        return ResponseEntity.ok(response)
    }

    @Operation(
        summary = "Заменить маршрут экскурсии",
        description = "Полностью заменяет набор и порядок точек маршрута у существующей экскурсии.",
        security = [SecurityRequirement(name = "bearerAuth")],
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Маршрут экскурсии успешно обновлён",
                content = [Content(schema = Schema(implementation = AdminExcursionDetailResponse::class))],
            ),
            ApiResponse(
                responseCode = "400",
                description = "Некорректные данные запроса",
                content = [Content(schema = Schema(implementation = ApiErrorResponse::class))],
            ),
            ApiResponse(
                responseCode = "401",
                description = "Пользователь не авторизован",
                content = [Content(schema = Schema(implementation = ApiErrorResponse::class))],
            ),
            ApiResponse(
                responseCode = "403",
                description = "Недостаточно прав",
                content = [Content(schema = Schema(implementation = ApiErrorResponse::class))],
            ),
            ApiResponse(
                responseCode = "404",
                description = "Экскурсия или одна из точек маршрута не найдены",
                content = [Content(schema = Schema(implementation = ApiErrorResponse::class))],
            ),
        ],
    )
    @PutMapping("/{excursionId}/points")
    fun setExcursionPoints(
        @Parameter(description = "Идентификатор экскурсии", example = "1") @PathVariable excursionId: Long,
        @Valid @RequestBody request: SetExcursionPointsRequest,
    ): ResponseEntity<AdminExcursionDetailResponse> {
        val response: AdminExcursionDetailResponse = excursionService.setAdminExcursionPoints(excursionId, request.toCommand())
        return ResponseEntity.ok(response)
    }

    @Operation(
        summary = "Удалить экскурсию",
        description = "Удаляет экскурсию вместе со связанными точками маршрута и пользовательскими отметками.",
        security = [SecurityRequirement(name = "bearerAuth")],
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "204",
                description = "Экскурсия успешно удалена",
            ),
            ApiResponse(
                responseCode = "401",
                description = "Пользователь не авторизован",
                content = [Content(schema = Schema(implementation = ApiErrorResponse::class))],
            ),
            ApiResponse(
                responseCode = "403",
                description = "Недостаточно прав",
                content = [Content(schema = Schema(implementation = ApiErrorResponse::class))],
            ),
            ApiResponse(
                responseCode = "404",
                description = "Экскурсия не найдена",
                content = [Content(schema = Schema(implementation = ApiErrorResponse::class))],
            ),
        ],
    )
    @DeleteMapping("/{excursionId}")
    fun deleteExcursion(
        @Parameter(description = "Идентификатор экскурсии", example = "1") @PathVariable excursionId: Long,
    ): ResponseEntity<Void> {
        excursionService.deleteExcursion(excursionId)
        return ResponseEntity.noContent().build()
    }
}
