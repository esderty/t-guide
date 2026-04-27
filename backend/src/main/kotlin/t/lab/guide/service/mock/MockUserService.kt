package t.lab.guide.service.mock

import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service
import t.lab.guide.dto.admin.user.AdminPatchUserRequest
import t.lab.guide.dto.admin.user.AdminUserDetailResponse
import t.lab.guide.dto.admin.user.AdminUserPageQuery
import t.lab.guide.dto.admin.user.AdminUserPageResponse
import t.lab.guide.dto.admin.user.AdminUserShortItem
import t.lab.guide.dto.user.PatchUserRequest
import t.lab.guide.dto.user.UserResponse
import t.lab.guide.enums.UserLanguage
import t.lab.guide.enums.UserRole
import t.lab.guide.service.SecurityService
import t.lab.guide.service.UserService
import java.time.OffsetDateTime

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
            username = request.username ?: "username$userId",
            email = request.email ?: "user$userId@example.com",
            name = request.name ?: "User $userId",
            language = request.language ?: UserLanguage.RU,
            role = UserRole.USER,
        )
    }

    override fun getUsersPage(query: AdminUserPageQuery): AdminUserPageResponse {
        val totalElements = 531L
        val totalPages =
            if (query.size == 0) {
                0
            } else {
                ((totalElements + query.size - 1) / query.size).toInt()
            }
        val from = query.page * query.size
        val to = minOf((from + query.size).toLong(), totalElements).toInt()
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
            page = query.page,
            size = query.size,
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
}
