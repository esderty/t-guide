package t.lab.guide.service

import t.lab.guide.dto.admin.user.AdminPatchUserRequest
import t.lab.guide.dto.admin.user.AdminUserDetailResponse
import t.lab.guide.dto.admin.user.AdminUserPageResponse
import t.lab.guide.dto.admin.user.command.AdminUserPageQueryCommand
import t.lab.guide.dto.user.PatchUserRequest
import t.lab.guide.dto.user.UserResponse

interface UserService {
    fun getCurrentUser(): UserResponse

    fun patchUser(request: PatchUserRequest): UserResponse

    fun getUsersPage(query: AdminUserPageQueryCommand): AdminUserPageResponse

    fun getUserDetail(userId: Long): AdminUserDetailResponse

    fun patchUserByAdmin(
        userId: Long,
        request: AdminPatchUserRequest,
    ): AdminUserDetailResponse
}
