package com.github.saphyra.apphub.service.platform.storage.service;

import com.github.saphyra.apphub.lib.concurrency.ExecutorServiceBean;
import com.github.saphyra.apphub.service.platform.storage.client.StorageClient;
import com.github.saphyra.apphub.service.platform.storage.client.StorageClientProvider;
import com.github.saphyra.apphub.service.platform.storage.dao.stored_file.Storage;
import com.github.saphyra.apphub.service.platform.storage.dao.stored_file.StoredFile;
import com.github.saphyra.apphub.service.platform.storage.dao.stored_file.StoredFileDao;
import com.github.saphyra.apphub.service.platform.storage.dao.stored_file.StoredFileFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willAnswer;

@ExtendWith(MockitoExtension.class)
class CloneFileServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID STORED_FILE_ID = UUID.randomUUID();
    private static final UUID CLONE_ID = UUID.randomUUID();

    @Mock
    private StoredFileDao storedFileDao;

    @Mock
    private StoredFileFactory storedFileFactory;

    @Mock
    private StorageClientProvider storageClientProvider;

    @Mock
    private ExecutorServiceBean executorServiceBean;

    @InjectMocks
    private CloneFileService underTest;

    @Mock
    private StoredFile storedFile;

    @Mock
    private StoredFile clone;

    @Mock
    private StorageClient storageClient;

    @Test
    void clone_notS3() {
        given(storedFileDao.findByIdValidated(USER_ID, STORED_FILE_ID)).willReturn(storedFile);
        given(storedFile.getStorage()).willReturn(Storage.FTP);

        Throwable thrown = catchThrowable(() -> underTest.clone(USER_ID, STORED_FILE_ID));

        assertThat(thrown).isNotNull();
        then(storageClientProvider).shouldHaveNoInteractions();
    }

    @Test
    void clone_success() {
        given(storedFileDao.findByIdValidated(USER_ID, STORED_FILE_ID)).willReturn(storedFile);
        given(storedFile.getStorage()).willReturn(Storage.S3);
        given(storedFileFactory.clone(storedFile)).willReturn(clone);
        given(clone.getStoredFileId()).willReturn(CLONE_ID);
        given(storageClientProvider.getClientForType(Storage.S3)).willReturn(storageClient);
        willAnswer(invocation -> {
            invocation.getArgument(0, Runnable.class).run();
            return null;
        }).given(executorServiceBean).execute(any());

        UUID result = underTest.clone(USER_ID, STORED_FILE_ID);

        assertThat(result).isEqualTo(CLONE_ID);
        then(storageClient).should().clone(STORED_FILE_ID, CLONE_ID);
    }
}