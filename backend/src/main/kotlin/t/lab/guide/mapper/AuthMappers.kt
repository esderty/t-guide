package t.lab.guide.mapper

import org.springframework.security.core.authority.SimpleGrantedAuthority
import t.lab.guide.domain.User
import t.lab.guide.dto.auth.AuthResponse
import t.lab.guide.dto.auth.RegistrationResponse
import t.lab.guide.dto.auth.command.RegistrationCommand
import t.lab.guide.enums.UserRole
import t.lab.guide.repository.view.LoginUserView
import t.lab.guide.repository.view.ProfileUserView
import t.lab.guide.security.AppUserDetails
import t.lab.guide.security.TokenPair

fun LoginUserView.toAppUserDetails(): AppUserDetails =
    AppUserDetails(
        id = id,
        username = username,
        passwordHash = passwordHash,
        role = role,
        isActive = isActive,
        authorities = listOf(SimpleGrantedAuthority("ROLE_${role.name}")),
    )

fun User.toAppUserDetails(passwordHash: String): AppUserDetails =
    AppUserDetails(
        id = id!!,
        username = username,
        passwordHash = passwordHash,
        role = role,
        isActive = isActive,
        authorities = listOf(SimpleGrantedAuthority("ROLE_${role.name}")),
    )

fun RegistrationCommand.toUser(): User =
    User(
        username = username,
        name = name,
        email = email,
        lang = lang,
        role = UserRole.USER,
        isActive = true,
    )

fun User.toRegistrationResponse(tokens: TokenPair) =
    RegistrationResponse(
        tokens = tokens.toTokenPairResponse(),
        user = toUserResponse(),
    )

fun ProfileUserView.toAuthResponse(tokens: TokenPair) =
    AuthResponse(
        tokens = tokens.toTokenPairResponse(),
        user = toUserResponse(),
    )
