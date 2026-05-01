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
import org.springdoc.core.annotations.ParameterObject
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import t.lab.guide.dto.ApiErrorResponse
import t.lab.guide.dto.admin.user.AdminPatchUserRequest
import t.lab.guide.dto.admin.user.AdminUserDetailResponse
import t.lab.guide.dto.admin.user.AdminUserPageQuery
import t.lab.guide.dto.admin.user.AdminUserPageResponse
import t.lab.guide.service.UserService

@Validated
@Tag(name = "Admin Users", description = "Управление пользователями")
@RestController
@RequestMapping("/admin/users")
class AdminUserController(
    private val userService: UserService,
) {
    @Operation(
        summary = "Список пользователей",
        description = "Возвращает страницу пользователей с пагинацией, сортировкой и текстовым поиском",
        security = [SecurityRequirement(name = "bearerAuth")],
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Страница пользователей успешно получена",
                content = [Content(schema = Schema(implementation = AdminUserPageResponse::class))],
            ), ApiResponse(
                responseCode = "400",
                description = "Некорректные параметры запроса",
                content = [Content(schema = Schema(implementation = ApiErrorResponse::class))],
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
    @GetMapping("/page")
    fun getUsersPage(
        @ParameterObject @Valid query: AdminUserPageQuery,
    ): ResponseEntity<AdminUserPageResponse> = ResponseEntity.ok(userService.getUsersPage(query))

    @Operation(
        summary = "Детали пользователя",
        description = "Возвращает полную информацию о пользователе по его идентификатору. Доступно только администратору.",
        security = [SecurityRequirement(name = "bearerAuth")],
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Информация о пользователе успешно получена",
                content = [Content(schema = Schema(implementation = AdminUserDetailResponse::class))],
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
                description = "Пользователь не найден",
                content = [Content(schema = Schema(implementation = ApiErrorResponse::class))],
            ),
        ],
    )
    @GetMapping("/{userId}")
    fun getUserById(
        @Parameter(description = "Идентификатор пользователя", example = "1") @PathVariable userId: Long,
    ): ResponseEntity<AdminUserDetailResponse> {
        val user: AdminUserDetailResponse = userService.getUserDetail(userId)
        return ResponseEntity.ok(user)
    }

    @Operation(
        summary = "Обновить пользователя",
        description = "Частично обновляет данные пользователя от имени администратора.",
        security = [SecurityRequirement(name = "bearerAuth")],
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Пользователь успешно обновлён",
                content = [Content(schema = Schema(implementation = AdminUserDetailResponse::class))],
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
                description = "Пользователь не найден",
                content = [Content(schema = Schema(implementation = ApiErrorResponse::class))],
            ), ApiResponse(
                responseCode = "409",
                description = "Пользователь с таким username или email уже существует",
                content = [Content(schema = Schema(implementation = ApiErrorResponse::class))],
            ),
        ],
    )
    @PatchMapping("/{userId}")
    fun patchUserById(
        @Parameter(description = "Идентификатор пользователя", example = "1") @PathVariable userId: Long,
        @Valid @RequestBody request: AdminPatchUserRequest,
    ): ResponseEntity<AdminUserDetailResponse> {
        val response: AdminUserDetailResponse = userService.patchUserByAdmin(userId, request)
        return ResponseEntity.ok(response)
    }
}
