package com.github.saphyra.apphub.service.notebook.service.table.validator;

import com.github.saphyra.apphub.api.feature.notebook.model.ItemType;
import com.github.saphyra.apphub.api.feature.notebook.model.table.TableHeadModel;
import com.github.saphyra.apphub.lib.common_util.ValidationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
class EditTableHeadValidator {
    void validateTableHeads(List<TableHeadModel> tableHeads) {
        ValidationUtil.notNull(tableHeads, "tableHeads");

        tableHeads.forEach(this::validateTableHead);
    }

    private void validateTableHead(TableHeadModel model) {
        ValidationUtil.notNull(model.getColumnIndex(), "tableHead.columnIndex");
        ValidationUtil.notBlank(model.getContent(), "tableHead.content");
        ValidationUtil.notNull(model.getType(), "tableHead.type");

        if (model.getType() == ItemType.EXISTING) {
            ValidationUtil.notNull(model.getTableHeadId(), "tableHead.tableHeadId");
        }
    }
}
