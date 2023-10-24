package com.github.saphyra.apphub.service.notebook.service.table.validator;

import com.github.saphyra.apphub.api.notebook.model.ItemType;
import com.github.saphyra.apphub.api.notebook.model.table.ColumnType;
import com.github.saphyra.apphub.api.notebook.model.table.TableColumnModel;
import com.github.saphyra.apphub.service.notebook.dao.dimension.Dimension;
import com.github.saphyra.apphub.service.notebook.dao.dimension.DimensionDao;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class EditTableColumnValidatorTest {
    private static final Object DATA = "data";
    private static final UUID COLUMN_ID = UUID.randomUUID();
    private static final UUID ROW_ID = UUID.randomUUID();

    @Mock
    private DimensionDao dimensionDao;

    @Mock
    private TableColumnDataValidator tableColumnDataValidator;

    @InjectMocks
    private EditTableColumnValidator underTest;

    @Mock
    private Dimension column;

    @Test
    void nullColumns() {
        Throwable ex = catchThrowable(() -> underTest.validateColumns(null, ItemType.NEW, null));

        ExceptionValidator.validateInvalidParam(ex, "row.columns", "must not be null");
    }

    @Test
    void nullColumnIndex() {
        TableColumnModel model = TableColumnModel.builder()
            .columnIndex(null)
            .build();

        Throwable ex = catchThrowable(() -> underTest.validateColumns(null, ItemType.NEW, List.of(model)));

        ExceptionValidator.validateInvalidParam(ex, "row.column.columnIndex", "must not be null");
    }

    @Test
    void nullColumnType() {
        TableColumnModel model = TableColumnModel.builder()
            .columnIndex(234)
            .columnType(null)
            .build();

        Throwable ex = catchThrowable(() -> underTest.validateColumns(null, ItemType.NEW, List.of(model)));

        ExceptionValidator.validateInvalidParam(ex, "row.column.columnType", "must not be null");
    }

    @Test
    void nullItemType() {
        TableColumnModel model = TableColumnModel.builder()
            .columnIndex(234)
            .columnType(ColumnType.TEXT)
            .itemType(null)
            .data(DATA)
            .build();

        Throwable ex = catchThrowable(() -> underTest.validateColumns(null, ItemType.NEW, List.of(model)));

        ExceptionValidator.validateInvalidParam(ex, "row.column.itemType", "must not be null");

        then(tableColumnDataValidator).should().validate(ColumnType.TEXT, DATA);
    }

    @Test
    void itemTypeDoesNotMatchesRowItemType() {
        TableColumnModel model = TableColumnModel.builder()
            .columnIndex(234)
            .columnType(ColumnType.TEXT)
            .itemType(ItemType.EXISTING)
            .data(DATA)
            .build();

        Throwable ex = catchThrowable(() -> underTest.validateColumns(null, ItemType.NEW, List.of(model)));

        ExceptionValidator.validateInvalidParam(ex, "row.column.itemType", "must be " + ItemType.NEW);

        then(tableColumnDataValidator).should().validate(ColumnType.TEXT, DATA);
    }

    @Test
    void existingColumnWithInvalidColumnId() {
        TableColumnModel model = TableColumnModel.builder()
            .columnId(COLUMN_ID)
            .columnIndex(234)
            .columnType(ColumnType.TEXT)
            .itemType(ItemType.EXISTING)
            .data(DATA)
            .build();

        Throwable ex = catchThrowable(() -> underTest.validateColumns(null, ItemType.EXISTING, List.of(model)));

        ExceptionValidator.validateInvalidParam(ex, "row.column.columnId", "points to different table");

        then(tableColumnDataValidator).should().validate(ColumnType.TEXT, DATA);
        then(dimensionDao).should().findByIdValidated(COLUMN_ID);
    }

    @Test
    void valid() {
        TableColumnModel model = TableColumnModel.builder()
            .columnId(COLUMN_ID)
            .columnIndex(234)
            .columnType(ColumnType.TEXT)
            .itemType(ItemType.EXISTING)
            .data(DATA)
            .build();

        given(dimensionDao.getByExternalReference(ROW_ID)).willReturn(List.of(column));
        given(column.getDimensionId()).willReturn(COLUMN_ID);

        underTest.validateColumns(ROW_ID, ItemType.EXISTING, List.of(model));

        then(tableColumnDataValidator).should().validate(ColumnType.TEXT, DATA);
    }
}