package com.github.saphyra.apphub.service.notebook.service.table.validator;

import com.github.saphyra.apphub.api.notebook.model.table.TableHeadModel;
import com.github.saphyra.apphub.lib.common_util.ValidationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
class CreateTableHeadValidator {
    void validateTableHeads(List<TableHeadModel> tableHeads) {
        ValidationUtil.notNull(tableHeads, "tableHeads");

        tableHeads.forEach(this::validateTableHead);
    }

    private void validateTableHead(TableHeadModel tableHeadModel) {
        ValidationUtil.notNull(tableHeadModel.getColumnIndex(), "tableHead.columnIndex");
        ValidationUtil.notBlank(tableHeadModel.getContent(), "tableHead.content");
    }
}
