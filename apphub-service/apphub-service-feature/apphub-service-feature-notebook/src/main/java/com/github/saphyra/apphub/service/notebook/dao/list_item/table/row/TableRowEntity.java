package com.github.saphyra.apphub.service.notebook.dao.list_item.table.row;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Data
@Builder
class TableRowEntity {
    private String userId;
    private String listItemId;
    private String tableRowId;
    private String index;
    private String checked;
    private String columns;
}
