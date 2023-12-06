package com.github.saphyra.apphub.service.notebook.service.table.query;

import com.github.saphyra.apphub.api.notebook.model.ItemType;
import com.github.saphyra.apphub.api.notebook.model.table.TableHeadModel;
import com.github.saphyra.apphub.service.notebook.dao.content.ContentDao;
import com.github.saphyra.apphub.service.notebook.dao.table_head.TableHeadDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
class TableHeadQueryService {
    private final TableHeadDao tableHeadDao;
    private final ContentDao contentDao;

    List<TableHeadModel> getTableHeads(UUID listItemId) {
        return tableHeadDao.getByParent(listItemId)
            .stream()
            .map(tableHead -> TableHeadModel.builder()
                .tableHeadId(tableHead.getTableHeadId())
                .columnIndex(tableHead.getColumnIndex())
                .content(contentDao.findByParentValidated(tableHead.getTableHeadId()).getContent())
                .type(ItemType.EXISTING)
                .build())
            .collect(Collectors.toList());
    }
}
