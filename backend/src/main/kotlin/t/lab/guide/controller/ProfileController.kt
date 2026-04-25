package t.lab.guide.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import t.lab.guide.dto.ApiErrorResponse
import t.lab.guide.dto.auth.ChangePasswordRequest
import t.lab.guide.dto.auth.TokenPairResponse
import t.lab.guide.dto.user.PatchUserRequest
import t.lab.guide.dto.user.UserResponse
import t.lab.guide.service.AuthService
import t.lab.guide.service.UserService

@Tag(name = "Profile", description = "Управление профилем текущего пользователя")
@RestController
@RequestMapping("/profile")
class ProfileController(
    private val userService: UserService,
    private val authService: AuthService,
) {
    @Operation(
        summary = "Получить профиль",
        description = "Возвращает данные текущего авторизованного пользователя",
        security = [SecurityRequirement(name = "bearerAuth")],
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Профиль успешно получен",
                content = [Content(schema = Schema(implementation = UserResponse::class))],
            ),
            ApiResponse(
                responseCode = "401",
                description = "Пользователь не авторизован",
                content = [Content(schema = Schema(implementation = ApiErrorResponse::class))],
            ),
        ],
    )
    @GetMapping
    fun getProfile(): ResponseEntity<UserResponse> {
        val response: UserResponse = userService.getCurrentUser()
        return ResponseEntity.ok(response)
    }

    @Operation(
        summary = "Обновить профиль",
        description = "Частично обновляет данные текущего авторизованного пользователя. Передаются только изменяемые поля.",
        security = [SecurityRequirement(name = "bearerAuth")],
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Профиль успешно обновлён",
                content = [Content(schema = Schema(implementation = UserResponse::class))],
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
                responseCode = "409",
                description = "Пользователь с таким username или email уже существует",
                content = [Content(schema = Schema(implementation = ApiErrorResponse::class))],
            ),
        ],
    )
    @PatchMapping
    fun patchProfile(
        @Valid @RequestBody request: PatchUserRequest,
    ): ResponseEntity<UserResponse> {
        val response: UserResponse = userService.patchUser(request)
        return ResponseEntity.ok(response)
    }

    @Operation(
        summary = "Смена пароля",
        description = "Изменяет пароль пользователя и возвращает новую пару токенов.",
        security = [SecurityRequirement(name = "bearerAuth")],
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Пароль успешно изменён",
                content = [Content(schema = Schema(implementation = TokenPairResponse::class))],
            ),
            ApiResponse(
                responseCode = "400",
                description = "Некорректные данные запроса или неверный текущий пароль",
                content = [Content(schema = Schema(implementation = ApiErrorResponse::class))],
            ),
            ApiResponse(
                responseCode = "401",
                description = "Пользователь не авторизован",
                content = [Content(schema = Schema(implementation = ApiErrorResponse::class))],
            ),
        ],
    )
    @PostMapping("/change-password")
    fun changePassword(
        @Valid @RequestBody request: ChangePasswordRequest,
    ): ResponseEntity<TokenPairResponse> {
        val response: TokenPairResponse = authService.changePassword(request)
        return ResponseEntity.ok(response)
    }
}
