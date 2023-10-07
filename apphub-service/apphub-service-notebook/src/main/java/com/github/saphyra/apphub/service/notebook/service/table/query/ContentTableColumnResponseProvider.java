package com.github.saphyra.apphub.service.notebook.service.table.query;

import com.github.saphyra.apphub.api.notebook.model.response.TableColumnResponse;
import com.github.saphyra.apphub.service.notebook.dao.content.ContentDao;
import com.github.saphyra.apphub.api.notebook.model.ColumnType;
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
public class ContentTableColumnResponseProvider implements TableColumnResponseProvider<String> {
    private final TableJoinDao tableJoinDao;
    private final ContentDao contentDao;

    @Override
    public List<TableColumnResponse<String>> fetchTableColumns(UUID listItemId) {
        return tableJoinDao.getByParent(listItemId)
            .stream()
            .map(tableJoin -> TableColumnResponse.<String>builder()
                .tableJoinId(tableJoin.getTableJoinId())
                .rowIndex(tableJoin.getRowIndex())
                .columnIndex(tableJoin.getColumnIndex())
                .content(contentDao.findByParentValidated(tableJoin.getTableJoinId()).getContent())
                .type(ColumnType.TEXT.name())
                .build()
            )
            .collect(Collectors.toList());
    }
}
