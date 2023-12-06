package com.github.saphyra.apphub.service.notebook.service.table.column_data.base.content;

import com.github.saphyra.apphub.api.notebook.model.table.ColumnType;
import com.github.saphyra.apphub.service.notebook.dao.dimension.Dimension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class ContentBasedColumnProxyTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID LIST_ITEM_ID = UUID.randomUUID();
    private static final UUID ROW_ID = UUID.randomUUID();
    private static final UUID COLUMN_ID = UUID.randomUUID();
    private static final Integer COLUMN_INDEX = 234;
    private static final String CONTENT = "content";

    @Mock
    private ContentBasedColumnSaver contentBasedColumnSaver;

    @Mock
    private ContentBasedColumnEditer contentBasedColumnEditer;

    @Mock
    private ContentBasedColumnCloner contentBasedColumnCloner;

    @Mock
    private ContentBasedColumnDeleter contentBasedColumnDeleter;

    @InjectMocks
    private ContentBasedColumnProxy underTest;

    @Mock
    private Dimension column;

    @Test
    void save() {
        underTest.save(USER_ID, LIST_ITEM_ID, ROW_ID, COLUMN_INDEX, CONTENT, ColumnType.TEXT);

        then(contentBasedColumnSaver).should().save(USER_ID, LIST_ITEM_ID, ROW_ID, COLUMN_INDEX, CONTENT, ColumnType.TEXT);
    }

    @Test
    void delete() {
        underTest.delete(column);

        then(contentBasedColumnDeleter).should().delete(column);
    }

    @Test
    void edit() {
        underTest.edit(COLUMN_ID, COLUMN_INDEX, CONTENT);

        then(contentBasedColumnEditer).should().edit(COLUMN_ID, COLUMN_INDEX, CONTENT);
    }

    @Test
    void cloneColumn() {
        underTest.clone(USER_ID, LIST_ITEM_ID, ROW_ID, COLUMN_ID, COLUMN_INDEX, ColumnType.TEXT);

        then(contentBasedColumnCloner).should().clone(USER_ID, LIST_ITEM_ID, ROW_ID, COLUMN_ID, COLUMN_INDEX, ColumnType.TEXT);
    }
}