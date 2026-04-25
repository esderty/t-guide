package t.lab.guide.service

import t.lab.guide.dto.admin.user.AdminPatchUserRequest
import t.lab.guide.dto.admin.user.AdminUserDetailResponse
import t.lab.guide.dto.admin.user.AdminUserPageResponse
import t.lab.guide.dto.user.PatchUserRequest
import t.lab.guide.dto.user.UserResponse
import t.lab.guide.enums.AdminUserSortField
import t.lab.guide.enums.SortDirection

interface UserService {
    fun getCurrentUser(): UserResponse

    fun patchUser(request: PatchUserRequest): UserResponse

    fun getUsersPage(
        page: Int,
        size: Int,
        sortBy: AdminUserSortField?,
        sortDir: SortDirection?,
        search: String?,
    ): AdminUserPageResponse

    fun getUserDetail(userId: Long): AdminUserDetailResponse

    fun patchUserByAdmin(
        userId: Long,
        request: AdminPatchUserRequest,
    ): AdminUserDetailResponse
}
