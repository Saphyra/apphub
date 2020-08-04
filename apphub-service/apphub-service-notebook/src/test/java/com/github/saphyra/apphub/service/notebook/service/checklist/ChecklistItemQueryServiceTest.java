package com.github.saphyra.apphub.service.notebook.service.checklist;

import com.github.saphyra.apphub.api.notebook.model.response.ChecklistResponse;
import com.github.saphyra.apphub.service.notebook.dao.checklist_item.ChecklistItem;
import com.github.saphyra.apphub.service.notebook.dao.checklist_item.ChecklistItemDao;
import com.github.saphyra.apphub.service.notebook.dao.content.Content;
import com.github.saphyra.apphub.service.notebook.dao.content.ContentDao;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItem;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDao;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class ChecklistItemQueryServiceTest {
    private static final UUID LIST_ITEM_ID = UUID.randomUUID();
    private static final UUID CHECKLIST_ITEM_ID = UUID.randomUUID();
    private static final String CONTENT = "content";
    private static final Integer ORDER = 23245;
    private static final String TITLE = "title";

    @Mock
    private ListItemDao listItemDao;

    @Mock
    private ChecklistItemDao checklistItemDao;

    @Mock
    private ContentDao contentDao;

    @InjectMocks
    private ChecklistItemQueryService underTest;

    @Mock
    private ListItem listItem;

    @Mock
    private ChecklistItem checklistItem;

    @Mock
    private Content content;

    @Test
    public void query() {
        given(listItemDao.findByIdValidated(LIST_ITEM_ID)).willReturn(listItem);
        given(checklistItemDao.getByParent(LIST_ITEM_ID)).willReturn(Arrays.asList(checklistItem));
        given(checklistItem.getChecklistItemId()).willReturn(CHECKLIST_ITEM_ID);
        given(contentDao.findByParentValidated(CHECKLIST_ITEM_ID)).willReturn(content);
        given(content.getContent()).willReturn(CONTENT);
        given(checklistItem.getChecked()).willReturn(true);
        given(checklistItem.getOrder()).willReturn(ORDER);
        given(listItem.getTitle()).willReturn(TITLE);

        ChecklistResponse result = underTest.query(LIST_ITEM_ID);

        assertThat(result.getTitle()).isEqualTo(TITLE);
        assertThat(result.getNodes()).hasSize(1);
        assertThat(result.getNodes().get(0).getChecklistItemId()).isEqualTo(CHECKLIST_ITEM_ID);
        assertThat(result.getNodes().get(0).getContent()).isEqualTo(CONTENT);
        assertThat(result.getNodes().get(0).getChecked()).isTrue();
        assertThat(result.getNodes().get(0).getOrder()).isEqualTo(ORDER);
    }
}