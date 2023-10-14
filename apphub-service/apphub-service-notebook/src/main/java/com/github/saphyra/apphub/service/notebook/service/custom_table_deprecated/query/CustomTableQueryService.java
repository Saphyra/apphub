package com.github.saphyra.apphub.service.notebook.service.custom_table_deprecated.query;

import com.github.saphyra.apphub.api.notebook.model.response.CustomTableResponse;
import com.github.saphyra.apphub.api.notebook.model.response.TableResponse;
import com.github.saphyra.apphub.service.notebook.service.table_deprecated.query.TableQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class CustomTableQueryService {
    private final CustomTableColumnProvider customTableColumnProvider;
    private final TableQueryService tableQueryService;

    public CustomTableResponse getCustomTable(UUID listItemId) {
        TableResponse<Object> tableResponse = tableQueryService.getTable(listItemId, customTableColumnProvider);
        return CustomTableResponse.builder()
            .title(tableResponse.getTitle())
            .parent(tableResponse.getParent())
            .tableHeads(tableResponse.getTableHeads())
            .tableColumns(tableResponse.getTableColumns())
            .build();
    }
}
