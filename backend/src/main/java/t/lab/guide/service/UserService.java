package t.lab.guide.service;

import t.lab.guide.dto.admin.user.AdminPatchUserRequest;
import t.lab.guide.dto.admin.user.AdminUserDetailResponse;
import t.lab.guide.dto.admin.user.AdminUserPageResponse;
import t.lab.guide.dto.user.PatchUserRequest;
import t.lab.guide.dto.user.UserResponse;

public interface UserService {

    UserResponse getCurrentUser();

    UserResponse patchUser(PatchUserRequest request);

    AdminUserPageResponse getUsersPage(int page, int size, String sortBy, String sortDir, String search);

    AdminUserDetailResponse getUserDetail(Long userId);

    AdminUserDetailResponse patchUserByAdmin(Long userId, AdminPatchUserRequest request);

    //TODO когда будут entity(model), добавить сервисные методы

    boolean userExists(Long userId);

}
