package com.github.saphyra.apphub.api.user.authentication.client;

import com.github.saphyra.apphub.lib.endpoint.Endpoint;
import com.github.saphyra.apphub.api.user.authentication.model.request.LoginRequest;
import com.github.saphyra.apphub.api.user.authentication.model.response.InternalAccessTokenResponse;
import com.github.saphyra.apphub.api.user.authentication.model.response.LoginResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.UUID;

@FeignClient("user-authentication")
public interface UserAuthenticationApiClient {
    @RequestMapping(method = RequestMethod.POST, value = Endpoint.LOGIN)
    LoginResponse login(@RequestBody LoginRequest loginRequest);

    @RequestMapping(method = RequestMethod.GET, value = Endpoint.INTERNAL_GET_ACCESS_TOKEN_BY_ID)
    InternalAccessTokenResponse getAccessTokenById(@PathVariable("accessTokenId") UUID accessTokenId);
}
