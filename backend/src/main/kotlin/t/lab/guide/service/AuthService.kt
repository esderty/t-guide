package t.lab.guide.service

import t.lab.guide.dto.auth.AuthRequest
import t.lab.guide.dto.auth.AuthResponse
import t.lab.guide.dto.auth.ChangePasswordRequest
import t.lab.guide.dto.auth.LogoutRequest
import t.lab.guide.dto.auth.RefreshRequest
import t.lab.guide.dto.auth.RegistrationRequest
import t.lab.guide.dto.auth.RegistrationResponse
import t.lab.guide.dto.auth.TokenPairResponse

interface AuthService {
    fun registerUser(request: RegistrationRequest): RegistrationResponse

    fun authenticateUser(request: AuthRequest): AuthResponse

    fun logoutUser(request: LogoutRequest)

    fun changePassword(request: ChangePasswordRequest): TokenPairResponse

    fun refreshToken(request: RefreshRequest): TokenPairResponse
// TODO когда будут entity(model), добавить сервисные методы
}
