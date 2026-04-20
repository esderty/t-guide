package t.lab.guide.service;

import t.lab.guide.dto.admin.excursion.AdminCreatePrebuiltExcursionRequest;
import t.lab.guide.dto.admin.excursion.AdminExcursionDetailResponse;
import t.lab.guide.dto.admin.excursion.AdminExcursionPageResponse;
import t.lab.guide.dto.admin.excursion.AdminPatchPrebuiltExcursionRequest;
import t.lab.guide.dto.excursion.*;

public interface ExcursionService {

    ExcursionListResponse searchExcursions(ExcursionSearchRequest request);

    ExcursionDetailResponse getExcursionDetail(Long id);

    ExcursionDetailResponse createCustomExcursion (CreateCustomExcursionRequest request);

    ExcursionShortItem updateCustomExcursion (Long id, UpdateCustomExcursionRequest request);

    ExcursionDetailResponse setExcursionPoints (Long id, SetExcursionPointsRequest request);

    void deleteCustomExcursion(Long id);
    
    void favoriteExcursion (Long excursionId);

    void unfavoriteExcursion (Long excursionId);

    AdminExcursionPageResponse getAdminExcursionsPage(int page, int size, String sortBy, String sortDirection, String search);

    AdminExcursionDetailResponse getAdminExcursionDetail(Long id);

    AdminExcursionDetailResponse createPrebuiltExcursion(AdminCreatePrebuiltExcursionRequest request);

    AdminExcursionDetailResponse patchPrebuiltExcursion(Long id, AdminPatchPrebuiltExcursionRequest request);

    AdminExcursionDetailResponse setAdminExcursionPoints(Long id, SetExcursionPointsRequest request);

    void deleteExcursion(Long id);
}