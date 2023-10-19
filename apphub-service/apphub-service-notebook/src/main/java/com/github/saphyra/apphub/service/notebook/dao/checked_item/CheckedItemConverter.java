package com.github.saphyra.apphub.service.notebook.dao.checked_item;

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
class CheckedItemConverter extends ConverterBase<CheckedItemEntity, CheckedItem> {
    private final BooleanEncryptor booleanEncryptor;
    private final UuidConverter uuidConverter;
    private final AccessTokenProvider accessTokenProvider;

    @Override
    protected CheckedItemEntity processDomainConversion(CheckedItem domain) {
        return CheckedItemEntity.builder()
            .checkedItemId(uuidConverter.convertDomain(domain.getCheckedItemId()))
            .userId(uuidConverter.convertDomain(domain.getUserId()))
            .checked(booleanEncryptor.encryptEntity(domain.getChecked(), accessTokenProvider.getUserIdAsString()))
            .build();
    }

    @Override
    protected CheckedItem processEntityConversion(CheckedItemEntity entity) {
        return CheckedItem.builder()
            .checkedItemId(uuidConverter.convertEntity(entity.getCheckedItemId()))
            .userId(uuidConverter.convertEntity(entity.getUserId()))
            .checked(booleanEncryptor.decryptEntity(entity.getChecked(), accessTokenProvider.getUserIdAsString()))
            .build();
    }
}
