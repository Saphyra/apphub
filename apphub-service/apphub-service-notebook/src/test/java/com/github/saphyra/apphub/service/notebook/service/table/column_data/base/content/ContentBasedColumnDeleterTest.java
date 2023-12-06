package com.github.saphyra.apphub.service.notebook.service.table.column_data.base.content;

import com.github.saphyra.apphub.service.notebook.dao.column_type.ColumnTypeDao;
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
class ContentBasedColumnDeleterTest {
    private static final UUID COLUMN_ID = UUID.randomUUID();

    @Mock
    private DimensionDao dimensionDao;

    @Mock
    private ContentDao contentDao;

    @Mock
    private ColumnTypeDao columnTypeDao;

    @InjectMocks
    private ContentBasedColumnDeleter underTest;

    @Mock
    private Dimension column;

    @Test
    void delete() {
        given(column.getDimensionId()).willReturn(COLUMN_ID);

        underTest.delete(column);

        then(contentDao).should().deleteByParent(COLUMN_ID);
        then(columnTypeDao).should().deleteById(COLUMN_ID);
        then(dimensionDao).should().delete(column);
    }
}