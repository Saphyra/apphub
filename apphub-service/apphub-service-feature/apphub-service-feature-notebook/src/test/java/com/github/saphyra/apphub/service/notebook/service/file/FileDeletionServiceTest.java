package com.github.saphyra.apphub.service.notebook.service.file;

import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.service.notebook.dao.list_item.list_item.ListItem;
import com.github.saphyra.apphub.service.notebook.dao.list_item.list_item.ListItemDao;
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
class FileDeletionServiceTest {
    private static final String DATA = "data";
    private static final UUID STORED_FILE_ID = UUID.randomUUID();

    @Mock
    private ListItemDao listItemDao;

    @Mock
    private UuidConverter uuidConverter;

    @Mock
    private StorageProxy storageProxy;

    @InjectMocks
    private FileDeletionService underTest;

    @Mock
    private ListItem listItem;

    @Test
    void deleteFile() {
        given(listItem.getData()).willReturn(DATA);
        given(uuidConverter.convertEntity(DATA)).willReturn(STORED_FILE_ID);

        underTest.deleteFile(listItem);

        then(storageProxy).should().deleteFile(STORED_FILE_ID);
        then(listItemDao).should().delete(listItem);
    }
}