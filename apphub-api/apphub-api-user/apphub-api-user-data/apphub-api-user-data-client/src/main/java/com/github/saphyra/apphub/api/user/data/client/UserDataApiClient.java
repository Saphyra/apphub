package com.github.saphyra.apphub.api.user.data.client;

import com.github.saphyra.apphub.api.user.data.model.request.RegistrationRequest;
import com.github.saphyra.apphub.api.user.data.model.response.InternalUserResponse;
import com.github.saphyra.apphub.lib.common_util.Constants;
import com.github.saphyra.apphub.lib.config.Endpoint;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.UUID;

@FeignClient(name = "user-data")
public interface UserDataApiClient {
    @RequestMapping(method = RequestMethod.GET, value = Endpoint.INTERNAL_FIND_USER_BY_EMAIL)
    InternalUserResponse findByEmail(@PathVariable("email") String email);

    @RequestMapping(method = RequestMethod.GET, value = Endpoint.INTERNAL_GET_USER_LANGUAGE)
    String getLanguage(@PathVariable("userId") UUID userId, @RequestHeader(Constants.LOCALE_HEADER) String locale);

    @RequestMapping(method = RequestMethod.POST, value = Endpoint.REGISTER)
    void register(@RequestBody RegistrationRequest registrationRequest);
}
