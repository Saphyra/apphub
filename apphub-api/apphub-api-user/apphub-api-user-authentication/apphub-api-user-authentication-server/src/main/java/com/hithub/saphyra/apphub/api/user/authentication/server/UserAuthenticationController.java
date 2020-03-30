package com.hithub.saphyra.apphub.api.user.authentication.server;

import com.github.saphyra.apphub.lib.endpoint.Endpoint;
import com.hithub.saphyra.apphub.api.user.authentication.model.request.LoginRequest;
import com.hithub.saphyra.apphub.api.user.authentication.model.response.LoginResponse;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletResponse;

public interface UserAuthenticationController {
    @RequestMapping(method = RequestMethod.POST, value = Endpoint.LOGIN)
    LoginResponse login(@RequestBody LoginRequest loginRequest, HttpServletResponse response);
}
