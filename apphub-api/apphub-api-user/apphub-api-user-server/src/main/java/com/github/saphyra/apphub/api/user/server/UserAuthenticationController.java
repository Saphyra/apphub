package com.github.saphyra.apphub.api.user.server;

import com.github.saphyra.apphub.api.platform.event_gateway.model.request.SendEventRequest;
import com.github.saphyra.apphub.api.user.model.request.LoginRequest;
import com.github.saphyra.apphub.api.user.model.response.InternalAccessTokenResponse;
import com.github.saphyra.apphub.api.user.model.response.LoginResponse;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.Constants;
import com.github.saphyra.apphub.lib.config.common.Endpoints;
import com.github.saphyra.apphub.lib.event.RefreshAccessTokenExpirationEvent;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.UUID;

public interface UserAuthenticationController {
    /**
     * Checking if the given access token is valid.
     * The endpoint itself does nothing, the logic is handled by the general access token check in the main-gateway
     */
    @GetMapping(Endpoints.CHECK_SESSION)
    void checkSession(@RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessToken);

    /**
     * Extending the current access token validity.
     * The endpoint itself does nothing, logic is handled by the general session-extending logic in the main-gateway
     */
    @PostMapping(Endpoints.EXTEND_SESSION)
    void extendSession(@RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessToken);

    /**
     * Removing the expired access tokens from the database to prevent flooding.
     * Triggered by scheduler-service
     */
    @PostMapping(Endpoints.EVENT_DELETE_EXPIRED_ACCESS_TOKENS)
    void deleteExpiredAccessTokens();

    /**
     * Checking the credentials of the user, and creating an access token with a certain validity.
     *
     * @return the generated access token, what the client can store at their side
     */
    @PostMapping(Endpoints.LOGIN)
    LoginResponse login(@RequestBody LoginRequest loginRequest);

    /**
     * Invalidating the access token by removing it from the database
     */
    @PostMapping(Endpoints.LOGOUT)
    void logout(@RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessToken);

    /**
     * Fetching details about the access token with the given id, including all the roles the owner user has access to.
     * Called by main-gateway to authorize the user, and create the AccessTokenHeader based on it.
     */
    @GetMapping(Endpoints.USER_DATA_INTERNAL_GET_ACCESS_TOKEN_BY_ID)
    ResponseEntity<InternalAccessTokenResponse> getAccessTokenById(@PathVariable("accessTokenId") UUID accessTokenId);

    /**
     * Resetting the access token's expiration to let the user continue browsing.
     * Triggered by main-gateway after the authorization was successful
     */
    @PostMapping(Endpoints.EVENT_REFRESH_ACCESS_TOKEN_EXPIRATION)
    void refreshAccessTokenExpiration(@RequestBody SendEventRequest<RefreshAccessTokenExpirationEvent> request);
}
