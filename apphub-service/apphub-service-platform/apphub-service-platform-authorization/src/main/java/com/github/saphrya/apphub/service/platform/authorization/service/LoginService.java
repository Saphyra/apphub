package com.github.saphrya.apphub.service.platform.authorization.service;

import com.github.saphrya.apphub.service.platform.authorization.dao.refresh_token.RefreshToken;
import com.github.saphrya.apphub.service.platform.authorization.dao.refresh_token.RefreshTokenDao;
import com.github.saphrya.apphub.service.platform.authorization.etc.AccessTokenDto;
import com.github.saphrya.apphub.service.platform.authorization.etc.AuthorizationClientProxy;
import com.github.saphyra.apphub.api.etc.user.model.authorization.AuthorizationRequest;
import com.github.saphyra.apphub.api.etc.user.model.authorization.AuthorizationResponse;
import com.github.saphyra.apphub.api.platform.authorization.model.LoginRequest;
import com.github.saphyra.apphub.api.platform.authorization.model.TokenResponse;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class LoginService {
    private final TokenService tokenService;
    private final LoginRequestValidator loginRequestValidator;
    private final RefreshTokenDao refreshTokenDao;
    private final AuthorizationClientProxy authorizationClientProxy;
    private final TokenResponseMapper tokenResponseMapper;

    public TokenResponse login(LoginRequest loginRequest) {
        loginRequestValidator.validate(loginRequest);

        AuthorizationRequest request = AuthorizationRequest.builder()
            .userIdentifier(loginRequest.getUserIdentifier().toLowerCase())
            .password(loginRequest.getPassword())
            .build();

        AuthorizationResponse response = authorizationClientProxy.authorize(request);

        return switch (response.getAuthorizationResult()) {
            case AUTHORIZED -> {
                RefreshToken refreshToken = tokenService.createRefreshToken(response.getUserId(), loginRequest.getRememberMe());
                refreshTokenDao.save(refreshToken);

                AccessTokenDto accessToken = tokenService.createAccessToken(response.getUserId(), refreshToken.getRefreshTokenId(), response.getRoles());

                yield tokenResponseMapper.create(refreshToken, accessToken);
            }
            case USER_NOT_FOUND -> throw ExceptionFactory.notLoggedException(HttpStatus.UNAUTHORIZED, ErrorCode.BAD_CREDENTIALS, "User not found by " + loginRequest.getUserIdentifier());
            case USER_LOCKED -> throw ExceptionFactory.notLoggedException(HttpStatus.UNAUTHORIZED, ErrorCode.ACCOUNT_LOCKED, response.getUserId() + " is locked.");
            case INCORRECT_PASSWORD -> throw ExceptionFactory.notLoggedException(HttpStatus.UNAUTHORIZED, ErrorCode.BAD_CREDENTIALS, "Incorrect password for user " + response.getUserId());
        };
    }
}
