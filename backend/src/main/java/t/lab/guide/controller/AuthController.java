package t.lab.guide.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import t.lab.guide.dto.ApiErrorResponse;
import t.lab.guide.dto.auth.*;
import t.lab.guide.service.AuthService;

@Tag(name = "Authentication", description = "Операции аутентификации пользователей")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @Operation(summary = "Регистрация нового пользователя", description = "Позволяет зарегистрироваться новому пользователю в системе")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Пользователь успешно зарегистрирован",
                    content = @Content(schema = @Schema(implementation = RegistrationResponse.class))),
            @ApiResponse(responseCode = "400",
                    description = "Некорректные данные запроса",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "409",
                    description = "Пользователь с таким логином или email уже существует",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @PostMapping("/registration")
    public ResponseEntity<RegistrationResponse> registrationUser (@Valid @RequestBody RegistrationRequest request) {
        RegistrationResponse response = authService.registrationUser(request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Вход в систему", description = "Аутентификация пользователя по логину и паролю")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Аутентификация прошла успешно",
                    content = @Content(schema = @Schema(implementation = AuthResponse.class))),
            @ApiResponse(responseCode = "400",
                    description = "Некорректные данные запроса",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "401",
                    description = "Неверный логин или пароль",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> authenticationUser (@Valid @RequestBody AuthRequest request) {
        AuthResponse response = authService.authenticationUser(request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Выход из системы",
            description = "Инвалидирует refresh-токен пользователя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204",
                    description = "Выход выполнен успешно"),
            @ApiResponse(responseCode = "400",
                    description = "Некорректные данные запроса",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "401",
                    description = "Refresh-токен недействителен или истёк",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @PostMapping("/logout")
    public ResponseEntity<Void> logoutUser (@Valid @RequestBody LogoutRequest request) {
        authService.logoutUser(request);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Смена пароля",
            description = "Изменяет пароль пользователя и возвращает новую пару токенов.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Пароль успешно изменён",
                    content = @Content(schema = @Schema(implementation = TokenPairResponse.class))),
            @ApiResponse(responseCode = "400",
                    description = "Некорректные данные запроса или неверный текущий пароль",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "401",
                    description = "Пользователь не авторизован",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @PostMapping("/change-password")
    public ResponseEntity<TokenPairResponse> changePassword (@Valid @RequestBody ChangePasswordRequest request) {
        TokenPairResponse response = authService.changePassword(request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Обновление токена доступа",
            description = "Выдаёт новую пару токенов по действующему refresh-токену")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Токены успешно обновлены",
                    content = @Content(schema = @Schema(implementation = TokenPairResponse.class))),
            @ApiResponse(responseCode = "400",
                    description = "Некорректные данные запроса",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "401",
                    description = "Refresh-токен недействителен или истёк",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @PostMapping("/refresh")
    public ResponseEntity<TokenPairResponse> refreshUser (@Valid @RequestBody RefreshRequest request) {
        TokenPairResponse response = authService.refreshToken(request);
        return ResponseEntity.ok(response);
    }
}
