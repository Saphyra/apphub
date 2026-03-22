package com.github.saphyra.apphub.service.notebook.service.clone;

import com.github.saphyra.apphub.service.notebook.dao.checked_item.CheckedItem;
import com.github.saphyra.apphub.service.notebook.dao.checked_item.CheckedItemDao;
import com.github.saphyra.apphub.service.notebook.dao.checked_item.CheckedItemFactory;
import com.github.saphyra.apphub.service.notebook.dao.content.Content;
import com.github.saphyra.apphub.service.notebook.dao.content.ContentDao;
import com.github.saphyra.apphub.service.notebook.dao.dimension.Dimension;
import com.github.saphyra.apphub.service.notebook.dao.dimension.DimensionDao;
import com.github.saphyra.apphub.service.notebook.dao.dimension.DimensionFactory;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItem;
import com.github.saphyra.apphub.service.notebook.service.ContentFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class ChecklistCloneServiceTest {
    private static final UUID ORIGINAL_LIST_ITEM_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID CLONED_LIST_ITEM_ID = UUID.randomUUID();
    private static final Integer INDEX = 432;
    private static final UUID ORIGINAL_ROW_ID = UUID.randomUUID();
    private static final UUID CLONED_ROW_ID = UUID.randomUUID();
    private static final String CONTENT = "content";

    @Mock
    private DimensionDao dimensionDao;

    @Mock
    private DimensionFactory dimensionFactory;

    @Mock
    private CheckedItemDao checkedItemDao;

    @Mock
    private CheckedItemFactory checkedItemFactory;

    @Mock
    private ContentDao contentDao;

    @Mock
    private ContentFactory contentFactory;

    @InjectMocks
    private ChecklistCloneService underTest;

    @Mock
    private ListItem originalListItem;

    @Mock
    private ListItem listItemClone;

    @Mock
    private Dimension originalRow;

    @Mock
    private Dimension rowClone;

    @Mock
    private CheckedItem originalCheckedItem;

    @Mock
    private CheckedItem checkedItemClone;

    @Mock
    private Content originalContent;

    @Mock
    private Content contentClone;

    @Test
    void cloneListItem() {
        given(originalListItem.getListItemId()).willReturn(ORIGINAL_LIST_ITEM_ID);
        given(dimensionDao.getByExternalReference(ORIGINAL_LIST_ITEM_ID)).willReturn(List.of(originalRow));
        given(listItemClone.getUserId()).willReturn(USER_ID);
        given(listItemClone.getListItemId()).willReturn(CLONED_LIST_ITEM_ID);
        given(originalRow.getIndex()).willReturn(INDEX);
        given(dimensionFactory.create(USER_ID, CLONED_LIST_ITEM_ID, INDEX)).willReturn(rowClone);
        given(originalRow.getDimensionId()).willReturn(ORIGINAL_ROW_ID);
        given(checkedItemDao.findByIdValidated(ORIGINAL_ROW_ID)).willReturn(originalCheckedItem);
        given(originalCheckedItem.getChecked()).willReturn(true);
        given(rowClone.getDimensionId()).willReturn(CLONED_ROW_ID);
        given(checkedItemFactory.create(USER_ID, CLONED_ROW_ID, true)).willReturn(checkedItemClone);
        given(contentDao.findByParentValidated(ORIGINAL_ROW_ID)).willReturn(originalContent);
        given(originalContent.getContent()).willReturn(CONTENT);
        given(contentFactory.create(CLONED_LIST_ITEM_ID, CLONED_ROW_ID, USER_ID, CONTENT)).willReturn(contentClone);

        underTest.clone(originalListItem, listItemClone);

        then(dimensionDao).should().save(rowClone);
        then(checkedItemDao).should().save(checkedItemClone);
        then(contentDao).should().save(contentClone);
    }
}