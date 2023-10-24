package com.github.saphyra.apphub.service.notebook.service.checklist.query;

import com.github.saphyra.apphub.api.notebook.model.checklist.ChecklistItemModel;
import com.github.saphyra.apphub.api.notebook.model.checklist.ChecklistResponse;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItem;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ChecklistResponseFactoryTest {
    private static final String TITLE = "title";
    private static final UUID PARENT = UUID.randomUUID();
    private static final UUID LIST_ITEM_ID = UUID.randomUUID();

    @Mock
    private ChecklistItemModelQueryService checklistItemModelQueryService;

    @InjectMocks
    private ChecklistResponseFactory underTest;

    @Mock
    private ListItem listItem;

    @Mock
    private ChecklistItemModel model;

    @Test
    void create() {
        given(listItem.getTitle()).willReturn(TITLE);
        given(listItem.getParent()).willReturn(PARENT);
        given(listItem.getListItemId()).willReturn(LIST_ITEM_ID);
        given(checklistItemModelQueryService.getItems(LIST_ITEM_ID)).willReturn(List.of(model));

        ChecklistResponse result = underTest.create(listItem);

        assertThat(result.getTitle()).isEqualTo(TITLE);
        assertThat(result.getParent()).isEqualTo(PARENT);
        assertThat(result.getItems()).containsExactly(model);
    }
}