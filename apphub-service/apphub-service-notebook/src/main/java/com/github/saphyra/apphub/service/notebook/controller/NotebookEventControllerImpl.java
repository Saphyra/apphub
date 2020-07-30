package com.github.saphyra.apphub.service.notebook.controller;

import com.github.saphyra.apphub.api.notebook.server.NotebookEventController;
import com.github.saphyra.apphub.api.platform.event_gateway.model.request.SendEventRequest;
import com.github.saphyra.apphub.lib.event.DeleteAccountEvent;
import com.github.saphyra.apphub.service.notebook.dao.checklist_item.ChecklistItemDao;
import com.github.saphyra.apphub.service.notebook.dao.content.ContentDao;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import javax.transaction.Transactional;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
public class NotebookEventControllerImpl implements NotebookEventController {
    private final ListItemDao listItemDao;
    private final ContentDao contentDao;
    private final ChecklistItemDao checklistItemDao;

    @Override
    @Transactional
    public void deleteAccountEvent(SendEventRequest<DeleteAccountEvent> request) {
        UUID userId = request.getPayload().getUserId();
        log.info("SendEventRequest arrived with userId {}", userId);
        listItemDao.deleteByUserId(userId);
        contentDao.deleteByUserId(userId);
        checklistItemDao.deleteByUserId(userId);
    }
}
