package t.lab.guide.service.mock

import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service
import t.lab.guide.dto.auth.AuthResponse
import t.lab.guide.dto.auth.RegistrationResponse
import t.lab.guide.dto.auth.TokenPairResponse
import t.lab.guide.dto.auth.command.AuthCommand
import t.lab.guide.dto.auth.command.ChangePasswordCommand
import t.lab.guide.dto.auth.command.LogoutCommand
import t.lab.guide.dto.auth.command.RefreshCommand
import t.lab.guide.dto.auth.command.RegistrationCommand
import t.lab.guide.dto.user.UserResponse
import t.lab.guide.enums.UserLanguage
import t.lab.guide.enums.UserRole
import t.lab.guide.service.AuthService
import t.lab.guide.service.SecurityService

@Service
@Profile("demo")
class MockAuthService(
    private val securityService: SecurityService,
) : AuthService {
    override fun registerUser(request: RegistrationCommand): RegistrationResponse {
        val user =
            UserResponse(
                id = 1L,
                username = request.username,
                email = request.email,
                name = request.name,
                lang = request.lang,
                role = UserRole.USER,
            )
        return RegistrationResponse(tokens = mockTokens(), user = user)
    }

    override fun authenticateUser(request: AuthCommand): AuthResponse {
        val user =
            UserResponse(
                id = 1L,
                username = "enzolu",
                email = "email@domain.zone",
                name = "Игорь",
                lang = UserLanguage.RU,
                role = UserRole.USER,
            )
        return AuthResponse(tokens = mockTokens(), user = user)
    }

    override fun logoutUser(request: LogoutCommand) {
        // Ревокаем рефреш токен пользователя
    }

    override fun changePassword(request: ChangePasswordCommand): TokenPairResponse {
        securityService.getCurrentUserId()
        // Проверяем старый пароль, если он не совпадает - кидаем исключение
        // После успешной смены пароля ревокаем все токены пользователя и выдаем новые
        return mockTokens()
    }

    override fun refreshToken(request: RefreshCommand): TokenPairResponse {
        // Проверяем валидность refresh токена, если он не валиден - кидаем исключение
        // Если токен валиден - ревокаем старый refresh, выдаем новый access и новый refresh
        return mockTokens()
    }

    private fun mockTokens(): TokenPairResponse =
        TokenPairResponse(
            accessToken = "mocked_access_token",
            refreshToken = "mocked_refresh_token",
        )
}
