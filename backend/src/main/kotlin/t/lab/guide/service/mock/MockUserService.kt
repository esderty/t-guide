package t.lab.guide.service.mock

import java.time.OffsetDateTime
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service
import t.lab.guide.dto.admin.user.AdminPatchUserRequest
import t.lab.guide.dto.admin.user.AdminUserDetailResponse
import t.lab.guide.dto.admin.user.AdminUserPageResponse
import t.lab.guide.dto.admin.user.AdminUserShortItem
import t.lab.guide.dto.user.PatchUserRequest
import t.lab.guide.dto.user.UserResponse
import t.lab.guide.entity.enums.UserLanguage
import t.lab.guide.entity.enums.UserRole
import t.lab.guide.service.SecurityService
import t.lab.guide.service.UserService

@Service
@Profile("demo")
class MockUserService(
    private val securityService: SecurityService,
) : UserService {
    override fun getCurrentUser(): UserResponse {
        val userId = securityService.getCurrentUserId()
        return UserResponse(
            id = userId,
            username = "username$userId",
            email = "user$userId@example.com",
            name = "User $userId",
            language = UserLanguage.RU,
            role = UserRole.USER,
        )
    }

    override fun patchUser(request: PatchUserRequest): UserResponse {
        val userId = securityService.getCurrentUserId()
        return UserResponse(
            id = userId,
            username = request.userName ?: "username$userId",
            email = request.email ?: "user$userId@example.com",
            name = request.name ?: "User $userId",
            language = request.language ?: UserLanguage.RU,
            role = UserRole.USER,
        )
    }

    override fun getUsersPage(
        page: Int,
        size: Int,
        sortBy: String?,
        sortDir: String?,
        search: String?,
    ): AdminUserPageResponse {
        val totalElements = 531L
        val safePage = page.coerceAtLeast(0)
        val safeSize = size.coerceIn(0, 100)
        val totalPages =
            if (safeSize == 0) {
                0
            } else {
                ((totalElements + safeSize - 1) / safeSize).toInt()
            }
        val from = safePage * safeSize
        val to = minOf((from + safeSize).toLong(), totalElements).toInt()
        val count = (to - from).coerceAtLeast(0)

        val users =
            (0 until count).map { i ->
                val id = (from + i + 1).toLong()
                val role = if (id % 10 == 0L) UserRole.ADMIN else UserRole.USER
                val isActive = id % 7 != 0L
                AdminUserShortItem(
                    id = id,
                    username = "username$id",
                    email = "user$id@example.com",
                    role = role,
                    isActive = isActive,
                    createdAt = OffsetDateTime.now().minusDays(id),
                )
            }
        return AdminUserPageResponse(
            users = users,
            page = safePage,
            size = safeSize,
            totalElements = totalElements,
            totalPages = totalPages,
        )
    }

    override fun getUserDetail(userId: Long): AdminUserDetailResponse {
        val role = if (userId % 10 == 0L) UserRole.ADMIN else UserRole.USER
        val isActive = userId % 7 != 0L
        return AdminUserDetailResponse(
            id = userId,
            username = "username$userId",
            email = "user$userId@example.com",
            name = "User $userId",
            lang = UserLanguage.RU,
            role = role,
            isActive = isActive,
            createdAt = OffsetDateTime.now().minusDays(userId),
            updatedAt = OffsetDateTime.now().minusDays(userId / 2),
        )
    }

    override fun patchUserByAdmin(
        userId: Long,
        request: AdminPatchUserRequest,
    ): AdminUserDetailResponse {
        val role = if (userId % 10 == 0L) UserRole.ADMIN else UserRole.USER
        val isActive = userId % 7 != 0L
        return AdminUserDetailResponse(
            id = userId,
            username = request.username ?: "username$userId",
            email = request.email ?: "user$userId@example.com",
            name = "User $userId",
            lang = request.lang ?: UserLanguage.RU,
            role = request.role ?: role,
            isActive = request.isActive ?: isActive,
            createdAt = OffsetDateTime.now().minusDays(userId),
            updatedAt = OffsetDateTime.now(),
        )
    }

    override fun userExists(userId: Long): Boolean = true
}
