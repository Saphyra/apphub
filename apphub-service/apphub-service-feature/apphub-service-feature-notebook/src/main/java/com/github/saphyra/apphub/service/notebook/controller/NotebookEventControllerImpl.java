package com.github.saphyra.apphub.service.notebook.controller;

import com.github.saphyra.apphub.api.feature.notebook.server.NotebookEventController;
import com.github.saphyra.apphub.api.platform.event_gateway.model.request.SendEventRequest;
import com.github.saphyra.apphub.lib.common_domain.AccessToken;
import com.github.saphyra.apphub.lib.common_domain.DeleteByUserIdDao;
import com.github.saphyra.apphub.lib.event.DeleteAccountEvent;
import com.github.saphyra.apphub.lib.security.access_token.AccessTokenProvider;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
public class NotebookEventControllerImpl implements NotebookEventController {
    private final List<DeleteByUserIdDao> daos;
    private final AccessTokenProvider accessTokenProvider;

    @Override
    @SneakyThrows
    @Transactional
    public void deleteAccountEvent(SendEventRequest<DeleteAccountEvent> request) {
        UUID userId = request.getPayload().getUserId();
        log.info("DeleteAccountEvent arrived with userId {}", userId);

        try (AutoCloseable _ = accessTokenProvider.set(AccessToken.builder().userId(userId).build())) {
            daos.forEach(deleteByUserIdDao -> deleteByUserIdDao.deleteByUserId(userId));
        }
    }
}
