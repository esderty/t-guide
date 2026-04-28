package t.lab.guide.mapper

import t.lab.guide.domain.User
import t.lab.guide.dto.admin.user.AdminUserDetailResponse
import t.lab.guide.dto.user.UserResponse
import t.lab.guide.repository.view.ProfileUserView

fun User.toUserResponse(): UserResponse =
    UserResponse(
        id = id!!,
        username = username,
        name = name,
        email = email,
        lang = lang,
        role = role,
    )

fun ProfileUserView.toUserResponse(): UserResponse =
    UserResponse(
        id = id,
        username = username,
        name = name,
        email = email,
        lang = lang,
        role = role,
    )

fun User.toAdminUserDetailResponse(): AdminUserDetailResponse =
    AdminUserDetailResponse(
        id = id!!,
        username = username,
        email = email,
        name = name,
        lang = lang,
        role = role,
        isActive = isActive,
        createdAt = createdAt!!,
        updatedAt = updatedAt!!,
    )
