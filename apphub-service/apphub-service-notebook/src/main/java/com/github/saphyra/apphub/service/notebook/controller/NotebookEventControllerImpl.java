package com.github.saphyra.apphub.service.notebook.controller;

import com.github.saphyra.apphub.api.notebook.server.NotebookEventController;
import com.github.saphyra.apphub.api.platform.event_gateway.model.request.SendEventRequest;
import com.github.saphyra.apphub.lib.common_domain.DeleteByUserIdDao;
import com.github.saphyra.apphub.lib.event.DeleteAccountEvent;
import com.github.saphyra.apphub.service.notebook.service.custom_table_deprecated.CustomTableDeletionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import jakarta.transaction.Transactional;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
public class NotebookEventControllerImpl implements NotebookEventController {
    private final CustomTableDeletionService customTableDeletionService;
    private final List<DeleteByUserIdDao> daos;

    @Override
    @Transactional
    public void deleteAccountEvent(SendEventRequest<DeleteAccountEvent> request) {
        UUID userId = request.getPayload().getUserId();
        log.info("DeleteAccountEvent arrived with userId {}", userId);

        customTableDeletionService.deleteForUser(userId);

        daos.forEach(deleteByUserIdDao -> deleteByUserIdDao.deleteByUserId(userId));
    }
}
