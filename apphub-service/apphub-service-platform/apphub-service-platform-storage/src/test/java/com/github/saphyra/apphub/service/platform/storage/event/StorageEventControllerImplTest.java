package com.github.saphyra.apphub.service.platform.storage.event;

import com.github.saphyra.apphub.api.platform.event_gateway.model.request.SendEventRequest;
import com.github.saphyra.apphub.lib.concurrency.ExecutorServiceBean;
import com.github.saphyra.apphub.lib.concurrency.ExecutorServiceBeenTestUtils;
import com.github.saphyra.apphub.lib.error_report.ErrorReporterService;
import com.github.saphyra.apphub.lib.event.DeleteAccountEvent;
import com.github.saphyra.apphub.service.platform.storage.client.StorageCleaner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class StorageEventControllerImplTest {
    private static final UUID USER_ID = UUID.randomUUID();

    @Mock
    private DeleteAccountEventProcessor deleteAccountEventProcessor;

    @Mock
    private StorageCleaner storageCleaner;

    @Mock
    private StoredFileCleanupEventProcessor storedFileCleanupEventProcessor;

    private StorageEventControllerImpl underTest;

    @BeforeEach
    void setUp() {
        ExecutorServiceBean executorServiceBean = ExecutorServiceBeenTestUtils.create(mock(ErrorReporterService.class));
        underTest = new StorageEventControllerImpl(deleteAccountEventProcessor, storedFileCleanupEventProcessor, List.of(storageCleaner), executorServiceBean);
    }

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

    @Test
    void cleanupFiled() {
        underTest.cleanupFiles();

        then(storageCleaner).should(timeout(1000)).cleanup();
    }
}