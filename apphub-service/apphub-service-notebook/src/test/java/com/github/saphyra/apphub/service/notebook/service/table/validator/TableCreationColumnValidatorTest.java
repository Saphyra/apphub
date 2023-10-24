package com.github.saphyra.apphub.service.notebook.service.table.validator;

import com.github.saphyra.apphub.api.notebook.model.table.ColumnType;
import com.github.saphyra.apphub.api.notebook.model.table.TableColumnModel;
import com.github.saphyra.apphub.service.notebook.service.table.validator.TableColumnDataValidator;
import com.github.saphyra.apphub.service.notebook.service.table.validator.TableCreationColumnValidator;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class TableCreationColumnValidatorTest {
    private static final String DATA = "asd";

    @Mock
    private TableColumnDataValidator tableColumnDataValidator;

    @InjectMocks
    private TableCreationColumnValidator underTest;

    @Test
    void nullColumnIndex() {
        TableColumnModel model = TableColumnModel.builder()
            .columnIndex(null)
            .columnType(ColumnType.TEXT)
            .data("asd")
            .build();

        Throwable ex = catchThrowable(() -> underTest.validateColumn(model));

        ExceptionValidator.validateInvalidParam(ex, "row.column.columnIndex", "must not be null");
    }

    @Test
    void nullColumnType() {
        TableColumnModel model = TableColumnModel.builder()
            .columnIndex(324)
            .columnType(null)
            .data("asd")
            .build();

        Throwable ex = catchThrowable(() -> underTest.validateColumn(model));

        ExceptionValidator.validateInvalidParam(ex, "row.column.columnType", "must not be null");
    }

    @Test
    void valid() {
        TableColumnModel model = TableColumnModel.builder()
            .columnIndex(324)
            .columnType(ColumnType.TEXT)
            .data(DATA)
            .build();

        underTest.validateColumn(model);

        then(tableColumnDataValidator).should().validate(ColumnType.TEXT, DATA);
    }
}