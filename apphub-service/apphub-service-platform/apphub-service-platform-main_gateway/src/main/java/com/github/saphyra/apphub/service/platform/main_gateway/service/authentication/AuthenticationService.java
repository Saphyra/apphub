package com.github.saphyra.apphub.service.platform.main_gateway.service.authentication;

import com.github.saphyra.apphub.api.platform.event_gateway.client.EventGatewayApiClient;
import com.github.saphyra.apphub.api.platform.event_gateway.model.request.SendEventRequest;
import com.github.saphyra.apphub.api.user.authentication.model.response.InternalAccessTokenResponse;
import com.github.saphyra.apphub.lib.common_util.Base64Encoder;
import com.github.saphyra.apphub.lib.event.RefreshAccessTokenExpirationEvent;
import com.github.saphyra.apphub.service.platform.main_gateway.util.ErrorResponseHandler;
import com.github.saphyra.util.CookieUtil;
import com.github.saphyra.util.ObjectMapperWrapper;
import com.netflix.zuul.context.RequestContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

import static com.github.saphyra.apphub.lib.common_util.Constants.ACCESS_TOKEN_COOKIE;
import static com.github.saphyra.apphub.lib.common_util.Constants.ACCESS_TOKEN_HEADER;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {
    private final AccessTokenIdConverter accessTokenIdConverter;
    private final AccessTokenQueryService accessTokenQueryService;
    private final Base64Encoder base64Encoder;
    private final CookieUtil cookieUtil;
    private final ErrorResponseHandler errorResponseHandler;
    private final EventGatewayApiClient eventGatewayApi;
    private final ObjectMapperWrapper objectMapperWrapper;

    public void authenticate(RequestContext requestContext) {
        Optional<String> accessTokenIdStringOptional = cookieUtil.getCookie(
            requestContext.getRequest(),
            ACCESS_TOKEN_COOKIE
        );

        if (!accessTokenIdStringOptional.isPresent()) {
            errorResponseHandler.handleUnauthorized(requestContext, ""); //TODO add proper response
            return;
        }

        String accessTokenIdString = accessTokenIdStringOptional.get();
        Optional<UUID> accessTokenIdOptional = accessTokenIdConverter.convertAccessTokenId(accessTokenIdString);
        if (!accessTokenIdOptional.isPresent()) {
            errorResponseHandler.handleBadRequest(requestContext, ""); //TODO add proper response
            return;
        }

        Optional<InternalAccessTokenResponse> accessTokenResponseOptional = accessTokenQueryService.getAccessToken(accessTokenIdOptional.get());
        if (!accessTokenResponseOptional.isPresent()) {
            errorResponseHandler.handleUnauthorized(requestContext, ""); //TODO add proper response
            return;
        }

        InternalAccessTokenResponse accessTokenResponse = accessTokenResponseOptional.get();
        String accessTokenString = objectMapperWrapper.writeValueAsString(accessTokenResponse);
        log.debug("Stringified accessToken: {}", accessTokenString);
        String encodedAccessToken = base64Encoder.encode(accessTokenString);
        log.debug("Enriching request with auth header: {}", encodedAccessToken);

        requestContext.addZuulRequestHeader(ACCESS_TOKEN_HEADER, encodedAccessToken);

        eventGatewayApi.sendEvent(
            SendEventRequest.builder()
                .eventName(RefreshAccessTokenExpirationEvent.EVENT_NAME)
                .payload(new RefreshAccessTokenExpirationEvent(accessTokenResponse.getAccessTokenId()))
                .build()
        );
    }
}
