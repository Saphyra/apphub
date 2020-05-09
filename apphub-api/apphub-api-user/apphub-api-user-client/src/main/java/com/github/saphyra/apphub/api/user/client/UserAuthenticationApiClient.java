package com.github.saphyra.apphub.api.user.client;

import com.github.saphyra.apphub.api.user.model.request.LoginRequest;
import com.github.saphyra.apphub.api.user.model.response.InternalAccessTokenResponse;
import com.github.saphyra.apphub.api.user.model.response.LoginResponse;
import com.github.saphyra.apphub.lib.config.Endpoints;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.UUID;

@FeignClient("user-authentication")
public interface UserAuthenticationApiClient {
    @RequestMapping(method = RequestMethod.POST, value = Endpoints.LOGIN)
    LoginResponse login(@RequestBody LoginRequest loginRequest);

    @RequestMapping(method = RequestMethod.GET, value = Endpoints.INTERNAL_GET_ACCESS_TOKEN_BY_ID)
    InternalAccessTokenResponse getAccessTokenById(@PathVariable("accessTokenId") UUID accessTokenId);
}
