package com.github.saphyra.apphub.service.notebook.service.table.deprecated_column_data.base;

import com.github.saphyra.apphub.api.feature.notebook.model.table.ColumnType;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
@Deprecated(forRemoval = true)
public class ColumnDataServiceFetcher {
    private final List<DeprecatedColumnDataService> columnDataServices;

    public DeprecatedColumnDataService findColumnDataService(ColumnType columnType) {
        return columnDataServices.stream()
                .filter(service -> service.canProcess(columnType))
                .findAny()
                .orElseThrow(() -> ExceptionFactory.reportedException("No ColumnDataService found for columnType " + columnType));
    }
}
