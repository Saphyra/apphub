package com.github.saphyra.apphub.service.notebook.service.checklist.query;

import com.github.saphyra.apphub.api.notebook.model.ItemType;
import com.github.saphyra.apphub.api.notebook.model.checklist.ChecklistItemModel;
import com.github.saphyra.apphub.service.notebook.dao.checked_item.CheckedItem;
import com.github.saphyra.apphub.service.notebook.dao.checked_item.CheckedItemDao;
import com.github.saphyra.apphub.service.notebook.dao.content.Content;
import com.github.saphyra.apphub.service.notebook.dao.content.ContentDao;
import com.github.saphyra.apphub.service.notebook.dao.dimension.Dimension;
import com.github.saphyra.apphub.service.notebook.dao.dimension.DimensionDao;
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
class ChecklistItemModelQueryServiceTest {
    private static final UUID LIST_ITEM_ID = UUID.randomUUID();
    private static final UUID CHECKLIST_ITEM_ID = UUID.randomUUID();
    private static final Integer INDEX = 324;
    private static final String CONTENT = "content";

    @Mock
    private DimensionDao dimensionDao;

    @Mock
    private CheckedItemDao checkedItemDao;

    @Mock
    private ContentDao contentDao;

    @InjectMocks
    private ChecklistItemModelQueryService underTest;

    @Mock
    private Dimension dimension;

    @Mock
    private CheckedItem checkedItem;

    @Mock
    private Content content;

    @Test
    void getItems() {
        given(dimensionDao.getByExternalReference(LIST_ITEM_ID)).willReturn(List.of(dimension));
        given(dimension.getDimensionId()).willReturn(CHECKLIST_ITEM_ID);
        given(checkedItemDao.findByIdValidated(CHECKLIST_ITEM_ID)).willReturn(checkedItem);
        given(contentDao.findByParentValidated(CHECKLIST_ITEM_ID)).willReturn(content);
        given(dimension.getIndex()).willReturn(INDEX);
        given(checkedItem.getChecked()).willReturn(true);
        given(content.getContent()).willReturn(CONTENT);

        List<ChecklistItemModel> result = underTest.getItems(LIST_ITEM_ID);

        assertThat(result).containsExactly(
            ChecklistItemModel.builder()
                .checklistItemId(CHECKLIST_ITEM_ID)
                .index(INDEX)
                .checked(true)
                .content(CONTENT)
                .type(ItemType.EXISTING)
                .build()
        );
    }
}