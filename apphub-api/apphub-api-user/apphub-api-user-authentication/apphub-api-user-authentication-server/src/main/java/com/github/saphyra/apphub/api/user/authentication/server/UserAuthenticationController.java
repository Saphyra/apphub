package com.github.saphyra.apphub.api.user.authentication.server;

import com.github.saphyra.apphub.api.platform.event_gateway.model.request.SendEventRequest;
import com.github.saphyra.apphub.api.user.authentication.model.request.LoginRequest;
import com.github.saphyra.apphub.api.user.authentication.model.response.InternalAccessTokenResponse;
import com.github.saphyra.apphub.api.user.authentication.model.response.LoginResponse;
import com.github.saphyra.apphub.lib.endpoint.Endpoint;
import com.github.saphyra.apphub.lib.event.DeleteExpiredAccessTokensEvent;
import com.github.saphyra.apphub.lib.event.RefreshAccessTokenExpirationEvent;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

public interface UserAuthenticationController {
    @RequestMapping(method = RequestMethod.POST, value = Endpoint.DELETE_EXPIRED_ACCESS_TOKENS_EVENT)
    void deleteExpiredAccessTokens(@RequestBody SendEventRequest<DeleteExpiredAccessTokensEvent> request);

    @RequestMapping(method = RequestMethod.POST, value = Endpoint.REFRESH_ACCESS_TOKEN_EXPIRATION_EVENT)
    void refreshAccessTokenExpiration(@RequestBody SendEventRequest<RefreshAccessTokenExpirationEvent> request);

    @RequestMapping(method = RequestMethod.POST, value = Endpoint.LOGIN)
    LoginResponse login(@RequestBody LoginRequest loginRequest, HttpServletResponse response);

    @RequestMapping(method = RequestMethod.GET, value = Endpoint.INTERNAL_GET_ACCESS_TOKEN_BY_ID)
    ResponseEntity<InternalAccessTokenResponse> getAccessTokenById(@PathVariable("accessTokenId") UUID accessTokenId);
}
