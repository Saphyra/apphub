package com.github.saphyra.apphub.service.notebook.service.checklist;

import com.github.saphyra.apphub.api.feature.notebook.model.ItemType;
import com.github.saphyra.apphub.api.feature.notebook.model.checklist.ChecklistItemModel;
import com.github.saphyra.apphub.api.feature.notebook.model.checklist.ChecklistResponse;
import com.github.saphyra.apphub.lib.common_domain.TriWrapper;
import com.github.saphyra.apphub.service.notebook.dao.list_item.CommonListItemDao;
import com.github.saphyra.apphub.service.notebook.dao.list_item.checklist_item.ChecklistItem;
import com.github.saphyra.apphub.service.notebook.dao.list_item.content.Content;
import com.github.saphyra.apphub.service.notebook.dao.list_item.list_item.ListItem;
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

@ExtendWith(MockitoExtension.class)
class ChecklistQueryServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID LIST_ITEM_ID = UUID.randomUUID();
    private static final UUID PARENT = UUID.randomUUID();
    private static final String TITLE = "title";
    private static final UUID CHECKLIST_ITEM_ID = UUID.randomUUID();
    private static final String CONTENT = "content";
    private static final int INDEX = 2;
    private static final boolean CHECKED = true;

    @Mock
    private CommonListItemDao commonListItemDao;

    @InjectMocks
    private ChecklistQueryService underTest;

    @Mock
    private ListItem listItem;

    @Mock
    private ChecklistItem checklistItem;

    @Mock
    private Content content;

    @Test
    void getChecklistResponse() {
        TriWrapper<ListItem, List<ChecklistItem>, List<Content>> wrapper = new TriWrapper<>(
            listItem,
            List.of(checklistItem),
            List.of(content)
        );

        given(commonListItemDao.findChecklistValidated(USER_ID, LIST_ITEM_ID)).willReturn(wrapper);
        given(listItem.getTitle()).willReturn(TITLE);
        given(listItem.getParent()).willReturn(PARENT);
        given(content.getContent()).willReturn(Map.of(CHECKLIST_ITEM_ID, CONTENT));
        given(checklistItem.getChecklistItemId()).willReturn(CHECKLIST_ITEM_ID);
        given(checklistItem.getIndex()).willReturn(INDEX);
        given(checklistItem.isChecked()).willReturn(CHECKED);

        ChecklistResponse result = underTest.getChecklistResponse(USER_ID, LIST_ITEM_ID);

        assertThat(result.getTitle()).isEqualTo(TITLE);
        assertThat(result.getParent()).isEqualTo(PARENT);
        assertThat(result.getItems()).hasSize(1);

        ChecklistItemModel item = result.getItems().getFirst();
        assertThat(item.getChecklistItemId()).isEqualTo(CHECKLIST_ITEM_ID);
        assertThat(item.getIndex()).isEqualTo(INDEX);
        assertThat(item.getChecked()).isEqualTo(CHECKED);
        assertThat(item.getContent()).isEqualTo(CONTENT);
        assertThat(item.getType()).isEqualTo(ItemType.EXISTING);
    }
}