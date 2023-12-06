package com.github.saphyra.apphub.service.notebook.service.table.validator;

import com.github.saphyra.apphub.api.notebook.model.table.ColumnType;
import com.github.saphyra.apphub.service.notebook.service.table.column_data.base.ColumnDataService;
import com.github.saphyra.apphub.service.notebook.service.table.column_data.base.ColumnDataServiceFetcher;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class TableColumnDataValidatorTest {
    private static final Object DATA = "data";

    @Mock
    private ColumnDataServiceFetcher columnDataServiceFetcher;

    @InjectMocks
    private TableColumnDataValidator underTest;

    @Mock
    private ColumnDataService columnDataService;

    @Test
    void validate() {
        given(columnDataServiceFetcher.findColumnDataService(ColumnType.CHECKBOX)).willReturn(columnDataService);

        underTest.validate(ColumnType.CHECKBOX, DATA);

        then(columnDataService).should().validateData(DATA);
    }
}