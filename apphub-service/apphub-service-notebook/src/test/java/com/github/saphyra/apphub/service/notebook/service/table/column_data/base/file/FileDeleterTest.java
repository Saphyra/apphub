package com.github.saphyra.apphub.service.notebook.service.table.column_data.base.file;

import com.github.saphyra.apphub.service.notebook.dao.file.File;
import com.github.saphyra.apphub.service.notebook.dao.file.FileDao;
import com.github.saphyra.apphub.service.notebook.service.StorageProxy;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class FileDeleterTest {
    private static final UUID COLUMN_ID = UUID.randomUUID();
    private static final UUID STORED_FILE_ID = UUID.randomUUID();

    @Mock
    private FileDao fileDao;

    @Mock
    private StorageProxy storageProxy;

    @InjectMocks
    private FileDeleter underTest;

    @Mock
    private File file;

    @Test
    void deleteFile() {
        given(fileDao.findByParentValidated(COLUMN_ID)).willReturn(file);
        given(file.getStoredFileId()).willReturn(STORED_FILE_ID);

        underTest.deleteFile(COLUMN_ID);

        then(fileDao).should().delete(file);
        then(storageProxy).should().deleteFile(STORED_FILE_ID);
    }
}