package com.github.saphyra.apphub.api.user.server;

import com.github.saphyra.apphub.api.user.model.request.RegistrationRequest;
import com.github.saphyra.apphub.lib.common_util.Constants;
import com.github.saphyra.apphub.lib.config.Endpoints;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.UUID;

public interface UserDataController {
    @RequestMapping(method = RequestMethod.GET, value = Endpoints.INTERNAL_GET_USER_LANGUAGE)
    String getLanguage(@PathVariable("userId") UUID userId);

    @RequestMapping(method = RequestMethod.POST, value = Endpoints.REGISTER)
    void register(@RequestBody RegistrationRequest registrationRequest, @RequestHeader(Constants.LOCALE_HEADER) String locale);
}
