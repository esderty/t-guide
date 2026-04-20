package t.lab.guide.service.mock;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import t.lab.guide.dto.admin.user.AdminPatchUserRequest;
import t.lab.guide.dto.admin.user.AdminUserDetailResponse;
import t.lab.guide.dto.admin.user.AdminUserPageResponse;
import t.lab.guide.dto.admin.user.AdminUserShortItem;
import t.lab.guide.dto.user.PatchUserRequest;
import t.lab.guide.dto.user.UserResponse;
import t.lab.guide.entity.enums.UserLanguage;
import t.lab.guide.entity.enums.UserRole;
import t.lab.guide.service.SecurityService;
import t.lab.guide.service.UserService;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.IntStream;

@Service
@Profile("demo")
@RequiredArgsConstructor
public class MockUserService implements UserService {
    private final SecurityService securityService;

    @Override
    public UserResponse getCurrentUser(){
        Long userId = securityService.getCurrentUserId();
        return UserResponse.builder()
                .id(userId)
                .username("username" + userId)
                .email("user" + userId + "@example.com")
                .name("User " + userId)
                .lang(UserLanguage.RU)
                .role(UserRole.USER)
                .build();
    }

    @Override
    public UserResponse patchUser(PatchUserRequest request){
        Long userId = securityService.getCurrentUserId();
        return UserResponse.builder()
                .id(userId)
                .username(request.username() != null ? request.username() : "username" + userId)
                .email(request.email() != null ? request.email() : "user" + userId + "@example.com")
                .name(request.name() != null ? request.name() : "User " + userId)
                .lang(request.lang() != null ? request.lang() : UserLanguage.RU)
                .role(UserRole.USER)
                .build();
    }

    @Override
    public AdminUserPageResponse getUsersPage(int page, int size, String sortBy, String sortDir, String search){
        long totalElements = 531;
        page = Math.max(page, 0);
        size = Math.min(Math.max(size, 0), 100);
        int totalPages = (int) Math.ceil((double) totalElements / size);
        int from = page * size;
        int to = (int) Math.min(from + size, totalElements);
        int count = Math.max(0, to - from);

        List<AdminUserShortItem> users = IntStream.range(0, count)
                .mapToObj(i -> {
                    long id = from + i + 1L;
                    UserRole role = id % 10 == 0 ? UserRole.ADMIN : UserRole.USER;
                    boolean isActive = id % 7 != 0;
                    return new AdminUserShortItem(
                            id,
                            "username" + id,
                            "user" + id + "@example.com",
                            role,
                            isActive,
                            OffsetDateTime.now().minusDays(id)
                    );
                })
                .toList();

        return new AdminUserPageResponse(users, page, size, totalElements, totalPages);
    }

    @Override
    public AdminUserDetailResponse getUserDetail(Long userId){
        UserRole role = userId % 10 == 0 ? UserRole.ADMIN : UserRole.USER;
        boolean isActive = userId % 7 != 0;
        return AdminUserDetailResponse.builder()
                .id(userId)
                .username("username" + userId)
                .email("user" + userId + "@example.com")
                .name("User " + userId)
                .lang(UserLanguage.RU)
                .role(role)
                .isActive(isActive)
                .createdAt(OffsetDateTime.now().minusDays(userId))
                .updatedAt(OffsetDateTime.now().minusDays(userId / 2))
                .build();
    }

    @Override
    public AdminUserDetailResponse patchUserByAdmin(Long userId, AdminPatchUserRequest request){
        UserRole role = userId % 10 == 0 ? UserRole.ADMIN : UserRole.USER;
        boolean isActive = userId % 7 != 0;
        return AdminUserDetailResponse.builder()
                .id(userId)
                .username(request.username() != null ? request.username() : "username" + userId)
                .email(request.email() != null ? request.email() : "user" + userId + "@example.com")
                .name("User " + userId)
                .lang(request.lang() != null ? request.lang() : UserLanguage.RU)
                .role(request.role() != null ? request.role() : role)
                .isActive(request.isActive() != null ? request.isActive() : isActive)
                .createdAt(OffsetDateTime.now().minusDays(userId))
                .updatedAt(OffsetDateTime.now())
                .build();
    }


    @Override
    public boolean userExists(Long userId) {
        return true;
    }
}
