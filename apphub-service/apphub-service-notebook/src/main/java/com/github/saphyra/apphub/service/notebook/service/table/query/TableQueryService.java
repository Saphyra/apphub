package com.github.saphyra.apphub.service.notebook.service.table.query;

import com.github.saphyra.apphub.api.notebook.model.ListItemType;
import com.github.saphyra.apphub.api.notebook.model.table.TableResponse;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItem;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class TableQueryService {
    private static final List<ListItemType> TABLE_TYPES = List.of(ListItemType.TABLE, ListItemType.CHECKLIST_TABLE, ListItemType.CUSTOM_TABLE);

    private final ListItemDao listItemDao;
    private final TableHeadQueryService tableHeadQueryService;
    private final TableRowQueryService tableRowQueryService;

    public TableResponse getTable(UUID listItemId) {
        ListItem listItem = listItemDao.findByIdValidated(listItemId);

        if (!TABLE_TYPES.contains(listItem.getType())) {
            throw ExceptionFactory.notLoggedException(HttpStatus.CONFLICT, ErrorCode.LIST_ITEM_NOT_FOUND, listItemId + " is not a kind of table, it is " + listItem.getType());
        }

        return TableResponse.builder()
            .title(listItem.getTitle())
            .parent(listItem.getParent())
            .tableHeads(tableHeadQueryService.getTableHeads(listItemId))
            .rows(tableRowQueryService.getRows(listItemId))
            .build();
    }
}
