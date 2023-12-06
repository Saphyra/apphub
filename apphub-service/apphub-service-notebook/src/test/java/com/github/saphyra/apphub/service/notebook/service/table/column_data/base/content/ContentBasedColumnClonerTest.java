package com.github.saphyra.apphub.service.notebook.service.table.column_data.base.content;

import com.github.saphyra.apphub.api.notebook.model.table.ColumnType;
import com.github.saphyra.apphub.service.notebook.dao.column_type.ColumnTypeDao;
import com.github.saphyra.apphub.service.notebook.dao.column_type.ColumnTypeDto;
import com.github.saphyra.apphub.service.notebook.dao.column_type.ColumnTypeFactory;
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

import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class ContentBasedColumnClonerTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID CLONED_LIST_ITEM_ID = UUID.randomUUID();
    private static final UUID ORIGINAL_COLUMN_ID = UUID.randomUUID();
    private static final UUID ROW_ID = UUID.randomUUID();
    private static final Integer COLUMN_INDEX = 25;
    private static final UUID CLONED_COLUMN_ID = UUID.randomUUID();
    private static final String CONTENT = "content";

    @Mock
    private DimensionFactory dimensionFactory;

    @Mock
    private ContentFactory contentFactory;

    @Mock
    private ColumnTypeFactory columnTypeFactory;

    @Mock
    private DimensionDao dimensionDao;

    @Mock
    private ContentDao contentDao;

    @Mock
    private ColumnTypeDao columnTypeDao;

    @InjectMocks
    private ContentBasedColumnCloner underTest;

    @Mock
    private Dimension column;

    @Mock
    private ColumnTypeDto columnTypeDto;

    @Mock
    private Content originalContent;

    @Mock
    private Content clonedContent;

    @Test
    void cloneColumn() {
        given(dimensionFactory.create(USER_ID, ROW_ID, COLUMN_INDEX)).willReturn(column);
        given(column.getDimensionId()).willReturn(CLONED_COLUMN_ID);
        given(columnTypeFactory.create(CLONED_COLUMN_ID, USER_ID, ColumnType.TEXT)).willReturn(columnTypeDto);
        given(contentDao.findByParentValidated(ORIGINAL_COLUMN_ID)).willReturn(originalContent);
        given(originalContent.getContent()).willReturn(CONTENT);
        given(contentFactory.create(CLONED_LIST_ITEM_ID, CLONED_COLUMN_ID, USER_ID, CONTENT)).willReturn(clonedContent);

        underTest.clone(USER_ID, CLONED_LIST_ITEM_ID, ROW_ID, ORIGINAL_COLUMN_ID, COLUMN_INDEX, ColumnType.TEXT);

        then(dimensionDao).should().save(column);
        then(columnTypeDao).should().save(columnTypeDto);
        then(contentDao).should().save(clonedContent);
    }
}