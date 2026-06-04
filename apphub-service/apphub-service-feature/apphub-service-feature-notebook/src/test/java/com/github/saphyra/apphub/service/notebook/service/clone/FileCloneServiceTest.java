package com.github.saphyra.apphub.service.notebook.service.clone;

import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.service.notebook.dao.list_item.list_item.ListItem;
import com.github.saphyra.apphub.service.notebook.dao.list_item.list_item.ListItemDao;
import com.github.saphyra.apphub.service.notebook.dao.list_item.list_item.ListItemFactory;
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
class FileCloneServiceTest {
    private static final UUID PARENT = UUID.randomUUID();
    private static final String ORIGINAL_DATA = "original-data";
    private static final UUID ORIGINAL_STORED_FILE_ID = UUID.randomUUID();
    private static final String CLONED_DATA = "cloned-data";
    private static final UUID CLONED_STORED_FILE_ID = UUID.randomUUID();

    @Mock
    private ListItemDao listItemDao;

    @Mock
    private ListItemFactory listItemFactory;

    @Mock
    private StorageProxy storageProxy;

    @Mock
    private UuidConverter uuidConverter;

    @InjectMocks
    private FileCloneService underTest;

    @Mock
    private ListItem toClone;

    @Mock
    private ListItem clone;

    @Test
    void cloneFile() {
        given(toClone.getData()).willReturn(ORIGINAL_DATA);
        given(listItemFactory.clone(PARENT, toClone, CLONED_DATA)).willReturn(clone);
        given(uuidConverter.convertEntity(ORIGINAL_DATA)).willReturn(ORIGINAL_STORED_FILE_ID);
        given(storageProxy.cloneFile(ORIGINAL_STORED_FILE_ID)).willReturn(CLONED_STORED_FILE_ID);
        given(uuidConverter.convertDomain(CLONED_STORED_FILE_ID)).willReturn(CLONED_DATA);


        underTest.cloneFile(PARENT, toClone);

        then(listItemDao).should().save(clone);
    }
}