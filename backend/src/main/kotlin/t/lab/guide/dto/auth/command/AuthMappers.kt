package t.lab.guide.dto.auth.command

import t.lab.guide.dto.auth.AuthRequest
import t.lab.guide.dto.auth.ChangePasswordRequest
import t.lab.guide.dto.auth.LogoutRequest
import t.lab.guide.dto.auth.RefreshRequest
import t.lab.guide.dto.auth.RegistrationRequest
import t.lab.guide.enums.UserLanguage

fun AuthRequest.toCommand(): AuthCommand =
    AuthCommand(
        username = requireNotNull(this.username) { "username not validated" },
        password = requireNotNull(this.password) { "password not validated" },
    )

fun ChangePasswordRequest.toCommand(): ChangePasswordCommand =
    ChangePasswordCommand(
        oldPassword = requireNotNull(this.oldPassword) { "oldPassword not validated" },
        newPassword = requireNotNull(this.newPassword) { "newPassword not validated" },
    )

fun LogoutRequest.toCommand(): LogoutCommand =
    LogoutCommand(
        refreshToken = requireNotNull(this.refreshToken) { "refreshToken not validated" },
    )

fun RefreshRequest.toCommand(): RefreshCommand =
    RefreshCommand(
        refreshToken = requireNotNull(this.refreshToken) { "refreshToken not validated" },
    )

fun RegistrationRequest.toCommand(): RegistrationCommand =
    RegistrationCommand(
        username = requireNotNull(this.username) { "username not validated" },
        email = requireNotNull(this.email) { "email not validated" },
        name = requireNotNull(this.name) { "name not validated" },
        password = requireNotNull(this.password) { "password not validated" },
        lang = UserLanguage.valueOf(requireNotNull(this.lang) { "lang not validated" }),
    )
