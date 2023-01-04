package com.github.saphyra.apphub.api.user.client;

import com.github.saphyra.apphub.api.user.model.response.InternalAccessTokenResponse;
import com.github.saphyra.apphub.lib.common_domain.Constants;
import com.github.saphyra.apphub.lib.config.common.Endpoints;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.UUID;

@FeignClient("user-authentication")
public interface UserAuthenticationClient {
    @RequestMapping(method = RequestMethod.GET, value = Endpoints.USER_DATA_INTERNAL_GET_ACCESS_TOKEN_BY_ID)
    InternalAccessTokenResponse getAccessTokenById(@PathVariable("accessTokenId") UUID accessTokenId, @RequestHeader(Constants.LOCALE_HEADER) String locale);
}
