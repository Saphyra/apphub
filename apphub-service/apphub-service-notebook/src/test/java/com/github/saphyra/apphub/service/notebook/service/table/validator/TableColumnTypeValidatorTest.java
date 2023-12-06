package com.github.saphyra.apphub.service.notebook.service.table.validator;

import com.github.saphyra.apphub.api.notebook.model.ListItemType;
import com.github.saphyra.apphub.api.notebook.model.table.ColumnType;
import com.github.saphyra.apphub.api.notebook.model.table.CreateTableRequest;
import com.github.saphyra.apphub.api.notebook.model.table.TableColumnModel;
import com.github.saphyra.apphub.api.notebook.model.table.TableRowModel;
import com.github.saphyra.apphub.service.notebook.service.table.column_data.base.ColumnDataService;
import com.github.saphyra.apphub.service.notebook.service.table.column_data.base.ColumnDataServiceFetcher;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class TableColumnTypeValidatorTest {
    private static final Object DATA = "data";

    @Mock
    private ColumnDataServiceFetcher columnDataServiceFetcher;

    @InjectMocks
    private TableColumnTypeValidator underTest;

    @Mock
    private CreateTableRequest createTableRequest;

    @Mock
    private TableRowModel rowModel;

    @Mock
    private TableColumnModel columnModel;

    @Mock
    private ColumnDataService columnDataService;

    @Test
    void validateForTable_columnTypeNotText() {
        given(createTableRequest.getRows()).willReturn(List.of(rowModel));
        given(createTableRequest.getListItemType()).willReturn(ListItemType.TABLE);
        given(rowModel.getColumns()).willReturn(List.of(columnModel));
        given(columnModel.getColumnType()).willReturn(ColumnType.LINK);

        Throwable ex = catchThrowable(() -> underTest.validateColumnType(createTableRequest));

        ExceptionValidator.validateInvalidParam(ex, "row.column.columnType", "must be " + ColumnType.TEXT.name());
    }

    @Test
    void validateForTable_dataNull() {
        given(createTableRequest.getRows()).willReturn(List.of(rowModel));
        given(createTableRequest.getListItemType()).willReturn(ListItemType.TABLE);
        given(rowModel.getColumns()).willReturn(List.of(columnModel));
        given(columnModel.getColumnType()).willReturn(ColumnType.TEXT);

        Throwable ex = catchThrowable(() -> underTest.validateColumnType(createTableRequest));

        ExceptionValidator.validateInvalidParam(ex, "row.column.data", "must not be null");
    }

    @Test
    void validateForTable_valid() {
        given(createTableRequest.getRows()).willReturn(List.of(rowModel));
        given(createTableRequest.getListItemType()).willReturn(ListItemType.TABLE);
        given(rowModel.getColumns()).willReturn(List.of(columnModel));
        given(columnModel.getColumnType()).willReturn(ColumnType.TEXT);
        given(columnModel.getData()).willReturn("asd");

        underTest.validateColumnType(createTableRequest);
    }

    @Test
    void validateForChecklistTable_columnTypeNotText() {
        given(createTableRequest.getRows()).willReturn(List.of(rowModel));
        given(createTableRequest.getListItemType()).willReturn(ListItemType.CHECKLIST_TABLE);
        given(rowModel.getColumns()).willReturn(List.of(columnModel));
        given(columnModel.getColumnType()).willReturn(ColumnType.LINK);

        Throwable ex = catchThrowable(() -> underTest.validateColumnType(createTableRequest));

        ExceptionValidator.validateInvalidParam(ex, "row.column.columnType", "must be " + ColumnType.TEXT.name());
    }

    @Test
    void validateForChecklistTable_dataNull() {
        given(createTableRequest.getRows()).willReturn(List.of(rowModel));
        given(createTableRequest.getListItemType()).willReturn(ListItemType.CHECKLIST_TABLE);
        given(rowModel.getColumns()).willReturn(List.of(columnModel));
        given(columnModel.getColumnType()).willReturn(ColumnType.TEXT);

        Throwable ex = catchThrowable(() -> underTest.validateColumnType(createTableRequest));

        ExceptionValidator.validateInvalidParam(ex, "row.column.data", "must not be null");
    }

    @Test
    void validateForChecklistTable_valid() {
        given(createTableRequest.getRows()).willReturn(List.of(rowModel));
        given(createTableRequest.getListItemType()).willReturn(ListItemType.CHECKLIST_TABLE);
        given(rowModel.getColumns()).willReturn(List.of(columnModel));
        given(columnModel.getColumnType()).willReturn(ColumnType.TEXT);
        given(columnModel.getData()).willReturn("asd");

        underTest.validateColumnType(createTableRequest);
    }

    @Test
    void validateCustomTable() {
        given(createTableRequest.getRows()).willReturn(List.of(rowModel));
        given(createTableRequest.getListItemType()).willReturn(ListItemType.CUSTOM_TABLE);
        given(rowModel.getColumns()).willReturn(List.of(columnModel));
        given(columnModel.getColumnType()).willReturn(ColumnType.LINK);
        given(columnDataServiceFetcher.findColumnDataService(ColumnType.LINK)).willReturn(columnDataService);
        given(columnModel.getData()).willReturn(DATA);

        underTest.validateColumnType(createTableRequest);

        then(columnDataService).should().validateData(DATA);
    }
}