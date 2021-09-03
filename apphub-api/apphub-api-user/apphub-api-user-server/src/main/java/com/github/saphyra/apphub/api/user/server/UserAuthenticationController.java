package com.github.saphyra.apphub.api.user.server;

import com.github.saphyra.apphub.api.platform.event_gateway.model.request.SendEventRequest;
import com.github.saphyra.apphub.api.user.model.request.LoginRequest;
import com.github.saphyra.apphub.api.user.model.response.InternalAccessTokenResponse;
import com.github.saphyra.apphub.api.user.model.response.LastVisitedPageResponse;
import com.github.saphyra.apphub.api.user.model.response.LoginResponse;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.Constants;
import com.github.saphyra.apphub.lib.config.Endpoints;
import com.github.saphyra.apphub.lib.event.RefreshAccessTokenExpirationEvent;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.UUID;

public interface UserAuthenticationController {
    @RequestMapping(method = RequestMethod.GET, value = Endpoints.CHECK_SESSION)
    void checkSession(@RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessToken);

    @PostMapping(Endpoints.EXTEND_SESSION)
    void extendSession(@RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessToken);

    @RequestMapping(method = RequestMethod.POST, value = Endpoints.EVENT_DELETE_EXPIRED_ACCESS_TOKENS)
    void deleteExpiredAccessTokens();

    @RequestMapping(method = RequestMethod.POST, value = Endpoints.LOGIN)
    LoginResponse login(@RequestBody LoginRequest loginRequest);

    @RequestMapping(method = RequestMethod.POST, value = Endpoints.LOGOUT)
    void logout(@RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessToken);

    @RequestMapping(method = RequestMethod.GET, value = Endpoints.USER_DATA_INTERNAL_GET_ACCESS_TOKEN_BY_ID)
    ResponseEntity<InternalAccessTokenResponse> getAccessTokenById(@PathVariable("accessTokenId") UUID accessTokenId);

    @RequestMapping(method = RequestMethod.POST, value = Endpoints.EVENT_REFRESH_ACCESS_TOKEN_EXPIRATION)
    void refreshAccessTokenExpiration(@RequestBody SendEventRequest<RefreshAccessTokenExpirationEvent> request);

    @GetMapping(Endpoints.USER_DATA_INTERNAL_GET_LAST_VISITED_PAGE)
    LastVisitedPageResponse getLastVisitedPage(@PathVariable("userId") UUID userId);
}
