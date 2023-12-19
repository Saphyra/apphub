package com.github.saphyra.apphub.service.utils.common;

import com.github.saphyra.apphub.api.platform.event_gateway.model.request.SendEventRequest;
import com.github.saphyra.apphub.api.utils.server.UtilsEventController;
import com.github.saphyra.apphub.lib.common_domain.DeleteByUserIdDao;
import com.github.saphyra.apphub.lib.event.DeleteAccountEvent;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
public class UtilsEventControllerImpl implements UtilsEventController {
    private final List<DeleteByUserIdDao> daos;

    @Override
    @Transactional
    public void deleteAccountEvent(SendEventRequest<DeleteAccountEvent> request) {
        UUID userId = request.getPayload().getUserId();
        log.info("DeleteAccountEvent arrived with userId {}", userId);

        daos.forEach(deleteByUserIdDao -> deleteByUserIdDao.deleteByUserId(userId));
    }
}
