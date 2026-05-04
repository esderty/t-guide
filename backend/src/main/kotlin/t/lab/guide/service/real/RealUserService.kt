package t.lab.guide.service.real

import org.springframework.context.annotation.Profile
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import t.lab.guide.domain.User
import t.lab.guide.dto.admin.user.AdminPatchUserRequest
import t.lab.guide.dto.admin.user.AdminUserDetailResponse
import t.lab.guide.dto.admin.user.AdminUserPageResponse
import t.lab.guide.dto.admin.user.command.AdminUserPageQueryCommand
import t.lab.guide.dto.user.PatchUserRequest
import t.lab.guide.dto.user.UserResponse
import t.lab.guide.enums.SortDirection
import t.lab.guide.enums.UserLanguage
import t.lab.guide.exception.ConflictException
import t.lab.guide.exception.InternalServerException
import t.lab.guide.exception.NotFoundException
import t.lab.guide.mapper.toAdminUserDetailResponse
import t.lab.guide.mapper.toAdminUserShortItem
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
                ?: throw InternalServerException("Текущий пользователь не найден")
        return user.toUserResponse()
    }

    override fun patchUser(request: PatchUserRequest): UserResponse {
        val userId = securityService.getCurrentUserId()
        val currentUser = getUserById(userId)

        request.email?.takeIf { it != currentUser.email }?.let {
            if (userRepository.existsByEmail(it)) {
                throw InternalServerException("Пользователь с email '$it' уже существует")
            }
        }
        request.username?.takeIf { it != currentUser.username }?.let {
            if (userRepository.existsByUsername(it)) {
                throw InternalServerException("Пользователь с email '$it' уже существует")
            }
        }

        val updatedUser =
            currentUser.copy(
                email = request.email ?: currentUser.email,
                username = request.username ?: currentUser.username,
                name = request.name ?: currentUser.name,
                lang = request.lang?.let(UserLanguage::valueOf) ?: currentUser.lang,
            )

        return userRepository.save(updatedUser).toUserResponse()
    }

    override fun getUsersPage(query: AdminUserPageQueryCommand): AdminUserPageResponse {
        val safeSearch = query.search?.trim()?.takeIf { it.isNotEmpty() }

        val direction =
            if (query.sortDirection == SortDirection.ASC) Sort.Direction.ASC else Sort.Direction.DESC
        val pageable = PageRequest.of(query.page, query.size, Sort.by(direction, query.sortBy.value))

        val users = userRepository.findUsersPage(safeSearch, pageable)
        val total = userRepository.countUsersInPage(safeSearch)
        val totalPages = ((total + query.size - 1) / query.size).toInt()

        return AdminUserPageResponse(
            users = users.map { it.toAdminUserShortItem() },
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
                throw ConflictException("Пользователь с email '$it' уже существует")
            }
        }
        request.username?.takeIf { it != currentUser.username }?.let {
            if (userRepository.existsByUsername(it)) {
                throw ConflictException("Пользователь с username '$it' уже существует")
            }
        }

        val updatedUser =
            currentUser.copy(
                email = request.email ?: currentUser.email,
                username = request.username ?: currentUser.username,
                lang = request.lang?.let(UserLanguage::valueOf) ?: currentUser.lang,
                role = request.role ?: currentUser.role,
                isActive = request.isActive ?: currentUser.isActive,
            )

        return userRepository.save(updatedUser).toAdminUserDetailResponse()
    }

    fun userExists(userId: Long): Boolean = userRepository.existsById(userId)

    fun getUserById(userId: Long): User =
        userRepository
            .findById(userId)
            .orElseThrow { NotFoundException("Пользователь с id=$userId не найден") }
}
