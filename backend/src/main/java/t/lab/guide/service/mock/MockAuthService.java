package t.lab.guide.service.mock;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import t.lab.guide.dto.auth.*;
import t.lab.guide.dto.user.UserResponse;
import t.lab.guide.entity.enums.UserLanguage;
import t.lab.guide.entity.enums.UserRole;
import t.lab.guide.service.AuthService;
import t.lab.guide.service.SecurityService;

@Service
@Profile("demo")
@RequiredArgsConstructor
public class MockAuthService implements AuthService {

    private final SecurityService securityService;

    @Override
    public RegistrationResponse registrationUser (RegistrationRequest request) {
        UserResponse userResponse = UserResponse.builder().id(1L)
                .username(request.username())
                .email(request.email())
                .name(request.name())
                .lang(request.language())
                .role(UserRole.USER)
                .build();
        TokenPairResponse tokenPairResponse = TokenPairResponse.builder()
                .accessToken("mocked_access_token")
                .refreshToken("mocked_refresh_token")
                .build();

        return RegistrationResponse.builder().
                tokens(tokenPairResponse)
                .user(userResponse)
                .build();

    }

    @Override
    public AuthResponse authenticationUser(AuthRequest request){
        UserResponse userResponse = UserResponse.builder().id(1L)
                .username("enzolu")
                .email("email@domain.zone")
                .name("Игорь")
                .lang(UserLanguage.RU)
                .role(UserRole.USER)
                .build();
        TokenPairResponse tokenPairResponse = TokenPairResponse.builder()
                .accessToken("mocked_access_token")
                .refreshToken("mocked_refresh_token")
                .build();
        return AuthResponse.builder()
                .tokens(tokenPairResponse)
                .user(userResponse)
                .build();
    }

    @Override
    public void logoutUser(LogoutRequest request) {
        //Ревокаем рефреш токен пользователя
    }

    @Override
    public TokenPairResponse changePassword(ChangePasswordRequest request){
        Long userId = securityService.getCurrentUserId();
        //Проверяем старый пароль, если он не совпадает - кидаем исключение
        //После успешной смены пароля ревокаем все токены пользователя и выдаем новые
        return TokenPairResponse.builder()
                .accessToken("mocked_access_token")
                .refreshToken("mocked_refresh_token")
                .build();
    }

    @Override
    public TokenPairResponse refreshToken (RefreshRequest request) {
        //Проверяем валидность refresh токен, если он не валиден - кидаем исключение
        //Если токен валиден - ревокаем старый refresh, выдаем новый access токен и новый refresh токен
        return TokenPairResponse.builder()
                .accessToken("mocked_access_token")
                .refreshToken("mocked_refresh_token")
                .build();
    }


}
