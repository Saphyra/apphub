package com.github.saphyra.apphub.service.notebook.service.file;

import com.github.saphyra.apphub.api.feature.notebook.model.ListItemType;
import com.github.saphyra.apphub.api.feature.notebook.model.request.CreateFileRequest;
import com.github.saphyra.apphub.api.feature.notebook.model.request.FileMetadata;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.service.notebook.dao.list_item.list_item.ListItem;
import com.github.saphyra.apphub.service.notebook.dao.list_item.list_item.ListItemDao;
import com.github.saphyra.apphub.service.notebook.dao.list_item.list_item.ListItemFactory;
import com.github.saphyra.apphub.service.notebook.service.StorageProxy;
import com.github.saphyra.apphub.service.notebook.service.validator.CreateFileRequestValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class FileCreationServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID PARENT = UUID.randomUUID();
    private static final String TITLE = "title";
    private static final String FILE_NAME = "file-name";
    private static final Long SIZE = 1234L;
    private static final UUID STORED_FILE_ID = UUID.randomUUID();
    private static final String STORED_FILE_ID_STRING = "stored-file-id-string";

    @Mock
    private ListItemFactory listItemFactory;

    @Mock
    private ListItemDao listItemDao;

    @Mock
    private StorageProxy storageProxy;

    @Mock
    private CreateFileRequestValidator createFileRequestValidator;

    @Mock
    private UuidConverter uuidConverter;

    @InjectMocks
    private FileCreationService underTest;

    @Mock
    private ListItem listItem;

    @Test
    void create() {
        CreateFileRequest request = CreateFileRequest.builder()
            .title(TITLE)
            .parent(PARENT)
            .metadata(FileMetadata.builder()
                .fileName(FILE_NAME)
                .size(SIZE)
                .build())
            .build();

        given(storageProxy.createFile(FILE_NAME, SIZE)).willReturn(STORED_FILE_ID);
        given(uuidConverter.convertDomain(STORED_FILE_ID)).willReturn(STORED_FILE_ID_STRING);
        given(listItemFactory.create(USER_ID, PARENT, TITLE, ListItemType.FILE, STORED_FILE_ID_STRING)).willReturn(listItem);

        UUID result = underTest.create(USER_ID, request, ListItemType.FILE);

        then(createFileRequestValidator).should().validate(USER_ID, request);
        then(listItemDao).should().save(listItem);
        assertThat(result).isEqualTo(STORED_FILE_ID);
    }
}