package com.github.saphyra.apphub.service.notebook.service.table.validator;

import com.github.saphyra.apphub.api.notebook.model.table.TableHeadModel;
import com.github.saphyra.apphub.api.notebook.model.table.TableRowModel;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
class ColumnNumberAmountValidator {
     void validateColumnNumbersMatches(List<TableHeadModel> tableHeads, List<TableRowModel> rows) {
        boolean hasMismatch = rows.stream()
            .anyMatch(tableRowModel -> tableRowModel.getColumns().size() != tableHeads.size());
        if (hasMismatch) {
            throw ExceptionFactory.invalidParam("row.columns", "item count mismatch");
        }
    }
}
