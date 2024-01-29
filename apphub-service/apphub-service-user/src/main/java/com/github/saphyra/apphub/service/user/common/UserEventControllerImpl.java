package com.github.saphyra.apphub.service.user.common;

import com.github.saphyra.apphub.api.platform.event_gateway.client.EventGatewayApiClient;
import com.github.saphyra.apphub.api.platform.event_gateway.model.request.SendEventRequest;
import com.github.saphyra.apphub.api.user.server.UserEventController;
import com.github.saphyra.apphub.lib.common_domain.DeleteByUserIdDao;
import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.lib.event.DeleteAccountEvent;
import com.github.saphyra.apphub.lib.web_utils.LocaleProvider;
import com.github.saphyra.apphub.service.user.ban.service.RevokeBanService;
import com.github.saphyra.apphub.service.user.config.UserProperties;
import com.github.saphyra.apphub.service.user.data.dao.user.User;
import com.github.saphyra.apphub.service.user.data.dao.user.UserDao;
import jakarta.transaction.Transactional;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

import static java.util.Objects.isNull;

@RestController
@RequiredArgsConstructor
@Slf4j
@Builder
class UserEventControllerImpl implements UserEventController {
    private final UserDao userDao;
    private final EventGatewayApiClient eventGatewayClient;
    private final LocaleProvider localeProvider;
    private final RevokeBanService revokeBanService;
    private final DateTimeUtil dateTimeUtil;
    private final List<DeleteByUserIdDao> deleteByUserIdDaos;
    private final UserProperties userProperties;

    @Override
    @Transactional
    public void deleteAccountEvent(SendEventRequest<DeleteAccountEvent> request) {
        log.info("Processing event {}", request.getEventName());
        log.debug("Request: {}", request);
        UUID userId = request.getPayload().getUserId();
        deleteByUserIdDaos.forEach(deleteByUserIdDao -> deleteByUserIdDao.deleteByUserId(userId));
    }

    @Override
    public void triggerAccountDeletion() {
        userDao.getUsersMarkedToDelete()
            .stream()
            .filter(user -> isNull(user.getMarkedForDeletionAt()) || user.getMarkedForDeletionAt().isBefore(dateTimeUtil.getCurrentDateTime()))
            .limit(userProperties.getDeleteAccountBatchCount())
            .map(User::getUserId)
            .forEach(this::deleteAccount);
    }

    @Override
    public void triggerRevokeExpiredBans() {
        revokeBanService.revokeExpiredBans();
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
