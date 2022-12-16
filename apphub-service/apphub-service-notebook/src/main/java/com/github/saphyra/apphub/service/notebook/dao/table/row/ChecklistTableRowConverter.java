package com.github.saphyra.apphub.service.notebook.dao.table.row;

import com.github.saphyra.apphub.lib.common_util.converter.ConverterBase;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.encryption.impl.BooleanEncryptor;
import com.github.saphyra.apphub.lib.security.access_token.AccessTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
class ChecklistTableRowConverter extends ConverterBase<ChecklistTableRowEntity, ChecklistTableRow> {
    private final UuidConverter uuidConverter;
    private final BooleanEncryptor booleanEncryptor;
    private final AccessTokenProvider accessTokenProvider;

    @Override
    protected ChecklistTableRow processEntityConversion(ChecklistTableRowEntity entity) {
        String userId = accessTokenProvider.getUidAsString();
        return ChecklistTableRow.builder()
            .rowId(uuidConverter.convertEntity(entity.getRowId()))
            .userId(uuidConverter.convertEntity(entity.getUserId()))
            .parent(uuidConverter.convertEntity(entity.getParent()))
            .rowIndex(entity.getRowIndex())
            .checked(booleanEncryptor.decryptEntity(entity.getChecked(), userId))
            .build();
    }

    @Override
    protected ChecklistTableRowEntity processDomainConversion(ChecklistTableRow domain) {
        String userId = accessTokenProvider.getUidAsString();
        return ChecklistTableRowEntity.builder()
            .rowId(uuidConverter.convertDomain(domain.getRowId()))
            .userId(uuidConverter.convertDomain(domain.getUserId()))
            .parent(uuidConverter.convertDomain(domain.getParent()))
            .rowIndex(domain.getRowIndex())
            .checked(booleanEncryptor.encryptEntity(domain.isChecked(), userId))
            .build();
    }
}
