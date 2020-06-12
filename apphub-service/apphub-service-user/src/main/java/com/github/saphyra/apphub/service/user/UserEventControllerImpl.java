package com.github.saphyra.apphub.service.user;

import com.github.saphyra.apphub.api.platform.event_gateway.model.request.SendEventRequest;
import com.github.saphyra.apphub.api.user.server.UserEventController;
import com.github.saphyra.apphub.lib.event.DeleteAccountEvent;
import com.github.saphyra.apphub.service.user.authentication.dao.AccessTokenDao;
import com.github.saphyra.apphub.service.user.data.dao.user.UserDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
public class UserEventControllerImpl implements UserEventController {
    private final AccessTokenDao accessTokenDao;
    private final UserDao userDao;

    @Override
    public void deleteAccountEvent(SendEventRequest<DeleteAccountEvent> request) {
        log.info("Processing event {}", request.getPayload());
        log.debug("Request: {}", request);
        UUID userId = request.getPayload().getUserId();
        accessTokenDao.deleteByUserId(userId);
        userDao.deleteById(userId);
    }
}
