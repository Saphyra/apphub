package com.github.saphyra.apphub.service.notebook.service;

import com.github.saphyra.apphub.api.feature.notebook.model.ListItemType;
import com.github.saphyra.apphub.api.feature.notebook.model.response.NotebookView;
import com.github.saphyra.apphub.service.notebook.dao.list_item.content.Content;
import com.github.saphyra.apphub.service.notebook.dao.list_item.content.ContentDao;
import com.github.saphyra.apphub.service.notebook.dao.list_item.list_item.ListItem;
import com.github.saphyra.apphub.service.notebook.dao.list_item.list_item.ListItemDao;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class SearchServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID LIST_ITEM_ID = UUID.randomUUID();
    private static final UUID CONTENT_KEY = UUID.randomUUID();

    @Mock
    private ListItemDao listItemDao;

    @Mock
    private NotebookViewFactory notebookViewFactory;

    @Mock
    private ContentDao contentDao;

    @InjectMocks
    private SearchService underTest;

    @Mock
    private NotebookView notebookView;

    @Test
    void searchValueTooShort() {
        ExceptionValidator.validateInvalidParam(() -> underTest.search(USER_ID, "ab"), "search", "too short");
    }

    @Test
    void search_titleMatches() {
        ListItem listItem = createListItem("AbCdEf", null);
        given(listItemDao.getByUserId(USER_ID)).willReturn(List.of(listItem));
        given(notebookViewFactory.create(listItem)).willReturn(notebookView);

        List<NotebookView> result = underTest.search(USER_ID, "cde");

        assertThat(result).containsExactly(notebookView);
        then(contentDao).shouldHaveNoInteractions();
    }

    @Test
    void search_dataMatches() {
        ListItem listItem = createListItem("title", "AaBbCc");
        given(listItemDao.getByUserId(USER_ID)).willReturn(List.of(listItem));
        given(notebookViewFactory.create(listItem)).willReturn(notebookView);

        List<NotebookView> result = underTest.search(USER_ID, "bbc");

        assertThat(result).containsExactly(notebookView);
        then(contentDao).shouldHaveNoInteractions();
    }

    @Test
    void search_contentMatches() {
        ListItem listItem = createListItem("title", "data");
        Content content = Content.builder()
            .listItemId(LIST_ITEM_ID)
            .batchIndex(0)
            .content(Map.of(CONTENT_KEY, "AbCdEf"))
            .build();
        given(listItemDao.getByUserId(USER_ID)).willReturn(List.of(listItem));
        given(contentDao.getByListItemId(LIST_ITEM_ID)).willReturn(List.of(content));
        given(notebookViewFactory.create(listItem)).willReturn(notebookView);

        List<NotebookView> result = underTest.search(USER_ID, "cde");

        assertThat(result).containsExactly(notebookView);
        then(contentDao).should().getByListItemId(LIST_ITEM_ID);
    }

    @Test
    void search_noMatch() {
        ListItem listItem = createListItem("title", "data");
        Content content = Content.builder()
            .listItemId(LIST_ITEM_ID)
            .batchIndex(0)
            .content(Map.of(CONTENT_KEY, "other"))
            .build();
        given(listItemDao.getByUserId(USER_ID)).willReturn(List.of(listItem));
        given(contentDao.getByListItemId(LIST_ITEM_ID)).willReturn(List.of(content));

        List<NotebookView> result = underTest.search(USER_ID, "match");

        assertThat(result).isEmpty();
        then(notebookViewFactory).shouldHaveNoInteractions();
    }

    private ListItem createListItem(String title, String data) {
        return ListItem.builder()
            .listItemId(LIST_ITEM_ID)
            .userId(USER_ID)
            .parent(null)
            .type(ListItemType.TEXT)
            .title(title)
            .pinned(false)
            .archived(false)
            .data(data)
            .build();
    }
}