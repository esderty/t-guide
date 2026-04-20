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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import t.lab.guide.dto.ApiErrorResponse;
import t.lab.guide.dto.admin.user.AdminPatchUserRequest;
import t.lab.guide.dto.admin.user.AdminUserDetailResponse;
import t.lab.guide.dto.admin.user.AdminUserPageResponse;
import t.lab.guide.service.UserService;

@Tag(name = "Admin Users", description = "Управление пользователями")
@RestController
@RequestMapping("/admin/users")
@RequiredArgsConstructor
public class AdminUserController {
    private final UserService userService;

    @Operation(
            summary = "Список пользователей",
            description = "Возвращает страницу пользователей с пагинацией, сортировкой и текстовым поиском по username/email/name. Доступно только администратору.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Страница пользователей успешно получена",
                    content = @Content(schema = @Schema(implementation = AdminUserPageResponse.class))),
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
    public ResponseEntity<AdminUserPageResponse> getUsersPage(
            @Parameter(description = "Номер страницы (с 0)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Размер страницы", example = "25")
            @RequestParam(defaultValue = "25") int size,
            @Parameter(description = "Поле для сортировки (например, id, username, createdAt)", example = "createdAt")
            @RequestParam(required = false) String sortBy,
            @Parameter(description = "Направление сортировки: ASC или DESC", example = "DESC")
            @RequestParam(required = false) String sortDirection,
            @Parameter(description = "Строка поиска по username/email/name", example = "ivan")
            @RequestParam(required = false) String search
    ){
        AdminUserPageResponse response = userService.getUsersPage(page, size,  sortBy, sortDirection, search);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Детали пользователя",
            description = "Возвращает полную информацию о пользователе по его идентификатору. Доступно только администратору.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Информация о пользователе успешно получена",
                    content = @Content(schema = @Schema(implementation = AdminUserDetailResponse.class))),
            @ApiResponse(responseCode = "401",
                    description = "Пользователь не авторизован",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "403",
                    description = "Недостаточно прав",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "404",
                    description = "Пользователь не найден",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @GetMapping("/{userId}")
    public ResponseEntity<AdminUserDetailResponse> getUserById(
            @Parameter(description = "Идентификатор пользователя", example = "1")
            @PathVariable Long userId){
        AdminUserDetailResponse user = userService.getUserDetail(userId);
        return ResponseEntity.ok(user);
    }

    @Operation(
            summary = "Обновить пользователя",
            description = "Частично обновляет данные пользователя от имени администратора (роль, статус активности, основные поля). Передаются только изменяемые поля.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Пользователь успешно обновлён",
                    content = @Content(schema = @Schema(implementation = AdminUserDetailResponse.class))),
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
                    description = "Пользователь не найден",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "409",
                    description = "Пользователь с таким username или email уже существует",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @PatchMapping("/{userId}")
    public ResponseEntity<AdminUserDetailResponse> patchUserById(
            @Parameter(description = "Идентификатор пользователя", example = "1")
            @PathVariable Long userId,
            @Valid @RequestBody AdminPatchUserRequest request){
        AdminUserDetailResponse response = userService.patchUserByAdmin(userId, request);
        return ResponseEntity.ok(response);
    }
}