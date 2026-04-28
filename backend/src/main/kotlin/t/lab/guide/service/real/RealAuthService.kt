package t.lab.guide.service.real

import org.springframework.context.annotation.Profile
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import t.lab.guide.dto.auth.AuthRequest
import t.lab.guide.dto.auth.AuthResponse
import t.lab.guide.dto.auth.ChangePasswordRequest
import t.lab.guide.dto.auth.LogoutRequest
import t.lab.guide.dto.auth.RefreshRequest
import t.lab.guide.dto.auth.RegistrationRequest
import t.lab.guide.dto.auth.RegistrationResponse
import t.lab.guide.dto.auth.TokenPairResponse
import t.lab.guide.exception.ConflictException
import t.lab.guide.exception.InternalServerException
import t.lab.guide.mapper.toAppUserDetails
import t.lab.guide.mapper.toAuthResponse
import t.lab.guide.mapper.toRegistrationResponse
import t.lab.guide.mapper.toUser
import t.lab.guide.repository.PasswordRepository
import t.lab.guide.repository.UserRepository
import t.lab.guide.security.AppUserDetails
import t.lab.guide.security.JwtTokenService
import t.lab.guide.service.AuthService
import t.lab.guide.service.SecurityService

@Service
@Profile("!demo")
class RealAuthService(
    private val userRepository: UserRepository,
    private val passwordRepository: PasswordRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtTokenService: JwtTokenService,
    private val authenticationManager: AuthenticationManager,
    private val securityService: SecurityService,
) : AuthService {
    @Transactional
    override fun registerUser(request: RegistrationRequest): RegistrationResponse {
        if (userRepository.existsByEmail(request.email)) {
            throw ConflictException("This email or username is already taken")
        }
        if (userRepository.existsByUsername(request.username)) {
            throw ConflictException("This email or username is already taken")
        }

        val newUser = request.toUser()

        val savedNewUser = userRepository.save(newUser)

        val encodedPassword = encodePassword(request.password)

        passwordRepository.insertPassword(
            userId = savedNewUser.id!!,
            passwordHash = encodedPassword,
        )

        val userDetails = savedNewUser.toAppUserDetails(encodedPassword)

        val tokenPair = jwtTokenService.issueTokens(userDetails)

        return savedNewUser.toRegistrationResponse(tokenPair)
    }

    override fun authenticateUser(request: AuthRequest): AuthResponse {
        val authRequest = UsernamePasswordAuthenticationToken(request.username, request.password)
        val auth = authenticationManager.authenticate(authRequest)
        val userDetails = auth.principal as AppUserDetails

        val user =
            userRepository
                .findUserProfileById(userDetails.id)
                ?: throw InternalServerException(
                    "User not found with id ${userDetails.id} after authentication. Try again or contact support",
                )

        val tokens = jwtTokenService.issueTokens(userDetails)
        return user.toAuthResponse(tokens)
    }

    override fun logoutUser(request: LogoutRequest) {
        jwtTokenService.revoke(request.refreshToken)
    }

    @Transactional
    override fun changePassword(request: ChangePasswordRequest): TokenPairResponse {
        val userId = securityService.getCurrentUserId()
        val userPassword =
            passwordRepository
                .findById(userId)
                .orElseThrow { InternalServerException("Password record not found for user. Try again or contact support") }

        if (!passwordEncoder.matches(request.oldPassword, userPassword.passwordHash)) {
            throw BadCredentialsException("Current password is incorrect")
        }
        val newEncodedPassword = encodePassword(request.newPassword)
        if (newEncodedPassword == userPassword.passwordHash) {
            throw BadCredentialsException("New password must be different from the old password")
        }
        val newUserPassword = userPassword.copy(passwordHash = newEncodedPassword)
        passwordRepository.save(newUserPassword)

        val userDetails =
            userRepository
                .findLoginViewByUserId(userId)
                ?.toAppUserDetails()
                ?: throw InternalServerException(
                    "User not found with id $userId after password change. Try again or contact support",
                )

        jwtTokenService.revokeAllUserTokens(userId)
        val tokenPair = jwtTokenService.issueTokens(userDetails)

        return TokenPairResponse(accessToken = tokenPair.accessToken, refreshToken = tokenPair.refreshToken)
    }

    override fun refreshToken(request: RefreshRequest): TokenPairResponse {
        val tokenPair = jwtTokenService.rotate(request.refreshToken)
        return TokenPairResponse(accessToken = tokenPair.accessToken, refreshToken = tokenPair.refreshToken)
    }

    private fun encodePassword(password: String): String =
        passwordEncoder.encode(password)
            ?: throw InternalServerException("Failed to encode new password. Try again or contact support")
}
