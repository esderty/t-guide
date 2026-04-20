package t.lab.guide.service;

import t.lab.guide.dto.auth.*;

public interface AuthService {
    RegistrationResponse registrationUser (RegistrationRequest request);
    AuthResponse authenticationUser (AuthRequest request);
    void logoutUser (LogoutRequest request);
    TokenPairResponse changePassword (ChangePasswordRequest request);
    TokenPairResponse refreshToken (RefreshRequest request);

    //TODO когда будут entity(model), добавить сервисные методы
}
