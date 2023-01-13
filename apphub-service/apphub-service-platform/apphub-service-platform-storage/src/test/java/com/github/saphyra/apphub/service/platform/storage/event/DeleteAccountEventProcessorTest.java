package com.github.saphyra.apphub.service.platform.storage.event;

import com.github.saphyra.apphub.service.platform.storage.dao.StoredFile;
import com.github.saphyra.apphub.service.platform.storage.dao.StoredFileDao;
import com.github.saphyra.apphub.service.platform.storage.service.DeleteFileService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;
import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class DeleteAccountEventProcessorTest {
    private static final UUID USER_ID = UUID.randomUUID();

    @Mock
    private StoredFileDao storedFileDao;

    @Mock
    private DeleteFileService deleteFileService;

    @InjectMocks
    private DeleteAccountEventProcessor underTest;

    @Mock
    private StoredFile storedFile;

    @Test
    public void deleteUserData() {
        given(storedFileDao.getByUserId(USER_ID)).willReturn(List.of(storedFile));

        underTest.deleteUserData(USER_ID);

        verify(deleteFileService).deleteFile(USER_ID, storedFile);
    }
}