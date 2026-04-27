package t.lab.guide.service.real

import org.springframework.context.annotation.Profile
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import t.lab.guide.domain.User
import t.lab.guide.dto.admin.user.AdminPatchUserRequest
import t.lab.guide.dto.admin.user.AdminUserDetailResponse
import t.lab.guide.dto.admin.user.AdminUserPageQuery
import t.lab.guide.dto.admin.user.AdminUserPageResponse
import t.lab.guide.dto.user.PatchUserRequest
import t.lab.guide.dto.user.UserResponse
import t.lab.guide.enums.AdminUserSortField
import t.lab.guide.enums.SortDirection
import t.lab.guide.exception.InternalServerException
import t.lab.guide.exception.NotFoundException
import t.lab.guide.mapper.toAdminUserDetailResponse
import t.lab.guide.mapper.toUserResponse
import t.lab.guide.repository.UserRepository
import t.lab.guide.service.SecurityService
import t.lab.guide.service.UserService

@Service
@Profile("!demo")
class RealUserService(
    private val userRepository: UserRepository,
    private val securityService: SecurityService,
) : UserService {
    override fun getCurrentUser(): UserResponse {
        val userId = securityService.getCurrentUserId()
        val user =
            userRepository.findUserProfileById(userId)
                ?: throw InternalServerException("Current user does not exist")
        return user.toUserResponse()
    }

    override fun patchUser(request: PatchUserRequest): UserResponse {
        val userId = securityService.getCurrentUserId()
        val currentUser = getUserById(userId)

        request.email?.takeIf { it != currentUser.email }?.let {
            if (userRepository.existsByEmail(it)) {
                throw InternalServerException("User with email '$it' already exists")
            }
        }
        request.username?.takeIf { it != currentUser.username }?.let {
            if (userRepository.existsByUsername(it)) {
                throw InternalServerException("User with email '$it' already exists")
            }
        }

        val updatedUser =
            currentUser.copy(
                email = request.email ?: currentUser.email,
                username = request.username ?: currentUser.username,
                name = request.name ?: currentUser.name,
                lang = request.language ?: currentUser.lang,
            )

        return userRepository.save(updatedUser).toUserResponse()
    }

    override fun getUsersPage(query: AdminUserPageQuery): AdminUserPageResponse {
        val safeSearch = query.search?.trim()?.takeIf { it.isNotEmpty() }

        val sortColumn = (query.sortBy ?: AdminUserSortField.CREATED_AT).value
        val direction =
            if (query.sortDirection == SortDirection.ASC) Sort.Direction.ASC else Sort.Direction.DESC
        val pageable = PageRequest.of(query.page, query.size, Sort.by(direction, sortColumn))

        val users = userRepository.findUsersPage(safeSearch, pageable)
        val total = userRepository.countUsers(safeSearch)
        val totalPages = if (query.size == 0) 0 else ((total + query.size - 1) / query.size).toInt()

        return AdminUserPageResponse(
            users = users,
            page = query.page,
            size = query.size,
            totalElements = total,
            totalPages = totalPages,
        )
    }

    override fun getUserDetail(userId: Long): AdminUserDetailResponse {
        val user = getUserById(userId)

        return user.toAdminUserDetailResponse()
    }

    override fun patchUserByAdmin(
        userId: Long,
        request: AdminPatchUserRequest,
    ): AdminUserDetailResponse {
        val currentUser = getUserById(userId)

        request.email?.takeIf { it != currentUser.email }?.let {
            if (userRepository.existsByEmail(it)) {
                throw InternalServerException("User with email '$it' already exists")
            }
        }
        request.username?.takeIf { it != currentUser.username }?.let {
            if (userRepository.existsByUsername(it)) {
                throw InternalServerException("User with email '$it' already exists")
            }
        }

        val updatedUser =
            currentUser.copy(
                email = request.email ?: currentUser.email,
                username = request.username ?: currentUser.username,
                lang = request.lang ?: currentUser.lang,
                role = request.role ?: currentUser.role,
                isActive = request.isActive ?: currentUser.isActive,
            )

        return userRepository.save(updatedUser).toAdminUserDetailResponse()
    }

    fun userExists(userId: Long): Boolean = userRepository.existsById(userId)

    fun getUserById(userId: Long): User =
        userRepository
            .findById(userId)
            .orElseThrow { NotFoundException("User $userId not found") }
}
