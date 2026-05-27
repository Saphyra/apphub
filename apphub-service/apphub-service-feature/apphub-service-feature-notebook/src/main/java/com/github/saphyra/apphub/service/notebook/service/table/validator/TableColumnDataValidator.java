package com.github.saphyra.apphub.service.notebook.service.table.validator;

import com.github.saphyra.apphub.api.feature.notebook.model.table.ColumnType;
import com.github.saphyra.apphub.service.notebook.service.table.column_data.ColumnDataServiceProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class TableColumnDataValidator {
    private final ColumnDataServiceProvider columnDataServiceProvider;

    public void validate(ColumnType columnType, Object data) {
        columnDataServiceProvider.getForType(columnType)
            .validateData(data);
    }
}
