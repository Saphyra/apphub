package com.github.saphyra.apphub.service.notebook.service.file;

import com.github.saphyra.apphub.api.notebook.model.request.CreateFileRequest;
import com.github.saphyra.apphub.api.notebook.model.request.FileMetadata;
import com.github.saphyra.apphub.service.notebook.dao.file.File;
import com.github.saphyra.apphub.service.notebook.dao.file.FileDao;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItem;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDao;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemType;
import com.github.saphyra.apphub.service.notebook.service.validator.CreateFileRequestValidator;
import com.github.saphyra.apphub.service.notebook.service.FileFactory;
import com.github.saphyra.apphub.service.notebook.service.ListItemFactory;
import com.github.saphyra.apphub.service.notebook.service.StorageProxy;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class FileCreationServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String TITLE = "title";
    private static final UUID PARENT = UUID.randomUUID();
    private static final String FILE_NAME = "file-name";
    private static final Long SIZE = 2345L;
    private static final UUID FILE_ID = UUID.randomUUID();
    private static final UUID LIST_ITEM_ID = UUID.randomUUID();

    @Mock
    private CreateFileRequestValidator createFileRequestValidator;

    @Mock
    private ListItemFactory listItemFactory;

    @Mock
    private ListItemDao listItemDao;

    @Mock
    private FileFactory fileFactory;

    @Mock
    private StorageProxy storageProxy;

    @Mock
    private FileDao fileDao;

    @InjectMocks
    private FileCreationService underTest;

    @Mock
    private CreateFileRequest request;

    @Mock
    private ListItem listItem;

    @Mock
    private File file;

    @Mock
    private FileMetadata metadata;

    @Test
    public void createImage() {
        given(request.getTitle()).willReturn(TITLE);
        given(request.getParent()).willReturn(PARENT);
        given(request.getMetadata()).willReturn(metadata);
        given(metadata.getFileName()).willReturn(FILE_NAME);
        given(metadata.getSize()).willReturn(SIZE);

        given(listItem.getListItemId()).willReturn(LIST_ITEM_ID);

        given(storageProxy.createFile(FILE_NAME, SIZE)).willReturn(FILE_ID);
        given(listItemFactory.create(USER_ID, TITLE, PARENT, ListItemType.FILE)).willReturn(listItem);
        given(fileFactory.create(USER_ID, LIST_ITEM_ID, FILE_ID)).willReturn(file);

        UUID result = underTest.createFile(USER_ID, request);

        verify(createFileRequestValidator).validate(request);
        verify(listItemDao).save(listItem);
        verify(fileDao).save(file);

        assertThat(result).isEqualTo(FILE_ID);
    }
}