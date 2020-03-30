package com.hithub.saphyra.apphub.api.user.authentication.client;

import com.github.saphyra.apphub.lib.endpoint.Endpoint;
import com.hithub.saphyra.apphub.api.user.authentication.model.request.LoginRequest;
import com.hithub.saphyra.apphub.api.user.authentication.model.response.LoginResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient("user-authentication")
public interface UserAuthenticationApiClient {
    @RequestMapping(method = RequestMethod.POST, value = Endpoint.LOGIN)
    LoginResponse login(@RequestBody LoginRequest loginRequest);
}
