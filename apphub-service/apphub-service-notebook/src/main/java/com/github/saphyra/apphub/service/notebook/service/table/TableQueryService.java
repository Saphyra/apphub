package com.github.saphyra.apphub.service.notebook.service.table;

import com.github.saphyra.apphub.api.notebook.model.response.TableColumnResponse;
import com.github.saphyra.apphub.api.notebook.model.response.TableHeadResponse;
import com.github.saphyra.apphub.api.notebook.model.response.TableResponse;
import com.github.saphyra.apphub.service.notebook.dao.content.ContentDao;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItem;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDao;
import com.github.saphyra.apphub.service.notebook.dao.table.head.TableHeadDao;
import com.github.saphyra.apphub.service.notebook.dao.table.join.TableJoinDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class TableQueryService {
    private final ListItemDao listItemDao;
    private final TableJoinDao tableJoinDao;
    private final TableHeadDao tableHeadDao;
    private final ContentDao contentDao;

    public TableResponse getTable(UUID listItemId) {
        ListItem listItem = listItemDao.findByIdValidated(listItemId);

        return TableResponse.builder()
            .title(listItem.getTitle())
            .tableHeads(fetchTableHeads(listItemId))
            .tableColumns(fetchTableColumns(listItemId))
            .build();
    }

    private List<TableHeadResponse> fetchTableHeads(UUID listItemId) {
        return tableHeadDao.getByParent(listItemId)
            .stream()
            .map(tableHead -> TableHeadResponse.builder()
                .tableHeadId(tableHead.getTableHeadId())
                .columnIndex(tableHead.getColumnIndex())
                .content(contentDao.findByParentValidated(tableHead.getTableHeadId()).getContent())
                .build()
            )
            .collect(Collectors.toList());
    }

    private List<TableColumnResponse> fetchTableColumns(UUID listItemId) {
        return tableJoinDao.getByParent(listItemId)
            .stream()
            .map(tableJoin -> TableColumnResponse.builder()
                .tableJoinId(tableJoin.getTableJoinId())
                .rowIndex(tableJoin.getRowIndex())
                .columnIndex(tableJoin.getColumnIndex())
                .content(contentDao.findByParentValidated(tableJoin.getTableJoinId()).getContent())
                .build()
            )
            .collect(Collectors.toList());
    }
}
