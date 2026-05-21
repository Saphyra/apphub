package com.github.saphyra.apphub.service.notebook.service.table.column_data;

import com.github.saphyra.apphub.api.feature.notebook.model.table.ColumnType;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class ColumnDataServiceProvider {
    private final List<ColumnDataService> columnDataServices;

    public ColumnDataService getForType(ColumnType columnType) {
        return getOptionalForType(columnType)
            .orElseThrow(() -> new IllegalStateException("No ColumnDataService found for ColumnType " + columnType));
    }

    private Optional<ColumnDataService> getOptionalForType(ColumnType columnType) {
        return columnDataServices.stream()
            .filter(columnDataService -> columnDataService.canProcess(columnType))
            .findAny();
    }

    @PostConstruct
    void validate() {
        List<ColumnType> missingTypes = Arrays.stream(ColumnType.values())
            .filter(type -> getOptionalForType(type).isEmpty())
            .toList();

        if (!missingTypes.isEmpty()) {
            throw new IllegalStateException("Missing ColumnDataService for ColumnTypes: " + missingTypes);
        }
    }
}
