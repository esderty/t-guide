package t.lab.guide.dto.auth.command

import t.lab.guide.dto.auth.AuthRequest
import t.lab.guide.dto.auth.ChangePasswordRequest
import t.lab.guide.dto.auth.LogoutRequest
import t.lab.guide.dto.auth.RefreshRequest
import t.lab.guide.dto.auth.RegistrationRequest
import t.lab.guide.enums.UserLanguage
import t.lab.guide.exception.BadRequestException

fun AuthRequest.toCommand(): AuthCommand =
    AuthCommand(
        username = this.username ?: throw BadRequestException("Поле 'username' обязательно"),
        password = this.password ?: throw BadRequestException("Поле 'password' обязательно"),
    )

fun ChangePasswordRequest.toCommand(): ChangePasswordCommand =
    ChangePasswordCommand(
        oldPassword = this.oldPassword ?: throw BadRequestException("Поле 'oldPassword' обязательно"),
        newPassword = this.newPassword ?: throw BadRequestException("Поле 'newPassword' обязательно"),
    )

fun LogoutRequest.toCommand(): LogoutCommand =
    LogoutCommand(
        refreshToken = this.refreshToken ?: throw BadRequestException("Поле 'refreshToken' обязательно"),
    )

fun RefreshRequest.toCommand(): RefreshCommand =
    RefreshCommand(
        refreshToken = this.refreshToken ?: throw BadRequestException("Поле 'refreshToken' обязательно"),
    )

fun RegistrationRequest.toCommand(): RegistrationCommand =
    RegistrationCommand(
        username = this.username ?: throw BadRequestException("Поле 'username' обязательно"),
        email = this.email ?: throw BadRequestException("Поле 'email' обязательно"),
        name = this.name ?: throw BadRequestException("Поле 'name' обязательно"),
        password = this.password ?: throw BadRequestException("Поле 'password' обязательно"),
        lang = UserLanguage.valueOf(this.lang ?: throw BadRequestException("Поле 'lang' обязательно")),
    )
