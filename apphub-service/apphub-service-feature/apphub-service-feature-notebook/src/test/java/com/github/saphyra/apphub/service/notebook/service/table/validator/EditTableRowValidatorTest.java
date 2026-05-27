package com.github.saphyra.apphub.service.notebook.service.table.validator;

import com.github.saphyra.apphub.api.feature.notebook.model.ItemType;
import com.github.saphyra.apphub.api.feature.notebook.model.table.TableColumnModel;
import com.github.saphyra.apphub.api.feature.notebook.model.table.TableRowModel;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class EditTableRowValidatorTest {
    private static final UUID ROW_ID = UUID.randomUUID();

    @Mock
    private EditTableColumnValidator editTableColumnValidator;

    @InjectMocks
    private EditTableRowValidator underTest;

    @Mock
    private TableColumnModel columnModel;

    @Test
    void nullRows() {
        Throwable ex = catchThrowable(() -> underTest.validateTableRows(null));

        ExceptionValidator.validateInvalidParam(ex, "rows", "must not be null");
    }

    @Test
    void nullRowIndex() {
        TableRowModel model = TableRowModel.builder()
            .rowIndex(null)
            .build();

        Throwable ex = catchThrowable(() -> underTest.validateTableRows(List.of(model)));

        ExceptionValidator.validateInvalidParam(ex, "row.rowIndex", "must not be null");
    }

    @Test
    void nullChecked() {
        TableRowModel model = TableRowModel.builder()
            .rowIndex(32)
            .checked(null)
            .build();

        Throwable ex = catchThrowable(() -> underTest.validateTableRows(List.of(model)));

        ExceptionValidator.validateInvalidParam(ex, "row.checked", "must not be null");
    }

    @Test
    void nullItemType() {
        TableRowModel model = TableRowModel.builder()
            .rowIndex(32)
            .checked(true)
            .itemType(null)
            .build();

        Throwable ex = catchThrowable(() -> underTest.validateTableRows(List.of(model)));

        ExceptionValidator.validateInvalidParam(ex, "row.itemType", "must not be null");
    }

    @Test
    void nullRowId() {
        TableRowModel model = TableRowModel.builder()
            .rowId(null)
            .rowIndex(32)
            .checked(true)
            .itemType(ItemType.EXISTING)
            .build();

        Throwable ex = catchThrowable(() -> underTest.validateTableRows(List.of(model)));

        ExceptionValidator.validateInvalidParam(ex, "row.rowId", "must not be null");
    }

    @Test
    void valid() {
        TableRowModel model = TableRowModel.builder()
            .rowId(ROW_ID)
            .rowIndex(32)
            .checked(true)
            .itemType(ItemType.EXISTING)
            .columns(List.of(columnModel))
            .build();

        underTest.validateTableRows(List.of(model));

        then(editTableColumnValidator).should().validateColumns(ItemType.EXISTING, List.of(columnModel));
    }
}