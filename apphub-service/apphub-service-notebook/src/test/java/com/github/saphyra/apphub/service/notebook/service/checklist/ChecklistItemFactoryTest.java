package com.github.saphyra.apphub.service.notebook.service.checklist;

import com.github.saphyra.apphub.api.notebook.model.request.ChecklistItemNodeRequest;
import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.service.notebook.dao.checklist_item.ChecklistItem;
import com.github.saphyra.apphub.service.notebook.dao.content.Content;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItem;
import com.github.saphyra.apphub.service.notebook.service.ContentFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class ChecklistItemFactoryTest {
    private static final Integer ORDER = 143251243;
    private static final String CONTENT = "content";
    private static final UUID CHECKLIST_ITEM_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID LIST_ITEM_ID = UUID.randomUUID();

    @Mock
    private ContentFactory contentFactory;

    @Mock
    private IdGenerator idGenerator;

    @InjectMocks
    private ChecklistItemFactory underTest;

    @Mock
    private ListItem listItem;

    @Mock
    private Content content;

    @Test
    public void create() {
        given(idGenerator.randomUuid()).willReturn(CHECKLIST_ITEM_ID);
        given(listItem.getUserId()).willReturn(USER_ID);
        given(listItem.getListItemId()).willReturn(LIST_ITEM_ID);
        given(contentFactory.create(LIST_ITEM_ID, CHECKLIST_ITEM_ID, USER_ID, CONTENT)).willReturn(content);

        ChecklistItemNodeRequest nodeRequest = ChecklistItemNodeRequest.builder()
            .checked(true)
            .order(ORDER)
            .content(CONTENT)
            .build();

        BiWrapper<ChecklistItem, Content> result = underTest.create(listItem, nodeRequest);

        assertThat(result.getEntity1().getChecklistItemId()).isEqualTo(CHECKLIST_ITEM_ID);
        assertThat(result.getEntity1().getUserId()).isEqualTo(USER_ID);
        assertThat(result.getEntity1().getParent()).isEqualTo(LIST_ITEM_ID);
        assertThat(result.getEntity1().getOrder()).isEqualTo(ORDER);
        assertThat(result.getEntity1().getChecked()).isEqualTo(true);
        assertThat(result.getEntity2()).isEqualTo(content);
    }
}