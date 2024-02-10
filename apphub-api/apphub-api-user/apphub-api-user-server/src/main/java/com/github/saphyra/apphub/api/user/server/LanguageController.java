package com.github.saphyra.apphub.api.user.server;

import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.Constants;
import com.github.saphyra.apphub.lib.common_domain.OneParamRequest;
import com.github.saphyra.apphub.lib.config.common.Endpoints;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.UUID;

public interface LanguageController {
    /**
     * Changing the preferred language of the given user
     */
    @RequestMapping(method = RequestMethod.POST, value = Endpoints.ACCOUNT_CHANGE_LANGUAGE)
    void changeLanguage(@RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader, @RequestBody OneParamRequest<String> language);

    /**
     * Fecthing the preferred language of the given user.
     * Called by main-gateway's LocaleFilter
     */
    @RequestMapping(method = RequestMethod.GET, value = Endpoints.USER_DATA_INTERNAL_GET_USER_LANGUAGE)
    String getLanguage(@PathVariable("userId") UUID userId);
}
