package com.github.saphyra.apphub.service.notebook.service.checklist_table.edition;

import com.github.saphyra.apphub.api.notebook.model.request.ChecklistTableRowRequest;
import com.github.saphyra.apphub.api.notebook.model.request.EditChecklistTableRequest;
import com.github.saphyra.apphub.api.notebook.model.request.EditTableRequest;
import com.github.saphyra.apphub.lib.common_domain.KeyValuePair;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
@RequiredArgsConstructor
class EditTableRequestConverter {
    public EditTableRequest convert(EditChecklistTableRequest request) {
        return EditTableRequest.builder()
            .title(request.getTitle())
            .columnNames(request.getColumnNames())
            .columns(mapRows(request.getRows()))
            .build();
    }

    private List<List<KeyValuePair<String>>> mapRows(List<ChecklistTableRowRequest<KeyValuePair<String>>> rows) {
        return rows.stream()
            .map(ChecklistTableRowRequest::getColumns)
            .collect(Collectors.toList());
    }
}
