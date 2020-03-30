package com.github.saphyra.apphub.service.user.authentication.service;

import com.github.saphyra.apphub.api.user.data.client.UserDataApiClient;
import com.github.saphyra.apphub.api.user.data.model.response.InternalUserResponse;
import com.github.saphyra.apphub.service.user.authentication.dao.AccessToken;
import com.github.saphyra.apphub.service.user.authentication.dao.AccessTokenDao;
import com.github.saphyra.encryption.impl.PasswordService;
import com.github.saphyra.apphub.api.user.authentication.model.request.LoginRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static org.apache.commons.lang.BooleanUtils.isTrue;

@Component
@Slf4j
@RequiredArgsConstructor
//TODO unit test
//TODO proper exception
public class LoginService {
    private final AccessTokenDao accessTokenDao;
    private final AccessTokenFactory accessTokenFactory;
    private final UserDataApiClient internalUserDataApi;
    private final PasswordService passwordService;

    public AccessToken login(LoginRequest loginRequest) {
        InternalUserResponse user = internalUserDataApi.findByEmail(loginRequest.getEmail());
        if (!passwordService.authenticate(loginRequest.getPassword(), user.getPasswordHash())) {
            throw new RuntimeException("Invalid password");
        }

        AccessToken accessToken = accessTokenFactory.create(user.getUserId(), isTrue(loginRequest.getRememberMe()));
        accessTokenDao.save(accessToken);
        return accessToken;
    }
}
