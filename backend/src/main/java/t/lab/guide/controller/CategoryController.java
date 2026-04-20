package t.lab.guide.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import t.lab.guide.dto.point.category.CategoryListResponse;
import t.lab.guide.service.PointCategoryService;

@Tag(name = "Categories", description = "Операции с категориями точек интереса")
@RestController
@RequestMapping("/points/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final PointCategoryService pointCategoryService;

    @Operation(
            summary = "Список категорий",
            description = "Возвращает все доступные категории точек интереса"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Список категорий успешно получен",
                    content = @Content(schema = @Schema(implementation = CategoryListResponse.class)))
    })
    @GetMapping
    public ResponseEntity<CategoryListResponse> getAllCategories() {
        CategoryListResponse response = pointCategoryService.getAllCategories();
        return ResponseEntity.ok(response);
    }
}