package com.github.saphyra.apphub.service.notebook.dao.list_item.table.head;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Data
@Builder
class TableHeadEntity {
    private String listItemId;
    private String data;
}
