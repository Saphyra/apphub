package com.github.saphyra.apphub.service.notebook.service.search;

import com.github.saphyra.apphub.api.notebook.model.response.NotebookView;
import com.github.saphyra.apphub.lib.error_report.ErrorReporterService;
import com.github.saphyra.apphub.service.notebook.dao.content.Content;
import com.github.saphyra.apphub.service.notebook.dao.content.ContentDao;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItem;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDao;
import com.github.saphyra.apphub.service.notebook.service.NotebookViewFactory;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class SearchServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String SEARCH_TEXT = "search-text";
    private static final UUID LIST_ITEM_ID_1 = UUID.randomUUID();
    private static final UUID LIST_ITEM_ID_2 = UUID.randomUUID();

    @Mock
    private ListItemDao listItemDao;

    @Mock
    private ContentDao contentDao;

    @Mock
    private NotebookViewFactory notebookViewFactory;

    @Mock
    private ErrorReporterService errorReporterService;

    @InjectMocks
    private SearchService underTest;

    @Mock
    private ListItem listItem1;

    @Mock
    private ListItem listItem2;

    @Mock
    private ListItem listItem3;

    @Mock
    private Content content1;

    @Mock
    private Content content2;

    @Mock
    private Content content3;

    @Mock
    private Content content4;

    @Mock
    private NotebookView notebookView1;

    @Mock
    private NotebookView notebookView2;

    @Test
    public void searchTextTooShort() {
        Throwable ex = catchThrowable(() -> underTest.search(USER_ID, "as"));

        ExceptionValidator.validateInvalidParam(ex, "search", "too short");
    }

    @Test
    public void search() {
        given(listItemDao.getByUserId(USER_ID)).willReturn(Arrays.asList(listItem1, listItem2));
        given(listItem1.getTitle()).willReturn("ASeArch-teXt1f");
        given(listItem2.getTitle()).willReturn("asd");

        given(contentDao.getByUserId(USER_ID)).willReturn(Arrays.asList(content1, content2, content3, content4));

        given(content1.getContent()).willReturn("ASeArch-teXt1f");
        given(content2.getContent()).willReturn("ASeArch-teXt1f");
        given(content3.getContent()).willReturn("ASeArch-teXt1f");
        given(content4.getContent()).willReturn("bg-teXt1f");

        given(content1.getListItemId()).willReturn(LIST_ITEM_ID_1);
        given(content2.getListItemId()).willReturn(LIST_ITEM_ID_2);

        given(listItemDao.findById(LIST_ITEM_ID_1)).willReturn(Optional.of(listItem3));

        given(notebookViewFactory.create(listItem1)).willReturn(notebookView1);
        given(notebookViewFactory.create(listItem3)).willReturn(notebookView2);

        List<NotebookView> result = underTest.search(USER_ID, SEARCH_TEXT);

        verify(errorReporterService, times(1)).report(anyString());
        assertThat(result).containsExactlyInAnyOrder(notebookView1, notebookView2);
    }
}