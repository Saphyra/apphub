package com.github.saphyra.apphub.service.notebook.service.checklist_table.creation;

import com.github.saphyra.apphub.api.notebook.model.request.ChecklistTableRowRequest;
import com.github.saphyra.apphub.api.notebook.model.request.CreateChecklistTableRequest;
import com.github.saphyra.apphub.api.notebook.model.request.CreateTableRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
//TODO unit test
class CreateTableRequestConverter {
    CreateTableRequest convert(CreateChecklistTableRequest request) {
        return CreateTableRequest.builder()
            .title(request.getTitle())
            .parent(request.getParent())
            .columnNames(request.getColumnNames())
            .columns(mapRows(request.getRows()))
            .build();
    }

    private List<List<String>> mapRows(List<ChecklistTableRowRequest<String>> rows) {
        return rows.stream()
            .map(ChecklistTableRowRequest::getColumns)
            .collect(Collectors.toList());
    }
}
