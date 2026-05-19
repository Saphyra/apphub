package com.github.saphyra.apphub.service.notebook.service;

import com.github.saphyra.apphub.api.feature.notebook.model.ListItemType;
import com.github.saphyra.apphub.api.feature.notebook.model.response.NotebookView;
import com.github.saphyra.apphub.api.platform.storage.model.StoredFileResponse;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.service.notebook.dao.deprecated_content.Content;
import com.github.saphyra.apphub.service.notebook.dao.deprecated_content.ContentDao;
import com.github.saphyra.apphub.service.notebook.dao.deprecated_file.File;
import com.github.saphyra.apphub.service.notebook.dao.deprecated_file.FileDao;
import com.github.saphyra.apphub.service.notebook.dao.deprecated_list_item.DeprecatedListItem;
import com.github.saphyra.apphub.service.notebook.dao.deprecated_list_item.DeprecatedListItemDao;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
public class NotebookViewFactoryTest {
    private static final UUID LIST_ITEM_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID PARENT = UUID.randomUUID();
    private static final String TITLE = "title";
    private static final String VALUE = "value";
    private static final String PARENT_TITLE = "parent-title";
    private static final UUID STORED_FILE_ID = UUID.randomUUID();
    private static final String STORED_FILE_ID_STRING = "stored-file-id";

    @Mock
    private ContentDao contentDao;

    @Mock
    private DeprecatedListItemDao listItemDao;

    @Mock
    private FileDao fileDao;

    @Mock
    private UuidConverter uuidConverter;

    @Mock
    private StorageProxy storageProxy;

    @Mock
    private IsParentArchivedService isParentArchivedService;

    @InjectMocks
    private NotebookViewFactory underTest;

    @Mock
    private Content content;

    @Mock
    private DeprecatedListItem parentListItem;

    @Mock
    private File file;

    @Mock
    private StoredFileResponse storedFileResponse;

    @Test
    public void create_hasParent() {
        DeprecatedListItem listItem = DeprecatedListItem.builder()
            .listItemId(LIST_ITEM_ID)
            .userId(USER_ID)
            .parent(PARENT)
            .type(ListItemType.CATEGORY)
            .title(TITLE)
            .pinned(true)
            .archived(false)
            .build();

        given(listItemDao.findByIdValidated(PARENT)).willReturn(parentListItem);
        given(parentListItem.getTitle()).willReturn(PARENT_TITLE);
        given(isParentArchivedService.isAnyOfParentsArchived(PARENT)).willReturn(true);

        NotebookView result = underTest.create(listItem);

        assertThat(result.getId()).isEqualTo(LIST_ITEM_ID);
        assertThat(result.getTitle()).isEqualTo(TITLE);
        assertThat(result.getType()).isEqualTo(ListItemType.CATEGORY.name());
        assertThat(result.getValue()).isNull();
        assertThat(result.isPinned()).isTrue();
        assertThat(result.isArchived()).isTrue();
        assertThat(result.getParentId()).isEqualTo(PARENT);
        assertThat(result.getParentTitle()).isEqualTo(PARENT_TITLE);

        verifyNoInteractions(storageProxy);
    }

    @Test
    public void create_noParent() {
        DeprecatedListItem listItem = DeprecatedListItem.builder()
            .listItemId(LIST_ITEM_ID)
            .userId(USER_ID)
            .parent(null)
            .type(ListItemType.CATEGORY)
            .title(TITLE)
            .pinned(true)
            .archived(true)
            .build();

        NotebookView result = underTest.create(listItem);

        assertThat(result.getId()).isEqualTo(LIST_ITEM_ID);
        assertThat(result.getTitle()).isEqualTo(TITLE);
        assertThat(result.getType()).isEqualTo(ListItemType.CATEGORY.name());
        assertThat(result.getValue()).isNull();
        assertThat(result.isPinned()).isTrue();
        assertThat(result.isArchived()).isTrue();
        assertThat(result.getParentId()).isNull();
        assertThat(result.getParentTitle()).isNull();

        verifyNoInteractions(storageProxy);
    }

    @Test
    public void fillValueForLink() {
        DeprecatedListItem listItem = DeprecatedListItem.builder()
            .listItemId(LIST_ITEM_ID)
            .userId(USER_ID)
            .parent(PARENT)
            .type(ListItemType.LINK)
            .title(TITLE)
            .pinned(true)
            .archived(false)
            .build();

        given(contentDao.findByParentValidated(LIST_ITEM_ID)).willReturn(content);
        given(content.getContent()).willReturn(VALUE);
        given(isParentArchivedService.isAnyOfParentsArchived(PARENT)).willReturn(false);

        NotebookView result = underTest.create(listItem);

        assertThat(result.getId()).isEqualTo(LIST_ITEM_ID);
        assertThat(result.getTitle()).isEqualTo(TITLE);
        assertThat(result.getType()).isEqualTo(ListItemType.LINK.name());
        assertThat(result.getValue()).isEqualTo(VALUE);
        assertThat(result.isPinned()).isTrue();
        assertThat(result.isArchived()).isFalse();

        verifyNoInteractions(storageProxy);
    }

    @Test
    public void fillFieldsForImage() {
        DeprecatedListItem listItem = DeprecatedListItem.builder()
            .listItemId(LIST_ITEM_ID)
            .userId(USER_ID)
            .parent(PARENT)
            .type(ListItemType.IMAGE)
            .title(TITLE)
            .pinned(true)
            .archived(true)
            .build();

        given(fileDao.findByParentValidated(LIST_ITEM_ID)).willReturn(file);
        given(file.getStoredFileId()).willReturn(STORED_FILE_ID);
        given(uuidConverter.convertDomain(STORED_FILE_ID)).willReturn(STORED_FILE_ID_STRING);
        given(storageProxy.getFileMetadata(STORED_FILE_ID)).willReturn(storedFileResponse);
        given(storedFileResponse.getFileUploaded()).willReturn(true);

        NotebookView result = underTest.create(listItem);

        assertThat(result.getId()).isEqualTo(LIST_ITEM_ID);
        assertThat(result.getTitle()).isEqualTo(TITLE);
        assertThat(result.getType()).isEqualTo(ListItemType.IMAGE.name());
        assertThat(result.getValue()).isEqualTo(STORED_FILE_ID_STRING);
        assertThat(result.isPinned()).isTrue();
        assertThat(result.isEnabled()).isTrue();
    }

    @Test
    public void fillFieldsForFile() {
        DeprecatedListItem listItem = DeprecatedListItem.builder()
            .listItemId(LIST_ITEM_ID)
            .userId(USER_ID)
            .parent(PARENT)
            .type(ListItemType.FILE)
            .title(TITLE)
            .pinned(true)
            .archived(true)
            .build();

        given(fileDao.findByParentValidated(LIST_ITEM_ID)).willReturn(file);
        given(file.getStoredFileId()).willReturn(STORED_FILE_ID);
        given(uuidConverter.convertDomain(STORED_FILE_ID)).willReturn(STORED_FILE_ID_STRING);
        given(storageProxy.getFileMetadata(STORED_FILE_ID)).willReturn(storedFileResponse);
        given(storedFileResponse.getFileUploaded()).willReturn(true);

        NotebookView result = underTest.create(listItem);

        assertThat(result.getId()).isEqualTo(LIST_ITEM_ID);
        assertThat(result.getTitle()).isEqualTo(TITLE);
        assertThat(result.getType()).isEqualTo(ListItemType.FILE.name());
        assertThat(result.getValue()).isEqualTo(STORED_FILE_ID_STRING);
        assertThat(result.isPinned()).isTrue();
        assertThat(result.isEnabled()).isTrue();
    }
}