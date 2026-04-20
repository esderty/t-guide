package t.lab.guide.service;

import t.lab.guide.dto.admin.category.AdminCreateCategoryRequest;
import t.lab.guide.dto.admin.category.AdminPatchCategoryRequest;
import t.lab.guide.dto.point.category.CategoryItem;
import t.lab.guide.dto.point.category.CategoryListResponse;

public interface PointCategoryService {

    CategoryListResponse getAllCategories();

    CategoryItem createCategory(AdminCreateCategoryRequest request);

    CategoryItem patchCategory(Long id, AdminPatchCategoryRequest request);

    void deleteCategory(Long id);

    //TODO когда будут entity(model), добавить сервисные методы
}