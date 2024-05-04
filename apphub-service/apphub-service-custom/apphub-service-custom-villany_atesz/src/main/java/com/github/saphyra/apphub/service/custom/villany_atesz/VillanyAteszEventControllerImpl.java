package com.github.saphyra.apphub.service.custom.villany_atesz;

import com.github.saphyra.apphub.api.custom.villany_atesz.server.VillanyAteszEventController;
import com.github.saphyra.apphub.api.platform.event_gateway.model.request.SendEventRequest;
import com.github.saphyra.apphub.lib.common_domain.DeleteByUserIdDao;
import com.github.saphyra.apphub.lib.event.DeleteAccountEvent;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
//TODO unit test
class VillanyAteszEventControllerImpl implements VillanyAteszEventController {
    private final List<DeleteByUserIdDao> deleteByUserIdDaos;

    @Override
    @Transactional
    public void deleteAccountEvent(SendEventRequest<DeleteAccountEvent> request) {
        DeleteAccountEvent event = request.getPayload();
        log.info("Processing event {}", event);
        deleteByUserIdDaos.forEach(deleteByUserIdDao -> deleteByUserIdDao.deleteByUserId(event.getUserId()));
    }
}
