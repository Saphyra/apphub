package com.github.saphyra.apphub.service.notebook.dao.checklist_item;

import com.github.saphyra.apphub.lib.common_util.ForRemoval;
import com.github.saphyra.apphub.lib.common_util.converter.ConverterBase;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.encryption.impl.BooleanEncryptor;
import com.github.saphyra.apphub.lib.encryption.impl.IntegerEncryptor;
import com.github.saphyra.apphub.lib.security.access_token.AccessTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@ForRemoval("notebook-redesign")
class ChecklistItemConverter extends ConverterBase<ChecklistItemEntity, ChecklistItem> {
    private final UuidConverter uuidConverter;
    private final AccessTokenProvider accessTokenProvider;
    private final IntegerEncryptor integerEncryptor;
    private final BooleanEncryptor booleanEncryptor;

    @Override
    protected ChecklistItem processEntityConversion(ChecklistItemEntity entity) {
        String userId = uuidConverter.convertDomain(accessTokenProvider.get().getUserId());
        return ChecklistItem.builder()
            .checklistItemId(uuidConverter.convertEntity(entity.getChecklistItemId()))
            .userId(uuidConverter.convertEntity(entity.getUserId()))
            .parent(uuidConverter.convertEntity(entity.getParent()))
            .order(integerEncryptor.decryptEntity(entity.getOrder(), userId))
            .checked(booleanEncryptor.decryptEntity(entity.getChecked(), userId))
            .build();
    }

    @Override
    protected ChecklistItemEntity processDomainConversion(ChecklistItem domain) {
        String userId = uuidConverter.convertDomain(accessTokenProvider.get().getUserId());
        return ChecklistItemEntity.builder()
            .checklistItemId(uuidConverter.convertDomain(domain.getChecklistItemId()))
            .userId(uuidConverter.convertDomain(domain.getUserId()))
            .parent(uuidConverter.convertDomain(domain.getParent()))
            .order(integerEncryptor.encryptEntity(domain.getOrder(), userId))
            .checked(booleanEncryptor.encryptEntity(domain.getChecked(), userId))
            .build();
    }
}
