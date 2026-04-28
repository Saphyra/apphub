package com.github.saphyra.apphub.service.platform.storage.service;

import com.github.saphyra.apphub.service.platform.storage.client.StorageClient;
import com.github.saphyra.apphub.service.platform.storage.client.StorageClientProvider;
import com.github.saphyra.apphub.service.platform.storage.dao.Storage;
import com.github.saphyra.apphub.service.platform.storage.dao.StoredFile;
import com.github.saphyra.apphub.service.platform.storage.dao.StoredFileDao;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class DeleteFileServiceTest {
    private static final UUID STORED_FILE_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();

    @Mock
    private StoredFileDao storedFileDao;

    @Mock
    private StorageClientProvider storageClientProvider;

    @InjectMocks
    private DeleteFileService underTest;

    @Mock
    private StoredFile storedFile;

    @Mock
    private StorageClient storageClient;

    @Test
    public void forbiddenOperation() {
        given(storedFileDao.findById(STORED_FILE_ID)).willReturn(Optional.of(storedFile));
        given(storedFile.getUserId()).willReturn(UUID.randomUUID());

        Throwable ex = catchThrowable(() -> underTest.deleteFile(USER_ID, STORED_FILE_ID));

        ExceptionValidator.validateForbiddenOperation(ex);
    }

    @Test
    public void deleteFile() {
        given(storedFileDao.findById(STORED_FILE_ID)).willReturn(Optional.of(storedFile));
        given(storedFile.getUserId()).willReturn(USER_ID);
        given(storedFile.getStoredFileId()).willReturn(STORED_FILE_ID);
        given(storedFile.getStorage()).willReturn(Storage.FTP);
        given(storageClientProvider.getClientForType(Storage.FTP)).willReturn(storageClient);

        underTest.deleteFile(USER_ID, STORED_FILE_ID);

        verify(storedFileDao).delete(storedFile);
        verify(storageClient).delete(STORED_FILE_ID);
    }
}