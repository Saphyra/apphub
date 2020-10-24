package com.github.saphyra.apphub.service.notebook.dao.table.row;

import com.github.saphyra.apphub.lib.common_util.UuidConverter;
import com.github.saphyra.converter.ConverterBase;
import com.github.saphyra.encryption.impl.BooleanEncryptor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
class ChecklistTableRowConverter extends ConverterBase<ChecklistTableRowEntity, ChecklistTableRow> {
    private final UuidConverter uuidConverter;
    private final BooleanEncryptor booleanEncryptor;

    @Override
    protected ChecklistTableRow processEntityConversion(ChecklistTableRowEntity entity) {
        return ChecklistTableRow.builder()
            .rowId(uuidConverter.convertEntity(entity.getRowId()))
            .userId(uuidConverter.convertEntity(entity.getUserId()))
            .parent(uuidConverter.convertEntity(entity.getParent()))
            .rowIndex(entity.getRowIndex())
            .checked(booleanEncryptor.decryptEntity(entity.getChecked(), entity.getUserId()))
            .build();
    }

    @Override
    protected ChecklistTableRowEntity processDomainConversion(ChecklistTableRow domain) {
        String userId = uuidConverter.convertDomain(domain.getUserId());
        return ChecklistTableRowEntity.builder()
            .rowId(uuidConverter.convertDomain(domain.getRowId()))
            .userId(userId)
            .parent(uuidConverter.convertDomain(domain.getParent()))
            .rowIndex(domain.getRowIndex())
            .checked(booleanEncryptor.encryptEntity(domain.isChecked(), userId))
            .build();
    }
}
