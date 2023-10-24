package com.github.saphyra.apphub.service.notebook.service.checklist.create;

import com.github.saphyra.apphub.api.notebook.model.checklist.ChecklistItemModel;
import com.github.saphyra.apphub.service.notebook.dao.checked_item.CheckedItem;
import com.github.saphyra.apphub.service.notebook.dao.checked_item.CheckedItemDao;
import com.github.saphyra.apphub.service.notebook.dao.checked_item.CheckedItemFactory;
import com.github.saphyra.apphub.service.notebook.dao.content.Content;
import com.github.saphyra.apphub.service.notebook.dao.content.ContentDao;
import com.github.saphyra.apphub.service.notebook.dao.dimension.Dimension;
import com.github.saphyra.apphub.service.notebook.dao.dimension.DimensionDao;
import com.github.saphyra.apphub.service.notebook.dao.dimension.DimensionFactory;
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
class ChecklistItemCreationServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID LIST_ITEM_ID = UUID.randomUUID();
    private static final Integer INDEX = 32;
    private static final UUID DIMENSION_ID = UUID.randomUUID();
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
    private ChecklistItemCreationService underTest;

    @Mock
    private ChecklistItemModel model;

    @Mock
    private Dimension dimension;

    @Mock
    private CheckedItem checkedItem;

    @Mock
    private Content content;

    @Test
    void create() {
        given(model.getIndex()).willReturn(INDEX);
        given(model.getChecked()).willReturn(true);
        given(model.getContent()).willReturn(CONTENT);
        given(dimension.getDimensionId()).willReturn(DIMENSION_ID);

        given(dimensionFactory.create(USER_ID, LIST_ITEM_ID, INDEX)).willReturn(dimension);
        given(checkedItemFactory.create(USER_ID, DIMENSION_ID, true)).willReturn(checkedItem);
        given(contentFactory.create(LIST_ITEM_ID, DIMENSION_ID, USER_ID, CONTENT)).willReturn(content);

        underTest.create(USER_ID, LIST_ITEM_ID, List.of(model));

        then(dimensionDao).should().save(dimension);
        then(checkedItemDao).should().save(checkedItem);
        then(contentDao).should().save(content);
    }
}