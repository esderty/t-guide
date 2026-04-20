package t.lab.guide.service;

import org.springframework.web.multipart.MultipartFile;
import t.lab.guide.dto.admin.point.AdminCreatePointRequest;
import t.lab.guide.dto.admin.point.AdminPatchPointRequest;
import t.lab.guide.dto.admin.point.AdminPointDetailResponse;
import t.lab.guide.dto.admin.point.AdminPointMediaItem;
import t.lab.guide.dto.admin.point.AdminPointPageResponse;
import t.lab.guide.dto.admin.point.AdminUploadPointMediaRequest;
import t.lab.guide.dto.point.PointDetailResponse;
import t.lab.guide.dto.point.PointListResponse;
import t.lab.guide.dto.point.PointSearchRequest;

public interface PointService {

    PointListResponse searchPoints(PointSearchRequest request);

    PointDetailResponse getPointDetail(Long id);

    AdminPointPageResponse getPointsPage(int page, int size, String sortBy, String sortDirection, String search);

    AdminPointDetailResponse getAdminPointDetail(Long id);

    AdminPointDetailResponse createPoint(AdminCreatePointRequest request);

    AdminPointDetailResponse patchPoint(Long id, AdminPatchPointRequest request);

    void deletePoint(Long id);

    AdminPointMediaItem uploadPointMedia(Long pointId, MultipartFile file, AdminUploadPointMediaRequest request);

    void deletePointMedia(Long pointId, Long mediaId);

    //TODO когда будут entity(model), добавить сервисные методы

}