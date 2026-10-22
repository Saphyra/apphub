package com.github.saphyra.apphub.service.notebook.service.table.validator;

import com.github.saphyra.apphub.api.feature.notebook.model.ListItemType;
import com.github.saphyra.apphub.api.feature.notebook.model.table.TableColumnModel;
import com.github.saphyra.apphub.api.feature.notebook.model.table.TableRowModel;
import com.github.saphyra.apphub.lib.common_util.ValidationUtil;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class TableCreationRowValidatorTest {
    @Mock
    private TableCreationColumnValidator tableCreationColumnValidator;

    @InjectMocks
    private TableCreationRowValidator underTest;

    @Mock
    private TableColumnModel columnModel;

    @Test
    void nullRows() {
        Throwable ex = catchThrowable(() -> underTest.validateRows(ListItemType.TABLE, null));

        ExceptionValidator.validateInvalidParam(ex, "rows", ValidationUtil.MUST_NOT_BE_NULL);
    }

    @Test
    void nullRowIndex() {
        TableRowModel rowModel = TableRowModel.builder()
            .rowIndex(null)
            .checked(null)
            .columns(List.of(columnModel))
            .build();

        Throwable ex = catchThrowable(() -> underTest.validateRows(ListItemType.TABLE, List.of(rowModel)));

        ExceptionValidator.validateInvalidParam(ex, "row.rowIndex", ValidationUtil.MUST_NOT_BE_NULL);
    }

    @Test
    void nullColumns() {
        TableRowModel rowModel = TableRowModel.builder()
            .rowIndex(23)
            .checked(null)
            .columns(null)
            .build();

        Throwable ex = catchThrowable(() -> underTest.validateRows(ListItemType.TABLE, List.of(rowModel)));

        ExceptionValidator.validateInvalidParam(ex, "row.columns", ValidationUtil.MUST_NOT_BE_NULL);
    }

    @Test
    void nullCheckedWhenChecklistTable() {
        TableRowModel rowModel = TableRowModel.builder()
            .rowIndex(234)
            .checked(null)
            .columns(List.of(columnModel))
            .build();

        Throwable ex = catchThrowable(() -> underTest.validateRows(ListItemType.CHECKLIST_TABLE, List.of(rowModel)));

        ExceptionValidator.validateInvalidParam(ex, "row.checked", ValidationUtil.MUST_NOT_BE_NULL);
    }

    @Test
    void valid() {
        TableRowModel rowModel = TableRowModel.builder()
            .rowIndex(234)
            .checked(true)
            .columns(List.of(columnModel))
            .build();

        underTest.validateRows(ListItemType.CHECKLIST_TABLE, List.of(rowModel));

        then(tableCreationColumnValidator).should().validateColumn(columnModel);
    }
}