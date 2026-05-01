package t.lab.guide.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import t.lab.guide.dto.ApiErrorResponse
import t.lab.guide.dto.excursion.CreateCustomExcursionRequest
import t.lab.guide.dto.excursion.ExcursionDetailResponse
import t.lab.guide.dto.excursion.ExcursionListResponse
import t.lab.guide.dto.excursion.ExcursionSearchRequest
import t.lab.guide.dto.excursion.ExcursionShortItem
import t.lab.guide.dto.excursion.SetExcursionPointsRequest
import t.lab.guide.dto.excursion.UpdateCustomExcursionRequest
import t.lab.guide.dto.excursion.command.toCommand
import t.lab.guide.service.ExcursionService

@Tag(name = "Excursions", description = "Операции с экскурсиями")
@RestController
@RequestMapping("/excursions")
class ExcursionController(
    val excursionService: ExcursionService,
) {
    @Operation(
        summary = "Поиск экскурсий",
        description = "Возвращает список экскурсий, удовлетворяющих условиям фильтра (категории, длительность, тип маршрута и т.д.)",
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Экскурсии успешно найдены",
                content = [Content(schema = Schema(implementation = ExcursionListResponse::class))],
            ),
            ApiResponse(
                responseCode = "400",
                description = "Некорректные параметры запроса",
                content = [Content(schema = Schema(implementation = ApiErrorResponse::class))],
            ),
        ],
    )
    @PostMapping("/search")
    fun searchExcursions(
        @Valid @RequestBody request: ExcursionSearchRequest,
    ): ResponseEntity<ExcursionListResponse> {
        val response: ExcursionListResponse = excursionService.searchExcursions(request.toCommand())
        return ResponseEntity.ok(response)
    }

    @Operation(
        summary = "Детали экскурсии",
        description = "Возвращает подробную информацию об экскурсии, включая список точек маршрута",
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Информация об экскурсии успешно получена",
                content = [Content(schema = Schema(implementation = ExcursionDetailResponse::class))],
            ),
            ApiResponse(
                responseCode = "404",
                description = "Экскурсия с указанным идентификатором не найдена",
                content = [Content(schema = Schema(implementation = ApiErrorResponse::class))],
            ),
        ],
    )
    @GetMapping("/{excursionId}")
    fun getExcursionDetail(
        @Parameter(description = "Идентификатор экскурсии", example = "1") @PathVariable excursionId: Long,
    ): ResponseEntity<ExcursionDetailResponse> {
        val response: ExcursionDetailResponse = excursionService.getExcursionDetail(excursionId)
        return ResponseEntity.ok(response)
    }

    @Operation(
        summary = "Получить пользовательские экскурсии",
        description = "Возвращает список экскурсий, созданных текущим пользователем.",
        security = [SecurityRequirement(name = "bearerAuth")],
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Экскурсии успешно получены",
                content = [Content(schema = Schema(implementation = ExcursionListResponse::class))],
            ),
            ApiResponse(
                responseCode = "401",
                description = "Пользователь не авторизован",
                content = [Content(schema = Schema(implementation = ApiErrorResponse::class))],
            ),
        ],
    )
    @GetMapping("/my")
    fun getUserCustomExcursions(): ResponseEntity<ExcursionListResponse> =
        ResponseEntity.ok(excursionService.getUserCustomExcursions())

    @Operation(
        summary = "Создать кастомную экскурсию",
        description = "Создаёт пользовательскую экскурсию. Доступно только авторизованным пользователям.",
        security = [SecurityRequirement(name = "bearerAuth")],
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Экскурсия успешно создана",
                content = [Content(schema = Schema(implementation = ExcursionDetailResponse::class))],
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
        ],
    )
    @PostMapping
    fun createCustomExcursion(
        @Valid @RequestBody request: CreateCustomExcursionRequest,
    ): ResponseEntity<ExcursionDetailResponse> {
        val response: ExcursionDetailResponse = excursionService.createCustomExcursion(request.toCommand())
        return ResponseEntity.ok(response)
    }

    @Operation(
        summary = "Обновить кастомную экскурсию",
        description = "Частично обновляет данные пользовательской экскурсии",
        security = [SecurityRequirement(name = "bearerAuth")],
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Экскурсия успешно обновлена",
                content = [Content(schema = Schema(implementation = ExcursionShortItem::class))],
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
                description = "Нет прав на изменение экскурсии",
                content = [Content(schema = Schema(implementation = ApiErrorResponse::class))],
            ),
            ApiResponse(
                responseCode = "404",
                description = "Экскурсия не найдена",
                content = [Content(schema = Schema(implementation = ApiErrorResponse::class))],
            ),
        ],
    )
    @PatchMapping("/{excursionId}")
    fun updateCustomExcursion(
        @Parameter(description = "Идентификатор экскурсии", example = "1") @PathVariable excursionId: Long,
        @Valid @RequestBody request: UpdateCustomExcursionRequest,
    ): ResponseEntity<ExcursionShortItem> {
        val response: ExcursionShortItem = excursionService.updateCustomExcursion(excursionId, request)
        return ResponseEntity.ok(response)
    }

    @Operation(
        summary = "Задать точки маршрута",
        description = "Полностью заменяет список точек маршрута кастомной экскурсии. Доступно только владельцу.",
        security = [SecurityRequirement(name = "bearerAuth")],
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Точки маршрута успешно обновлены",
                content = [Content(schema = Schema(implementation = ExcursionDetailResponse::class))],
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
                description = "Нет прав на изменение экскурсии",
                content = [Content(schema = Schema(implementation = ApiErrorResponse::class))],
            ),
            ApiResponse(
                responseCode = "404",
                description = "Экскурсия не найдена",
                content = [Content(schema = Schema(implementation = ApiErrorResponse::class))],
            ),
        ],
    )
    @PutMapping("/{excursionId}/points")
    fun setExcursionPoints(
        @Parameter(description = "Идентификатор экскурсии", example = "1") @PathVariable excursionId: Long,
        @Valid @RequestBody request: SetExcursionPointsRequest,
    ): ResponseEntity<ExcursionDetailResponse> {
        val response: ExcursionDetailResponse = excursionService.setExcursionPoints(excursionId, request.toCommand())
        return ResponseEntity.ok(response)
    }

    @Operation(
        summary = "Удалить кастомную экскурсию",
        description = "Удаляет пользовательскую экскурсию. Доступно только владельцу.",
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
                description = "Нет прав на удаление экскурсии",
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
    fun deleteCustomExcursion(
        @Parameter(description = "Идентификатор экскурсии", example = "1") @PathVariable excursionId: Long,
    ): ResponseEntity<Void> {
        excursionService.deleteCustomExcursion(excursionId)
        return ResponseEntity.noContent().build()
    }

    @Operation(
        summary = "Получить избранные экскурсии",
        description = "Возвращает список экскурсий, добавленных в избранное текущим пользователем.",
        security = [SecurityRequirement(name = "bearerAuth")],
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Избранные экскурсии успешно получены",
                content = [Content(schema = Schema(implementation = ExcursionListResponse::class))],
            ),
            ApiResponse(
                responseCode = "401",
                description = "Пользователь не авторизован",
                content = [Content(schema = Schema(implementation = ApiErrorResponse::class))],
            ),
        ],
    )
    @GetMapping("/favorites")
    fun getFavorites(): ResponseEntity<ExcursionListResponse> =
        ResponseEntity.ok(
            excursionService.getUserFavoriteExcursions(),
        )

    @Operation(
        summary = "Добавить в избранное",
        description = "Добавляет экскурсию в список избранного текущего пользователя",
        security = [SecurityRequirement(name = "bearerAuth")],
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "204",
                description = "Экскурсия добавлена в избранное",
            ),
            ApiResponse(
                responseCode = "401",
                description = "Пользователь не авторизован",
                content = [Content(schema = Schema(implementation = ApiErrorResponse::class))],
            ),
            ApiResponse(
                responseCode = "404",
                description = "Экскурсия не найдена",
                content = [Content(schema = Schema(implementation = ApiErrorResponse::class))],
            ),
        ],
    )
    @PostMapping("/{excursionId}/favorite")
    fun favoriteExcursion(
        @Parameter(description = "Идентификатор экскурсии", example = "1") @PathVariable excursionId: Long,
    ): ResponseEntity<Void> {
        excursionService.favoriteExcursion(excursionId)
        return ResponseEntity.noContent().build()
    }

    @Operation(
        summary = "Убрать из избранного",
        description = "Удаляет экскурсию из списка избранного текущего пользователя",
        security = [SecurityRequirement(name = "bearerAuth")],
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "204",
                description = "Экскурсия убрана из избранного",
            ),
            ApiResponse(
                responseCode = "401",
                description = "Пользователь не авторизован",
                content = [Content(schema = Schema(implementation = ApiErrorResponse::class))],
            ),
            ApiResponse(
                responseCode = "404",
                description = "Экскурсия не найдена",
                content = [Content(schema = Schema(implementation = ApiErrorResponse::class))],
            ),
        ],
    )
    @PostMapping("/{excursionId}/unfavorite")
    fun unfavoriteExcursion(
        @Parameter(description = "Идентификатор экскурсии", example = "1") @PathVariable excursionId: Long,
    ): ResponseEntity<Void> {
        excursionService.unfavoriteExcursion(excursionId)
        return ResponseEntity.noContent().build()
    }
}
