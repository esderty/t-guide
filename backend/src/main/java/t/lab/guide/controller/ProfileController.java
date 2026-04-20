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
import org.springframework.web.bind.annotation.*;
import t.lab.guide.dto.ApiErrorResponse;
import t.lab.guide.dto.user.PatchUserRequest;
import t.lab.guide.dto.user.UserResponse;
import t.lab.guide.service.UserService;

@Tag(name = "Profile", description = "Управление профилем текущего пользователя")
@RestController
@RequestMapping("/profile")
@RequiredArgsConstructor
public class ProfileController {
    private final UserService userService;

    @Operation(
            summary = "Получить профиль",
            description = "Возвращает данные текущего авторизованного пользователя",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Профиль успешно получен",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "401",
                    description = "Пользователь не авторизован",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @GetMapping()
    public ResponseEntity<UserResponse> getProfile() {
        UserResponse response = userService.getCurrentUser();
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Обновить профиль",
            description = "Частично обновляет данные текущего авторизованного пользователя. Передаются только изменяемые поля.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Профиль успешно обновлён",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "400",
                    description = "Некорректные данные запроса",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "401",
                    description = "Пользователь не авторизован",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "409",
                    description = "Пользователь с таким username или email уже существует",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @PatchMapping()
    public ResponseEntity<UserResponse> patchProfile(@Valid @RequestBody PatchUserRequest request) {
        UserResponse response = userService.patchUser(request);
        return ResponseEntity.ok(response);
    }
}
