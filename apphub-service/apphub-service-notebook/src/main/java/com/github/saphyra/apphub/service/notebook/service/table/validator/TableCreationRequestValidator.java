package com.github.saphyra.apphub.service.notebook.service.table.validator;

import com.github.saphyra.apphub.api.notebook.model.ListItemType;
import com.github.saphyra.apphub.api.notebook.model.table.CreateTableRequest;
import com.github.saphyra.apphub.lib.common_util.ValidationUtil;
import com.github.saphyra.apphub.service.notebook.service.validator.ListItemRequestValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class TableCreationRequestValidator {
    private final ListItemRequestValidator listItemRequestValidator;
    private final TableCreationRowValidator tableCreationRowValidator;
    private final TableColumnTypeValidator tableColumnTypeValidator;
    private final CreateTableHeadValidator createTableHeadValidator;
    private final ColumnNumberAmountValidator columnNumberAmountValidator;

    public void validate(CreateTableRequest request) {
        listItemRequestValidator.validate(request.getTitle(), request.getParent());

        createTableHeadValidator.validateTableHeads(request.getTableHeads());

        ListItemType listItemType = request.getListItemType();
        ValidationUtil.notNull(listItemType, "listItemType");
        ValidationUtil.contains(listItemType, List.of(ListItemType.TABLE, ListItemType.CHECKLIST_TABLE, ListItemType.CUSTOM_TABLE), "listItemType");

        tableCreationRowValidator.validateRows(listItemType, request.getRows());
        columnNumberAmountValidator.validateColumnNumbersMatches(request.getTableHeads(), request.getRows());
        tableColumnTypeValidator.validateColumnType(request);
    }
}
