package com.github.saphyra.apphub.service.notebook.service.table.validator;

import com.github.saphyra.apphub.api.notebook.model.table.EditTableRequest;
import com.github.saphyra.apphub.service.notebook.service.validator.TitleValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class EditTableRequestValidator {
    private final TitleValidator titleValidator;
    private final EditTableHeadValidator editTableHeadValidator;
    private final EditTableRowValidator editTableRowValidator;
    private final ColumnNumberAmountValidator columnNumberAmountValidator;

    public void validate(UUID listItemId, EditTableRequest request) {
        titleValidator.validate(request.getTitle());

        editTableHeadValidator.validateTableHeads(listItemId, request.getTableHeads());
        columnNumberAmountValidator.validateColumnNumbersMatches(request.getTableHeads(), request.getRows());
        editTableRowValidator.validateTableRows(listItemId, request.getRows());
    }
}
