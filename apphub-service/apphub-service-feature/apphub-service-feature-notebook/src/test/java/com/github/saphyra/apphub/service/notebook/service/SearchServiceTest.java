package com.github.saphyra.apphub.service.notebook.service;

import com.github.saphyra.apphub.api.feature.notebook.model.ListItemType;
import com.github.saphyra.apphub.api.feature.notebook.model.response.NotebookView;
import com.github.saphyra.apphub.lib.common_domain.AccessToken;
import com.github.saphyra.apphub.lib.concurrency.ExecutorServiceBean;
import com.github.saphyra.apphub.lib.concurrency.ExecutorServiceBeenTestUtils;
import com.github.saphyra.apphub.lib.error_report.ErrorReporterService;
import com.github.saphyra.apphub.lib.security.access_token.AccessTokenProvider;
import com.github.saphyra.apphub.service.notebook.dao.list_item.content.Content;
import com.github.saphyra.apphub.service.notebook.dao.list_item.content.ContentDao;
import com.github.saphyra.apphub.service.notebook.dao.list_item.list_item.ListItem;
import com.github.saphyra.apphub.service.notebook.dao.list_item.list_item.ListItemDao;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

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

    @Spy
    private final ExecutorServiceBean executorServiceBean = ExecutorServiceBeenTestUtils.create(mock(ErrorReporterService.class));

    @Mock
    private AccessTokenProvider accessTokenProvider;

    @InjectMocks
    private SearchService underTest;

    @Mock
    private NotebookView notebookView;

    @Test
    void searchValueTooShort() {
        ExceptionValidator.validateInvalidParam(() -> underTest.search(USER_ID, "ab"), "search", "too short");
    }

    @Test
    void search_titleMatches() throws Exception {
        ListItem listItem = createListItem("AbCdEf", null);
        given(listItemDao.getByUserId(USER_ID)).willReturn(List.of(listItem));
        given(notebookViewFactory.create(List.of(listItem))).willReturn(List.of(notebookView));
        given(accessTokenProvider.set(AccessToken.builder().userId(USER_ID).build())).willReturn(accessTokenProvider);

        List<NotebookView> result = underTest.search(USER_ID, "cde");

        assertThat(result).containsExactly(notebookView);
        then(contentDao).shouldHaveNoInteractions();
        then(accessTokenProvider).should().close();
    }

    @Test
    void search_dataMatches() throws Exception {
        ListItem listItem = createListItem("title", "AaBbCc");
        given(listItemDao.getByUserId(USER_ID)).willReturn(List.of(listItem));
        given(notebookViewFactory.create(List.of(listItem))).willReturn(List.of(notebookView));
        given(accessTokenProvider.set(AccessToken.builder().userId(USER_ID).build())).willReturn(accessTokenProvider);

        List<NotebookView> result = underTest.search(USER_ID, "bbc");

        assertThat(result).containsExactly(notebookView);
        then(contentDao).shouldHaveNoInteractions();
        then(accessTokenProvider).should().close();
    }

    @Test
    void search_contentMatches() throws Exception {
        ListItem listItem = createListItem("title", "data");
        Content content = Content.builder()
            .listItemId(LIST_ITEM_ID)
            .batchIndex(0)
            .content(Map.of(CONTENT_KEY, "AbCdEf"))
            .build();
        given(listItemDao.getByUserId(USER_ID)).willReturn(List.of(listItem));
        given(contentDao.getByListItemId(LIST_ITEM_ID)).willReturn(List.of(content));
        given(notebookViewFactory.create(List.of(listItem))).willReturn(List.of(notebookView));
        given(accessTokenProvider.set(AccessToken.builder().userId(USER_ID).build())).willReturn(accessTokenProvider);

        List<NotebookView> result = underTest.search(USER_ID, "cde");

        assertThat(result).containsExactly(notebookView);
        then(contentDao).should().getByListItemId(LIST_ITEM_ID);
        then(accessTokenProvider).should().close();
    }

    @Test
    void search_noMatch() throws Exception {
        ListItem listItem = createListItem("title", "data");
        Content content = Content.builder()
            .listItemId(LIST_ITEM_ID)
            .batchIndex(0)
            .content(Map.of(CONTENT_KEY, "other"))
            .build();
        given(listItemDao.getByUserId(USER_ID)).willReturn(List.of(listItem));
        given(contentDao.getByListItemId(LIST_ITEM_ID)).willReturn(List.of(content));
        given(accessTokenProvider.set(AccessToken.builder().userId(USER_ID).build())).willReturn(accessTokenProvider);

        List<NotebookView> result = underTest.search(USER_ID, "match");

        assertThat(result).isEmpty();
        then(notebookViewFactory).should().create(List.of());
        then(accessTokenProvider).should().close();
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