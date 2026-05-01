package t.lab.guide.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import t.lab.guide.dto.ApiErrorResponse
import t.lab.guide.dto.auth.AuthRequest
import t.lab.guide.dto.auth.AuthResponse
import t.lab.guide.dto.auth.LogoutRequest
import t.lab.guide.dto.auth.RefreshRequest
import t.lab.guide.dto.auth.RegistrationRequest
import t.lab.guide.dto.auth.RegistrationResponse
import t.lab.guide.dto.auth.TokenPairResponse
import t.lab.guide.dto.auth.command.toCommand
import t.lab.guide.service.AuthService

@Tag(name = "Authentication", description = "Операции аутентификации пользователей")
@RestController
@RequestMapping("/auth")
class AuthController(
    private val authService: AuthService,
) {
    @Operation(
        summary = "Регистрация нового пользователя",
        description = "Позволяет зарегистрироваться новому пользователю в системе",
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Пользователь успешно зарегистрирован",
                content = [Content(schema = Schema(implementation = RegistrationResponse::class))],
            ),
            ApiResponse(
                responseCode = "400",
                description = "Некорректные данные запроса",
                content = [Content(schema = Schema(implementation = ApiErrorResponse::class))],
            ),
            ApiResponse(
                responseCode = "409",
                description = "Пользователь с таким логином или email уже существует",
                content = [Content(schema = Schema(implementation = ApiErrorResponse::class))],
            ),
        ],
    )
    @PostMapping("/registration")
    fun registerUser(
        @Valid @RequestBody request: RegistrationRequest,
    ): ResponseEntity<RegistrationResponse> {
        val response: RegistrationResponse = authService.registerUser(request.toCommand())
        return ResponseEntity.ok(response)
    }

    @Operation(summary = "Вход в систему", description = "Аутентификация пользователя по логину и паролю")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Аутентификация прошла успешно",
                content = [Content(schema = Schema(implementation = AuthResponse::class))],
            ),
            ApiResponse(
                responseCode = "400",
                description = "Некорректные данные запроса",
                content = [Content(schema = Schema(implementation = ApiErrorResponse::class))],
            ),
            ApiResponse(
                responseCode = "401",
                description = "Неверный логин или пароль",
                content = [Content(schema = Schema(implementation = ApiErrorResponse::class))],
            ),
        ],
    )
    @PostMapping("/login")
    fun authenticateUser(
        @Valid @RequestBody request: AuthRequest,
    ): ResponseEntity<AuthResponse> {
        val response: AuthResponse = authService.authenticateUser(request.toCommand())
        return ResponseEntity.ok(response)
    }

    @Operation(summary = "Выход из системы", description = "Инвалидирует refresh-токен пользователя")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "204",
                description = "Выход выполнен успешно",
            ),
            ApiResponse(
                responseCode = "400",
                description = "Некорректные данные запроса",
                content = [Content(schema = Schema(implementation = ApiErrorResponse::class))],
            ),
            ApiResponse(
                responseCode = "401",
                description = "Refresh-токен недействителен или истёк",
                content = [Content(schema = Schema(implementation = ApiErrorResponse::class))],
            ),
        ],
    )
    @PostMapping("/logout")
    fun logoutUser(
        @Valid @RequestBody request: LogoutRequest,
    ): ResponseEntity<Void> {
        authService.logoutUser(request.toCommand())
        return ResponseEntity.noContent().build()
    }

    @Operation(
        summary = "Обновление токена доступа",
        description = "Выдаёт новую пару токенов по действующему refresh-токену",
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Токены успешно обновлены",
                content = [Content(schema = Schema(implementation = TokenPairResponse::class))],
            ),
            ApiResponse(
                responseCode = "400",
                description = "Некорректные данные запроса",
                content = [Content(schema = Schema(implementation = ApiErrorResponse::class))],
            ),
            ApiResponse(
                responseCode = "401",
                description = "Refresh-токен недействителен или истёк",
                content = [Content(schema = Schema(implementation = ApiErrorResponse::class))],
            ),
        ],
    )
    @PostMapping("/refresh")
    fun refreshUser(
        @Valid @RequestBody request: RefreshRequest,
    ): ResponseEntity<TokenPairResponse> {
        val response: TokenPairResponse = authService.refreshToken(request.toCommand())
        return ResponseEntity.ok(response)
    }
}
