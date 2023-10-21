package com.github.saphyra.apphub.service.notebook.service.checklist.edit;

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

import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class EditChecklistRowUpdaterTest {
    private static final UUID CHECKLIST_ITEM_ID = UUID.randomUUID();
    private static final Integer INDEX = 312;
    private static final String CONTENT = "content";

    @Mock
    private DimensionDao dimensionDao;

    @Mock
    private CheckedItemDao checkedItemDao;

    @Mock
    private ContentDao contentDao;

    @InjectMocks
    private EditChecklistRowUpdater underTest;

    @Mock
    private ChecklistItemModel model;

    @Mock
    private Dimension dimension;

    @Mock
    private CheckedItem checkedItem;

    @Mock
    private Content content;

    @Test
    void updateExistingChecklistItem() {
        given(model.getChecklistItemId()).willReturn(CHECKLIST_ITEM_ID);
        given(model.getIndex()).willReturn(INDEX);
        given(model.getChecked()).willReturn(true);
        given(model.getContent()).willReturn("content");

        given(dimensionDao.findByIdValidated(CHECKLIST_ITEM_ID)).willReturn(dimension);
        given(checkedItemDao.findByIdValidated(CHECKLIST_ITEM_ID)).willReturn(checkedItem);
        given(contentDao.findByParentValidated(CHECKLIST_ITEM_ID)).willReturn(content);

        underTest.updateExistingChecklistItem(model);

        then(dimension).should().setIndex(INDEX);
        then(dimensionDao).should().save(dimension);

        then(checkedItem).should().setChecked(true);
        then(checkedItemDao).should().save(checkedItem);

        then(content).should().setContent(CONTENT);
        then(contentDao).should().save(content);
    }
}