package com.github.saphyra.apphub.service.notebook.service.table.validator;

import com.github.saphyra.apphub.api.notebook.model.table.TableColumnModel;
import com.github.saphyra.apphub.api.notebook.model.table.TableHeadModel;
import com.github.saphyra.apphub.api.notebook.model.table.TableRowModel;
import com.github.saphyra.apphub.service.notebook.service.table.validator.ColumnNumberAmountValidator;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ColumnNumberAmountValidatorTest {
    @InjectMocks
    private ColumnNumberAmountValidator underTest;

    @Mock
    private TableHeadModel tableHeadModel;

    @Mock
    private TableRowModel tableRowModel;

    @Mock
    private TableColumnModel tableColumnModel;

    @Test
    void columnNumberMatches(){
        given(tableRowModel.getColumns()).willReturn(List.of(tableColumnModel));

        underTest.validateColumnNumbersMatches(List.of(tableHeadModel), List.of(tableRowModel));
    }

    @Test
    void columnNumberDoesNotMatch(){
        given(tableRowModel.getColumns()).willReturn(Collections.emptyList());

        Throwable ex = catchThrowable(() -> underTest.validateColumnNumbersMatches(List.of(tableHeadModel), List.of(tableRowModel)));

        ExceptionValidator.validateInvalidParam(ex, "row.columns", "item count mismatch");
    }
}