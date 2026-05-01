package t.lab.guide.service

import t.lab.guide.dto.auth.AuthResponse
import t.lab.guide.dto.auth.RegistrationResponse
import t.lab.guide.dto.auth.TokenPairResponse
import t.lab.guide.dto.auth.command.AuthCommand
import t.lab.guide.dto.auth.command.ChangePasswordCommand
import t.lab.guide.dto.auth.command.LogoutCommand
import t.lab.guide.dto.auth.command.RefreshCommand
import t.lab.guide.dto.auth.command.RegistrationCommand

interface AuthService {
    fun registerUser(request: RegistrationCommand): RegistrationResponse

    fun authenticateUser(request: AuthCommand): AuthResponse

    fun logoutUser(request: LogoutCommand)

    fun changePassword(request: ChangePasswordCommand): TokenPairResponse

    fun refreshToken(request: RefreshCommand): TokenPairResponse
// TODO когда будут entity(model), добавить сервисные методы
}
