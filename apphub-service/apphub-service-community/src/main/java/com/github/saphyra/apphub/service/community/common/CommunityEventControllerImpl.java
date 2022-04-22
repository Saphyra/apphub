package com.github.saphyra.apphub.service.community.common;

import com.github.saphyra.apphub.api.community.server.CommunityEventController;
import com.github.saphyra.apphub.api.platform.event_gateway.model.request.SendEventRequest;
import com.github.saphyra.apphub.lib.common_domain.DeleteByUserIdDao;
import com.github.saphyra.apphub.lib.event.DeleteAccountEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import javax.transaction.Transactional;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
public class CommunityEventControllerImpl implements CommunityEventController {
    private final List<DeleteByUserIdDao> daos;

    @Override
    @Transactional
    public void deleteAccountEvent(SendEventRequest<DeleteAccountEvent> request) {
        UUID userId = request.getPayload().getUserId();

        log.info("Deleting records for user {}", userId);

        daos.forEach(deleteByUserIdDao -> deleteByUserIdDao.deleteByUserId(userId));
    }
}
