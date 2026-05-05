package com.github.saphrya.apphub.service.platform.authorization.controller;

import com.github.saphrya.apphub.service.platform.authorization.service.LogoutService;
import com.github.saphyra.apphub.api.platform.authorization.server.AuthorizationEventController;
import com.github.saphyra.apphub.api.platform.event_gateway.model.request.SendEventRequest;
import com.github.saphyra.apphub.lib.event.DeleteAccountEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
class AuthorizationEventControllerImp implements AuthorizationEventController {
    private final LogoutService logoutService;

    @Override
    public void deleteAccountEvent(SendEventRequest<DeleteAccountEvent> request) {
        DeleteAccountEvent event = request.getPayload();
        log.info("Processing account deletion of user {}", event.getUserId());

        logoutService.invalidateAllRefreshTokens(event.getUserId());
    }
}
