package com.github.saphyra.apphub.service.notebook.service.table.column_data;

import com.github.saphyra.apphub.api.notebook.model.table.ColumnType;
import com.github.saphyra.apphub.api.notebook.model.table.TableColumnModel;
import com.github.saphyra.apphub.service.notebook.dao.column_type.ColumnTypeDao;
import com.github.saphyra.apphub.service.notebook.dao.column_type.ColumnTypeDto;
import com.github.saphyra.apphub.service.notebook.dao.column_type.ColumnTypeFactory;
import com.github.saphyra.apphub.service.notebook.dao.dimension.Dimension;
import com.github.saphyra.apphub.service.notebook.dao.dimension.DimensionDao;
import com.github.saphyra.apphub.service.notebook.dao.dimension.DimensionFactory;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItem;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class EmptyColumnDataServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID LIST_ITEM_ID = UUID.randomUUID();
    private static final UUID ROW_ID = UUID.randomUUID();
    private static final Integer COLUMN_INDEX = 45;
    private static final UUID COLUMN_ID = UUID.randomUUID();

    @Mock
    private DimensionFactory dimensionFactory;

    @Mock
    private DimensionDao dimensionDao;

    @Mock
    private ColumnTypeFactory columnTypeFactory;

    @Mock
    private ColumnTypeDao columnTypeDao;

    @InjectMocks
    private EmptyColumnDataService underTest;

    @Mock
    private TableColumnModel model;

    @Mock
    private Dimension column;

    @Mock
    private ColumnTypeDto columnTypeDto;

    @Mock
    private ListItem listItem;

    @Mock
    private Dimension clonedColumn;

    @Test
    void canProcess() {
        assertThat(underTest.canProcess(ColumnType.EMPTY)).isTrue();
        assertThat(underTest.canProcess(ColumnType.FILE)).isFalse();
    }

    @Test
    void save() {
        given(model.getColumnIndex()).willReturn(COLUMN_INDEX);
        given(dimensionFactory.create(USER_ID, ROW_ID, COLUMN_INDEX)).willReturn(column);
        given(column.getDimensionId()).willReturn(COLUMN_ID);
        given(columnTypeFactory.create(COLUMN_ID, USER_ID, ColumnType.EMPTY)).willReturn(columnTypeDto);

        assertThat(underTest.save(USER_ID, LIST_ITEM_ID, ROW_ID, model)).isEmpty();

        then(dimensionDao).should().save(column);
        then(columnTypeDao).should().save(columnTypeDto);
    }

    @Test
    void getData() {
        assertThat(underTest.getData(COLUMN_ID)).isNull();
    }

    @Test
    void edit() {
        given(model.getColumnId()).willReturn(COLUMN_ID);
        given(model.getColumnIndex()).willReturn(COLUMN_INDEX);
        given(dimensionDao.findByIdValidated(COLUMN_ID)).willReturn(column);

        assertThat(underTest.edit(listItem, ROW_ID, model)).isEmpty();

        then(column).should().setIndex(COLUMN_INDEX);
        then(dimensionDao).should().save(column);
    }

    @Test
    void cloneColumn() {
        given(listItem.getUserId()).willReturn(USER_ID);
        given(column.getIndex()).willReturn(COLUMN_INDEX);
        given(dimensionFactory.create(USER_ID, ROW_ID, COLUMN_INDEX)).willReturn(clonedColumn);
        given(clonedColumn.getDimensionId()).willReturn(COLUMN_ID);
        given(columnTypeFactory.create(COLUMN_ID, USER_ID, ColumnType.EMPTY)).willReturn(columnTypeDto);

        underTest.clone(listItem, ROW_ID, column);

        then(dimensionDao).should().save(clonedColumn);
        then(columnTypeDao).should().save(columnTypeDto);
    }
}