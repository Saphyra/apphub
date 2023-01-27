package com.github.saphyra.apphub.service.platform.encryption.common;

import com.github.saphyra.apphub.api.platform.encryption.server.EncryptionEventController;
import com.github.saphyra.apphub.api.platform.event_gateway.model.request.SendEventRequest;
import com.github.saphyra.apphub.lib.common_domain.DeleteByUserIdDao;
import com.github.saphyra.apphub.lib.event.DeleteAccountEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import jakarta.transaction.Transactional;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class EncryptionEventControllerImpl implements EncryptionEventController {
    private final List<DeleteByUserIdDao> daos;

    @Override
    @Transactional
    public void deleteAccountEvent(SendEventRequest<DeleteAccountEvent> request) {
        log.info("Processing {}", request.getPayload());
        daos.forEach(dao -> dao.deleteByUserId(request.getPayload().getUserId()));
    }
}
