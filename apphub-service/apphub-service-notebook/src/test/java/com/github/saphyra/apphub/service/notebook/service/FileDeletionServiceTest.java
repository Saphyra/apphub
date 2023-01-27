package com.github.saphyra.apphub.service.notebook.service;

import com.github.saphyra.apphub.service.notebook.dao.file.File;
import com.github.saphyra.apphub.service.notebook.dao.file.FileDao;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class FileDeletionServiceTest {
    private static final UUID LIST_ITEM_ID = UUID.randomUUID();
    private static final UUID FILE_ID = UUID.randomUUID();

    @Mock
    private FileDao fileDao;

    @Mock
    private StorageProxy storageProxy;

    @InjectMocks
    private FileDeletionService underTest;

    @Mock
    private File file;

    @Test
    public void deleteImage() {
        given(fileDao.findByParentValidated(LIST_ITEM_ID)).willReturn(file);

        given(file.getStoredFileId()).willReturn(FILE_ID);

        underTest.deleteImage(LIST_ITEM_ID);

        verify(storageProxy).deleteFile(FILE_ID);
        verify(fileDao).delete(file);
    }
}