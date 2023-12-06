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
class ContentBasedColumnSaverTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID LIST_ITEM_ID = UUID.randomUUID();
    private static final UUID ROW_ID = UUID.randomUUID();
    private static final UUID COLUMN_ID = UUID.randomUUID();
    private static final Integer COLUMN_INDEX = 2453;
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
    private ContentBasedColumnSaver underTest;

    @Mock
    private Dimension column;

    @Mock
    private Content content;

    @Mock
    private ColumnTypeDto columnTypeDto;

    @Test
    void save() {
        given(dimensionFactory.create(USER_ID, ROW_ID, COLUMN_INDEX)).willReturn(column);
        given(column.getDimensionId()).willReturn(COLUMN_ID);
        given(contentFactory.create(LIST_ITEM_ID, COLUMN_ID, USER_ID, CONTENT)).willReturn(content);
        given(columnTypeFactory.create(COLUMN_ID, USER_ID, ColumnType.TEXT)).willReturn(columnTypeDto);

        underTest.save(USER_ID, LIST_ITEM_ID, ROW_ID, COLUMN_INDEX, CONTENT, ColumnType.TEXT);

        then(dimensionDao).should().save(column);
        then(contentDao).should().save(content);
        then(columnTypeDao).should().save(columnTypeDto);
    }
}