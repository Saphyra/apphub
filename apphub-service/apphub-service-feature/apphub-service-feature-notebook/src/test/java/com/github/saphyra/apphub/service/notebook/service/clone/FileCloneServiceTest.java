package com.github.saphyra.apphub.service.notebook.service.clone;

import com.github.saphyra.apphub.service.notebook.dao.file.File;
import com.github.saphyra.apphub.service.notebook.dao.file.FileDao;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItem;
import com.github.saphyra.apphub.service.notebook.service.StorageProxy;
import com.github.saphyra.apphub.service.notebook.service.FileFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class FileCloneServiceTest {
    private static final UUID ORIGINAL_PARENT = UUID.randomUUID();
    private static final UUID FILE_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID NEW_PARENT = UUID.randomUUID();

    @Mock
    private FileDao fileDao;

    @Mock
    private StorageProxy storageProxy;

    @Mock
    private FileFactory fileFactory;

    @InjectMocks
    private FileCloneService underTest;

    @Mock
    private ListItem toClone;

    @Mock
    private ListItem listItemClone;

    @Mock
    private File file;

    @Mock
    private File fileClone;

    @Test
    public void cloneImage() {
        given(toClone.getListItemId()).willReturn(ORIGINAL_PARENT);
        given(toClone.getUserId()).willReturn(USER_ID);

        given(listItemClone.getListItemId()).willReturn(NEW_PARENT);

        given(file.getStoredFileId()).willReturn(FILE_ID);

        given(fileDao.findByParentValidated(ORIGINAL_PARENT)).willReturn(file);
        given(fileFactory.create(USER_ID, NEW_PARENT, FILE_ID)).willReturn(fileClone);

        underTest.cloneFile(toClone, listItemClone);

        verify(fileDao).save(fileClone);
    }
}