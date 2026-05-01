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
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import t.lab.guide.dto.ApiErrorResponse
import t.lab.guide.dto.admin.category.AdminCreateCategoryRequest
import t.lab.guide.dto.admin.category.AdminPatchCategoryRequest
import t.lab.guide.dto.admin.category.command.toCommand
import t.lab.guide.dto.category.CategoryItem
import t.lab.guide.dto.category.CategoryListResponse
import t.lab.guide.service.PointCategoryService

@Tag(name = "Admin Categories", description = "Управление категориями точек интереса")
@RestController
@RequestMapping("/admin/categories")
class AdminCategoryController(
    private val pointCategoryService: PointCategoryService,
) {
    @Operation(
        summary = "Список категорий",
        description = "Возвращает полный список категорий точек интереса для административного интерфейса.",
        security = [SecurityRequirement(name = "bearerAuth")],
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Список категорий успешно получен",
                content = [Content(schema = Schema(implementation = CategoryListResponse::class))],
            ), ApiResponse(
                responseCode = "401",
                description = "Пользователь не авторизован",
                content = [Content(schema = Schema(implementation = ApiErrorResponse::class))],
            ), ApiResponse(
                responseCode = "403",
                description = "Недостаточно прав",
                content = [Content(schema = Schema(implementation = ApiErrorResponse::class))],
            ),
        ],
    )
    @GetMapping
    fun getAllCategories(): ResponseEntity<CategoryListResponse> {
        val response = pointCategoryService.getAllCategories()
        return ResponseEntity.ok(response)
    }

    @Operation(
        summary = "Создать категорию",
        description = "Создаёт новую категорию точек интереса с указанным именем и slug.",
        security = [SecurityRequirement(name = "bearerAuth")],
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Категория успешно создана",
                content = [Content(schema = Schema(implementation = CategoryItem::class))],
            ), ApiResponse(
                responseCode = "400",
                description = "Некорректные данные запроса",
                content = [Content(schema = Schema(implementation = ApiErrorResponse::class))],
            ), ApiResponse(
                responseCode = "401",
                description = "Пользователь не авторизован",
                content = [Content(schema = Schema(implementation = ApiErrorResponse::class))],
            ), ApiResponse(
                responseCode = "403",
                description = "Недостаточно прав",
                content = [Content(schema = Schema(implementation = ApiErrorResponse::class))],
            ), ApiResponse(
                responseCode = "409",
                description = "Категория с таким именем или slug уже существует",
                content = [Content(schema = Schema(implementation = ApiErrorResponse::class))],
            ),
        ],
    )
    @PostMapping
    fun createCategory(
        @Valid @RequestBody request: AdminCreateCategoryRequest,
    ): ResponseEntity<CategoryItem> {
        val response = pointCategoryService.createCategory(request.toCommand())
        return ResponseEntity.ok(response)
    }

    @Operation(
        summary = "Обновить категорию",
        description = "Частично обновляет данные категории (имя или slug). Передаются только изменяемые поля.",
        security = [SecurityRequirement(name = "bearerAuth")],
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Категория успешно обновлена",
                content = [Content(schema = Schema(implementation = CategoryItem::class))],
            ), ApiResponse(
                responseCode = "400",
                description = "Некорректные данные запроса",
                content = [Content(schema = Schema(implementation = ApiErrorResponse::class))],
            ), ApiResponse(
                responseCode = "401",
                description = "Пользователь не авторизован",
                content = [Content(schema = Schema(implementation = ApiErrorResponse::class))],
            ), ApiResponse(
                responseCode = "403",
                description = "Недостаточно прав",
                content = [Content(schema = Schema(implementation = ApiErrorResponse::class))],
            ), ApiResponse(
                responseCode = "404",
                description = "Категория не найдена",
                content = [Content(schema = Schema(implementation = ApiErrorResponse::class))],
            ), ApiResponse(
                responseCode = "409",
                description = "Категория с таким именем или slug уже существует",
                content = [Content(schema = Schema(implementation = ApiErrorResponse::class))],
            ),
        ],
    )
    @PatchMapping("/{categoryId}")
    fun patchCategory(
        @Parameter(description = "Идентификатор категории", example = "1") @PathVariable categoryId: Long,
        @Valid @RequestBody request: AdminPatchCategoryRequest,
    ): ResponseEntity<CategoryItem> {
        val response = pointCategoryService.patchCategory(categoryId, request)
        return ResponseEntity.ok(response)
    }

    @Operation(
        summary = "Удалить категорию",
        description = "Удаляет категорию точек интереса по её идентификатору.",
        security = [SecurityRequirement(name = "bearerAuth")],
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "204",
                description = "Категория успешно удалена",
            ), ApiResponse(
                responseCode = "401",
                description = "Пользователь не авторизован",
                content = [Content(schema = Schema(implementation = ApiErrorResponse::class))],
            ), ApiResponse(
                responseCode = "403",
                description = "Недостаточно прав",
                content = [Content(schema = Schema(implementation = ApiErrorResponse::class))],
            ), ApiResponse(
                responseCode = "404",
                description = "Категория не найдена",
                content = [Content(schema = Schema(implementation = ApiErrorResponse::class))],
            ), ApiResponse(
                responseCode = "409",
                description = "Категорию нельзя удалить, так как к ней привязаны точки интереса",
                content = [Content(schema = Schema(implementation = ApiErrorResponse::class))],
            ),
        ],
    )
    @DeleteMapping("/{categoryId}")
    fun deleteCategory(
        @Parameter(description = "Идентификатор категории", example = "1") @PathVariable categoryId: Long,
    ): ResponseEntity<Void> {
        pointCategoryService.deleteCategory(categoryId)
        return ResponseEntity.noContent().build()
    }
}
