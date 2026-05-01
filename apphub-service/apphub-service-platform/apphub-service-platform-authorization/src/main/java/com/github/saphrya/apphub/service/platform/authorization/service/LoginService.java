package com.github.saphrya.apphub.service.platform.authorization.service;

import com.github.saphrya.apphub.service.platform.authorization.dao.refresh_token.RefreshToken;
import com.github.saphrya.apphub.service.platform.authorization.dao.refresh_token.RefreshTokenDao;
import com.github.saphrya.apphub.service.platform.authorization.etc.AccessTokenDto;
import com.github.saphrya.apphub.service.platform.authorization.etc.AuthorizationClientProxy;
import com.github.saphyra.apphub.api.etc.user.model.authorization.AuthorizationRequest;
import com.github.saphyra.apphub.api.etc.user.model.authorization.AuthorizationResponse;
import com.github.saphyra.apphub.api.platform.authorization.model.LoginRequest;
import com.github.saphyra.apphub.api.platform.authorization.model.TokenResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

        RefreshToken refreshToken = tokenService.createRefreshToken(response.getUserId(), loginRequest.getRememberMe());
        refreshTokenDao.save(refreshToken);

        AccessTokenDto accessToken = tokenService.createAccessToken(response.getUserId(), response.getRoles());

        return tokenResponseMapper.create(refreshToken, accessToken);
    }
}
