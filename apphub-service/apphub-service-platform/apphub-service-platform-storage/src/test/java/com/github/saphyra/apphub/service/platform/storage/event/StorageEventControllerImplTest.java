package com.github.saphyra.apphub.service.platform.storage.event;

import com.github.saphyra.apphub.api.platform.event_gateway.model.request.SendEventRequest;
import com.github.saphyra.apphub.lib.event.DeleteAccountEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class StorageEventControllerImplTest {
    private static final UUID USER_ID = UUID.randomUUID();

    @Mock
    private DeleteAccountEventProcessor deleteAccountEventProcessor;

    @Mock
    private StoredFileCleanupEventProcessor storedFileCleanupEventProcessor;

    @InjectMocks
    private StorageEventControllerImpl underTest;

    @Test
    public void deleteAccountEvent() {
        SendEventRequest<DeleteAccountEvent> request = SendEventRequest.<DeleteAccountEvent>builder()
            .payload(new DeleteAccountEvent(USER_ID))
            .build();

        underTest.deleteAccountEvent(request);

        verify(deleteAccountEventProcessor).deleteUserData(USER_ID);
    }

    @Test
    public void cleanUpStoredFiles() {
        underTest.cleanUpStoredFiles();

        verify(storedFileCleanupEventProcessor).cleanup();
    }
}