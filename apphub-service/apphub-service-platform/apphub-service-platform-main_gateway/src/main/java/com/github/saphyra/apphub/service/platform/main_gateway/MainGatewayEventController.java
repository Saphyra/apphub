package com.github.saphyra.apphub.service.platform.main_gateway;

import com.github.saphyra.apphub.api.platform.event_gateway.model.request.SendEventRequest;
import com.github.saphyra.apphub.lib.config.common.endpoints.UserEndpoints;
import com.github.saphyra.apphub.service.platform.main_gateway.service.AccessTokenCache;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class MainGatewayEventController {
    private final AccessTokenCache accessTokenCache;

    @PostMapping(UserEndpoints.EVENT_ACCESS_TOKEN_INVALIDATED)
    void accessTokenInvalidated(@RequestBody SendEventRequest<List<UUID>> sendEventRequest) {
        sendEventRequest.getPayload()
            .forEach(accessTokenCache::invalidate);
    }
}
