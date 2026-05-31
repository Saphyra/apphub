package com.github.saphyra.apphub.service.feature.calendar.common;

import com.github.saphyra.apphub.api.feature.calendar.server.CalendarEventController;
import com.github.saphyra.apphub.api.platform.event_gateway.model.request.SendEventRequest;
import com.github.saphyra.apphub.lib.common_domain.AccessToken;
import com.github.saphyra.apphub.lib.common_domain.DeleteByUserIdDao;
import com.github.saphyra.apphub.lib.event.DeleteAccountEvent;
import com.github.saphyra.apphub.lib.security.access_token.AccessTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
public class CalendarEventControllerImpl implements CalendarEventController {
    private final List<DeleteByUserIdDao> daos;
    private final AccessTokenProvider accessTokenProvider;

    @Override
    @SneakyThrows
    public void deleteAccountEvent(SendEventRequest<DeleteAccountEvent> request) {
        UUID userId = request.getPayload()
            .getUserId();

        log.info("Deleting records for user {}", userId);

        try (var _ = accessTokenProvider.set(AccessToken.builder().userId(userId).build())) {
            daos.forEach(deleteByUserIdDao -> deleteByUserIdDao.deleteByUserId(userId));
        }
    }
}
