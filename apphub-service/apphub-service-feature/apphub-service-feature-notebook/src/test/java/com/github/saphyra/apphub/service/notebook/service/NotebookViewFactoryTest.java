package com.github.saphyra.apphub.service.notebook.service;

import com.github.saphyra.apphub.api.feature.notebook.model.ListItemType;
import com.github.saphyra.apphub.api.feature.notebook.model.response.NotebookView;
import com.github.saphyra.apphub.api.platform.storage.model.StoredFileResponse;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.service.notebook.dao.list_item.list_item.ListItem;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class NotebookViewFactoryTest {
    private static final UUID LIST_ITEM_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID PARENT_ID = UUID.randomUUID();
    private static final UUID STORED_FILE_ID = UUID.randomUUID();
    private static final String TITLE = "title";
    private static final String DATA = "data";

    @Mock
    private UuidConverter uuidConverter;

    @Mock
    private StorageProxy storageProxy;

    @Mock
    private IsParentArchivedService isParentArchivedService;

    @InjectMocks
    private NotebookViewFactory underTest;

    @Test
    void create_link() {
        ListItem listItem = createListItem(ListItemType.LINK, false, PARENT_ID, DATA);
        given(isParentArchivedService.isAnyOfParentsArchived(USER_ID, PARENT_ID)).willReturn(false);

        NotebookView result = underTest.create(listItem);

        assertThat(result)
            .returns(LIST_ITEM_ID, NotebookView::getId)
            .returns(TITLE, NotebookView::getTitle)
            .returns(ListItemType.LINK.name(), NotebookView::getType)
            .returns(DATA, NotebookView::getValue)
            .returns(PARENT_ID, NotebookView::getParentId)
            .returns(true, NotebookView::isPinned)
            .returns(false, NotebookView::isArchived)
            .returns(true, NotebookView::isEnabled);

        then(storageProxy).shouldHaveNoInteractions();
    }

    @Test
    void create_archivedItem_skipsParentCheck() {
        ListItem listItem = createListItem(ListItemType.TEXT, true, PARENT_ID, null);

        NotebookView result = underTest.create(listItem);

        assertThat(result)
            .returns(true, NotebookView::isArchived)
            .returns(null, NotebookView::getValue)
            .returns(true, NotebookView::isEnabled);

        then(isParentArchivedService).should(never()).isAnyOfParentsArchived(USER_ID, PARENT_ID);
    }

    @Test
    void create_text() {
        ListItem listItem = createListItem(ListItemType.TEXT, false, PARENT_ID, null);
        given(isParentArchivedService.isAnyOfParentsArchived(USER_ID, PARENT_ID)).willReturn(true);

        NotebookView result = underTest.create(listItem);

        assertThat(result)
            .returns(null, NotebookView::getValue)
            .returns(true, NotebookView::isArchived)
            .returns(true, NotebookView::isEnabled);

        then(uuidConverter).shouldHaveNoInteractions();
        then(storageProxy).shouldHaveNoInteractions();
    }

    @ParameterizedTest
    @EnumSource(value = ListItemType.class, names = {"FILE", "IMAGE"})
    void create_fileOrImage(ListItemType type) {
        ListItem listItem = createListItem(type, false, PARENT_ID, DATA);
        given(isParentArchivedService.isAnyOfParentsArchived(USER_ID, PARENT_ID)).willReturn(false);
        given(uuidConverter.convertEntity(DATA)).willReturn(STORED_FILE_ID);
        given(storageProxy.getFileMetadata(STORED_FILE_ID)).willReturn(StoredFileResponse.builder().fileUploaded(false).build());

        NotebookView result = underTest.create(listItem);

        assertThat(result)
            .returns(DATA, NotebookView::getValue)
            .returns(false, NotebookView::isEnabled)
            .returns(false, NotebookView::isArchived);

        then(uuidConverter).should().convertEntity(DATA);
        then(storageProxy).should().getFileMetadata(STORED_FILE_ID);
    }

    private ListItem createListItem(ListItemType type, boolean archived, UUID parentId, String data) {
        return ListItem.builder()
            .listItemId(LIST_ITEM_ID)
            .userId(USER_ID)
            .parent(parentId)
            .type(type)
            .title(TITLE)
            .pinned(true)
            .archived(archived)
            .data(data)
            .build();
    }
}