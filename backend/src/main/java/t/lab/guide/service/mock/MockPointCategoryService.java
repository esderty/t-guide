package t.lab.guide.service.mock;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import t.lab.guide.dto.admin.category.AdminCreateCategoryRequest;
import t.lab.guide.dto.admin.category.AdminPatchCategoryRequest;
import t.lab.guide.dto.point.category.CategoryItem;
import t.lab.guide.dto.point.category.CategoryListResponse;
import t.lab.guide.exception.NotFoundException;
import t.lab.guide.service.PointCategoryService;

import java.util.List;

@Service
@Profile("demo")
public class MockPointCategoryService implements PointCategoryService {

    private static final List<CategoryItem> categories = List.of(
            new CategoryItem(1L, "Достопримечательности", "attraction"),
            new CategoryItem(2L, "Шопинг", "shop"),
            new CategoryItem(3L, "Музеи", "museum"),
            new CategoryItem(4L, "Парки", "park"),
            new CategoryItem(5L, "Еда", "food")
    );

    @Override
    public CategoryListResponse getAllCategories() {
        return new CategoryListResponse(categories);
    }

    @Override
    public CategoryItem createCategory(AdminCreateCategoryRequest request) {
        long newId = categories.size() + 1L;
        return new CategoryItem(newId, request.name(), request.slug());
    }

    @Override
    public CategoryItem patchCategory(Long id, AdminPatchCategoryRequest request) {
        CategoryItem current = categories.stream()
                .filter(c -> c.id().equals(id))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Категория с id=" + id + " не найдена"));
        return new CategoryItem(
                current.id(),
                request.name() != null ? request.name() : current.name(),
                request.slug() != null ? request.slug() : current.slug()
        );
    }

    @Override
    public void deleteCategory(Long id) {
        categories.stream()
                .filter(c -> c.id().equals(id))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Категория с id=" + id + " не найдена"));
    }
}