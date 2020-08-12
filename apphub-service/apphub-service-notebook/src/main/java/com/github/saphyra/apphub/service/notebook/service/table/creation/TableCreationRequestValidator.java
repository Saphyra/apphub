package com.github.saphyra.apphub.service.notebook.service.table.creation;

import com.github.saphyra.apphub.api.notebook.model.request.CreateTableRequest;
import com.github.saphyra.apphub.service.notebook.service.ListItemRequestValidator;
import com.github.saphyra.apphub.service.notebook.service.table.ColumnNameValidator;
import com.github.saphyra.apphub.service.notebook.service.table.RowValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class TableCreationRequestValidator {
    private final ColumnNameValidator columnNameValidator;
    private final ListItemRequestValidator listItemRequestValidator;
    private final RowValidator rowValidator;

    public void validate(CreateTableRequest request) {
        listItemRequestValidator.validate(request.getTitle(), request.getParent());

        request.getColumnNames()
            .forEach(columnNameValidator::validate);

        request.getColumns()
            .forEach(columnValues -> rowValidator.validate(columnValues, request.getColumnNames().size()));
    }
}
