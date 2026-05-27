package com.github.saphyra.apphub.service.notebook.dao.list_item.list_item;

import com.github.saphyra.apphub.api.feature.notebook.model.ListItemType;
import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ListItemFactoryTest {
    private static final UUID LIST_ITEM_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID PARENT = UUID.randomUUID();
    private static final String TITLE = "title";
    private static final String DATA = "data";
    private static final ListItemType TYPE = ListItemType.TEXT;

    @Mock
    private IdGenerator idGenerator;

    @Mock
    private ListItem source;

    @InjectMocks
    private ListItemFactory underTest;

    @Test
    void create_withoutData() {
        given(idGenerator.randomUuid()).willReturn(LIST_ITEM_ID);

        ListItem result = underTest.create(USER_ID, PARENT, TITLE, TYPE);

        assertThat(result.getListItemId()).isEqualTo(LIST_ITEM_ID);
        assertThat(result.getUserId()).isEqualTo(USER_ID);
        assertThat(result.getParent()).isEqualTo(PARENT);
        assertThat(result.getType()).isEqualTo(TYPE);
        assertThat(result.getTitle()).isEqualTo(TITLE);
        assertThat(result.getData()).isNull();
        assertThat(result.isPinned()).isFalse();
        assertThat(result.isArchived()).isFalse();
    }

    @Test
    void create_withData() {
        given(idGenerator.randomUuid()).willReturn(LIST_ITEM_ID);

        ListItem result = underTest.create(USER_ID, PARENT, TITLE, TYPE, DATA);

        assertThat(result.getListItemId()).isEqualTo(LIST_ITEM_ID);
        assertThat(result.getUserId()).isEqualTo(USER_ID);
        assertThat(result.getParent()).isEqualTo(PARENT);
        assertThat(result.getType()).isEqualTo(TYPE);
        assertThat(result.getTitle()).isEqualTo(TITLE);
        assertThat(result.getData()).isEqualTo(DATA);
        assertThat(result.isPinned()).isFalse();
        assertThat(result.isArchived()).isFalse();
    }

    @Test
    void create_full() {
        given(idGenerator.randomUuid()).willReturn(LIST_ITEM_ID);

        ListItem result = underTest.create(USER_ID, PARENT, TITLE, TYPE, DATA, true, true);

        assertThat(result.getListItemId()).isEqualTo(LIST_ITEM_ID);
        assertThat(result.getUserId()).isEqualTo(USER_ID);
        assertThat(result.getParent()).isEqualTo(PARENT);
        assertThat(result.getType()).isEqualTo(TYPE);
        assertThat(result.getTitle()).isEqualTo(TITLE);
        assertThat(result.getData()).isEqualTo(DATA);
        assertThat(result.isPinned()).isTrue();
        assertThat(result.isArchived()).isTrue();
    }

    @Test
    void clone_withoutData() {
        given(idGenerator.randomUuid()).willReturn(LIST_ITEM_ID);
        given(source.getUserId()).willReturn(USER_ID);
        given(source.getTitle()).willReturn(TITLE);
        given(source.getType()).willReturn(TYPE);
        given(source.getData()).willReturn(DATA);
        given(source.isPinned()).willReturn(true);
        given(source.isArchived()).willReturn(true);

        ListItem result = underTest.clone(PARENT, source);

        assertThat(result.getListItemId()).isEqualTo(LIST_ITEM_ID);
        assertThat(result.getUserId()).isEqualTo(USER_ID);
        assertThat(result.getParent()).isEqualTo(PARENT);
        assertThat(result.getType()).isEqualTo(TYPE);
        assertThat(result.getTitle()).isEqualTo(TITLE);
        assertThat(result.getData()).isEqualTo(DATA);
        assertThat(result.isPinned()).isTrue();
        assertThat(result.isArchived()).isTrue();
    }

    @Test
    void clone_withData() {
        given(idGenerator.randomUuid()).willReturn(LIST_ITEM_ID);
        given(source.getUserId()).willReturn(USER_ID);
        given(source.getTitle()).willReturn(TITLE);
        given(source.getType()).willReturn(TYPE);
        given(source.isPinned()).willReturn(true);
        given(source.isArchived()).willReturn(true);

        ListItem result = underTest.clone(PARENT, source, "new-data");

        assertThat(result.getListItemId()).isEqualTo(LIST_ITEM_ID);
        assertThat(result.getUserId()).isEqualTo(USER_ID);
        assertThat(result.getParent()).isEqualTo(PARENT);
        assertThat(result.getType()).isEqualTo(TYPE);
        assertThat(result.getTitle()).isEqualTo(TITLE);
        assertThat(result.getData()).isEqualTo("new-data");
        assertThat(result.isPinned()).isTrue();
        assertThat(result.isArchived()).isTrue();
    }
}