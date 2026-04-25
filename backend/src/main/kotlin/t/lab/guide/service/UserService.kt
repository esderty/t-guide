package t.lab.guide.service

import t.lab.guide.dto.admin.user.AdminPatchUserRequest
import t.lab.guide.dto.admin.user.AdminUserDetailResponse
import t.lab.guide.dto.admin.user.AdminUserPageResponse
import t.lab.guide.dto.user.PatchUserRequest
import t.lab.guide.dto.user.UserResponse

interface UserService {
    fun getCurrentUser(): UserResponse

    fun patchUser(request: PatchUserRequest): UserResponse

    fun getUsersPage(
        page: Int,
        size: Int,
        sortBy: String?,
        sortDir: String?,
        search: String?,
    ): AdminUserPageResponse

    fun getUserDetail(userId: Long): AdminUserDetailResponse

    fun patchUserByAdmin(
        userId: Long,
        request: AdminPatchUserRequest,
    ): AdminUserDetailResponse

    // TODO когда будут entity(model), добавить сервисные методы
    fun userExists(userId: Long): Boolean
}
