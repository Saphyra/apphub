package com.github.saphyra.apphub.service.notebook.service.table.validator;

import com.github.saphyra.apphub.api.notebook.model.ItemType;
import com.github.saphyra.apphub.api.notebook.model.table.TableHeadModel;
import com.github.saphyra.apphub.lib.common_util.ValidationUtil;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.notebook.dao.table_head.TableHead;
import com.github.saphyra.apphub.service.notebook.dao.table_head.TableHeadDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
class EditTableHeadValidator {
    private final TableHeadDao tableHeadDao;

    void validateTableHeads(UUID listItemId, List<TableHeadModel> tableHeads) {
        ValidationUtil.notNull(tableHeads, "tableHeads");

        tableHeads.forEach(tableHead -> validateTableHead(listItemId, tableHead));
    }

    private void validateTableHead(UUID listItemId, TableHeadModel model) {
        ValidationUtil.notNull(model.getColumnIndex(), "tableHead.columnIndex");
        ValidationUtil.notBlank(model.getContent(), "tableHead.content");
        ValidationUtil.notNull(model.getType(), "tableHead.type");

        if (model.getType() == ItemType.EXISTING) {
            ValidationUtil.notNull(model.getTableHeadId(), "tableHead.tableHeadId");

            TableHead tableHead = tableHeadDao.findByIdValidated(model.getTableHeadId());
            if (!tableHead.getParent().equals(listItemId)) {
                throw ExceptionFactory.invalidParam("tableHead.tableHeadId", "points to different table");
            }
        }
    }
}
