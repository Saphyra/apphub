package com.github.saphyra.apphub.service.feature.task_manager.common;

import com.github.saphyra.apphub.api.feature.task_manager.server.TaskManagerEventController;
import com.github.saphyra.apphub.api.platform.event_gateway.model.request.SendEventRequest;
import com.github.saphyra.apphub.lib.event.DeleteAccountEvent;
import com.github.saphyra.apphub.service.feature.task_manager.domain.UserDeletionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
class TaskManagerEventControllerImpl implements TaskManagerEventController {
    private final UserDeletionService userDeletionService;

    @Override
    public void deleteAccountEvent(SendEventRequest<DeleteAccountEvent> request) {
        log.info("Processing account deletion for user {}", request.getPayload().getUserId());

        userDeletionService.delete(request.getPayload().getUserId());
    }
}
