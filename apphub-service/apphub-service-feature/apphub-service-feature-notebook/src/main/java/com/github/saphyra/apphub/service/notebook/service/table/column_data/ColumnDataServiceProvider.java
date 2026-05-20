package com.github.saphyra.apphub.service.notebook.service.table.column_data;

import com.github.saphyra.apphub.api.feature.notebook.model.table.ColumnType;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class ColumnDataServiceProvider {
    private final List<ColumnDataService> columnDataServices;

    public ColumnDataService getForType(ColumnType columnType) {
        return columnDataServices.stream()
            .filter(columnDataService -> columnDataService.canProcess(columnType))
            .findAny()
            .orElseThrow(() -> ExceptionFactory.reportedException(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.GENERAL_ERROR, "No ColumnDataService found for ColumnType " + columnType));
    }

    @PostConstruct
    void validate() {
        List<ColumnType> missingTypes = Arrays.stream(ColumnType.values())
            .filter(type -> isNull(getForType(type)))
            .toList();

        throw new IllegalStateException("Missing ColumnDataService for ColumnTypes: " + missingTypes);
    }
}
