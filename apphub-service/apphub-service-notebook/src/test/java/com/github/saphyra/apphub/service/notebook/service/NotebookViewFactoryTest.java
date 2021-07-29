package com.github.saphyra.apphub.service.notebook.service;

import com.github.saphyra.apphub.api.notebook.model.response.NotebookView;
import com.github.saphyra.apphub.service.notebook.dao.content.Content;
import com.github.saphyra.apphub.service.notebook.dao.content.ContentDao;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItem;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class NotebookViewFactoryTest {
    private static final UUID LIST_ITEM_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID PARENT = UUID.randomUUID();
    private static final String TITLE = "title";
    private static final String VALUE = "value";

    @Mock
    private ContentDao contentDao;

    @InjectMocks
    private NotebookViewFactory underTest;

    @Mock
    private Content content;

    @Test
    public void create() {
        ListItem listItem = ListItem.builder()
            .listItemId(LIST_ITEM_ID)
            .userId(USER_ID)
            .parent(PARENT)
            .type(ListItemType.CATEGORY)
            .title(TITLE)
            .pinned(true)
            .build();

        NotebookView result = underTest.create(listItem);

        assertThat(result.getId()).isEqualTo(LIST_ITEM_ID);
        assertThat(result.getTitle()).isEqualTo(TITLE);
        assertThat(result.getType()).isEqualTo(ListItemType.CATEGORY.name());
        assertThat(result.getValue()).isNull();
        assertThat(result.isPinned()).isTrue();
    }

    @Test
    public void fillValueForLink() {
        ListItem listItem = ListItem.builder()
            .listItemId(LIST_ITEM_ID)
            .userId(USER_ID)
            .parent(PARENT)
            .type(ListItemType.LINK)
            .title(TITLE)
            .pinned(true)
            .build();

        given(contentDao.findByParentValidated(LIST_ITEM_ID)).willReturn(content);
        given(content.getContent()).willReturn(VALUE);

        NotebookView result = underTest.create(listItem);

        assertThat(result.getId()).isEqualTo(LIST_ITEM_ID);
        assertThat(result.getTitle()).isEqualTo(TITLE);
        assertThat(result.getType()).isEqualTo(ListItemType.LINK.name());
        assertThat(result.getValue()).isEqualTo(VALUE);
        assertThat(result.isPinned()).isTrue();
    }
}