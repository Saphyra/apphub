package com.github.saphyra.apphub.service.feature.task_manager.common;

import com.github.saphyra.apphub.api.platform.event_gateway.model.request.SendEventRequest;
import com.github.saphyra.apphub.lib.event.DeleteAccountEvent;
import com.github.saphyra.apphub.service.feature.task_manager.domain.UserDeletionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class TaskManagerEventControllerImplTest {
    private static final UUID USER_ID = UUID.randomUUID();

    @Mock
    private UserDeletionService userDeletionService;

    @InjectMocks
    private TaskManagerEventControllerImpl underTest;

    @Test
    void deleteAccountEvent() {
        SendEventRequest<DeleteAccountEvent> request = SendEventRequest.<DeleteAccountEvent>builder()
            .payload(new DeleteAccountEvent(USER_ID))
            .build();

        underTest.deleteAccountEvent(request);

        then(userDeletionService).should().delete(USER_ID);
    }
}