package com.github.saphyra.apphub.service.notebook.dao.list_item;

import com.github.saphyra.apphub.lib.common_util.converter.ConverterBase;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.encryption.impl.StringEncryptor;
import com.github.saphyra.apphub.lib.security.access_token.AccessTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
@Slf4j
class ListItemConverter extends ConverterBase<ListItemEntity, ListItem> {
    private final AccessTokenProvider accessTokenProvider;
    private final UuidConverter uuidConverter;
    private final StringEncryptor stringEncryptor;

    @Override
    protected ListItem processEntityConversion(ListItemEntity entity) {
        return ListItem.builder()
            .listItemId(uuidConverter.convertEntity(entity.getListItemId()))
            .userId(uuidConverter.convertEntity(entity.getUserId()))
            .parent(uuidConverter.convertEntity(entity.getParent()))
            .type(entity.getType())
            .title(stringEncryptor.decryptEntity(entity.getTitle(), uuidConverter.convertDomain(accessTokenProvider.get().getUserId())))
            .build();
    }

    @Override
    protected ListItemEntity processDomainConversion(ListItem domain) {
        return ListItemEntity.builder()
            .listItemId(uuidConverter.convertDomain(domain.getListItemId()))
            .userId(uuidConverter.convertDomain(domain.getUserId()))
            .parent(uuidConverter.convertDomain(domain.getParent()))
            .type(domain.getType())
            .title(stringEncryptor.encryptEntity(domain.getTitle(), uuidConverter.convertDomain(accessTokenProvider.get().getUserId())))
            .build();
    }
}
