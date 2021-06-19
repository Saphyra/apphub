package com.github.saphyra.apphub.service.user.data;

import com.github.saphyra.apphub.api.platform.event_gateway.client.EventGatewayApiClient;
import com.github.saphyra.apphub.api.platform.event_gateway.model.request.SendEventRequest;
import com.github.saphyra.apphub.api.user.server.UserEventController;
import com.github.saphyra.apphub.lib.event.DeleteAccountEvent;
import com.github.saphyra.apphub.lib.event.PageVisitedEvent;
import com.github.saphyra.apphub.lib.web_utils.LocaleProvider;
import com.github.saphyra.apphub.service.user.authentication.dao.AccessTokenDao;
import com.github.saphyra.apphub.service.user.data.dao.role.RoleDao;
import com.github.saphyra.apphub.service.user.data.dao.user.User;
import com.github.saphyra.apphub.service.user.data.dao.user.UserDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import javax.transaction.Transactional;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
class UserEventControllerImpl implements UserEventController {
    private final AccessTokenDao accessTokenDao;
    private final RoleDao roleDao;
    private final UserDao userDao;
    private final EventGatewayApiClient eventGatewayClient;
    private final LocaleProvider localeProvider;

    @Override
    @Transactional
    public void deleteAccountEvent(SendEventRequest<DeleteAccountEvent> request) {
        log.info("Processing event {}", request.getPayload());
        log.debug("Request: {}", request);
        UUID userId = request.getPayload().getUserId();
        accessTokenDao.deleteByUserId(userId);
        roleDao.deleteByUserId(userId);
        userDao.deleteById(userId);
    }

    @Override
    public void pageVisitedEvent(SendEventRequest<PageVisitedEvent> request) {
        PageVisitedEvent event = request.getPayload();
        accessTokenDao.findById(event.getAccessTokenId()).ifPresent(accessToken -> {
            accessToken.setLastVisitedPage(event.getPageUrl());
            accessTokenDao.save(accessToken);
        });
    }

    @Override
    public void triggerAccountDeletion() {
        userDao.getUsersMarkedToDelete()
            .stream()
            .limit(5)
            .map(User::getUserId)
            .forEach(this::deleteAccount);
    }

    private void deleteAccount(UUID userId) {
        SendEventRequest<DeleteAccountEvent> event = SendEventRequest.<DeleteAccountEvent>builder()
            .eventName(DeleteAccountEvent.EVENT_NAME)
            .payload(new DeleteAccountEvent(userId))
            .build()
            .blockingRequest(false);

        eventGatewayClient.sendEvent(event, localeProvider.getLocaleValidated());
    }
}
